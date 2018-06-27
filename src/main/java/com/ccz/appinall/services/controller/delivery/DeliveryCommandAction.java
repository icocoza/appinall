package com.ccz.appinall.services.controller.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.common.rdb.DbTransaction;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.address.Location;
import com.ccz.appinall.services.controller.address.RecDataAddr;
import com.ccz.appinall.services.controller.address.RecDataAddr.*;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.enums.EGoodsSize;
import com.ccz.appinall.services.enums.EGoodsType;
import com.ccz.appinall.services.enums.EGoodsWeight;
import com.ccz.appinall.services.enums.EUserType;
import com.ccz.appinall.services.model.db.RecAddress;
import com.ccz.appinall.services.model.db.RecDeliveryApply;
import com.ccz.appinall.services.model.db.RecDeliveryOrder;
import com.ccz.appinall.services.model.db.RecDeliveryPhoto;
import com.ccz.appinall.services.model.db.RecDeliveryStatus;
import com.ccz.appinall.services.model.db.RecFile;
import com.ccz.appinall.services.model.db.RecUser;
import com.ccz.appinall.services.model.redis.QueueDeliveryStatus;
import com.ccz.appinall.services.repository.redis.OrderGeoRepository;
import com.ccz.appinall.services.service.SendMessageManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeliveryCommandAction extends CommonAction {
	final int MAX_LIST_COUNT = 20;
	
	public ResponseData<EAllError> result;
	
	@Autowired
	OrderGeoRepository geoRepository;
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	SendMessageManager sendMessageManager;
	
	public DeliveryCommandAction() {
		super.setCommandFunction(EAllCmd.order_add, doOrderRequest);
		super.setCommandFunction(EAllCmd.order_list, doOrderList);
		super.setCommandFunction(EAllCmd.order_detail, doOrderDetail);
		super.setCommandFunction(EAllCmd.order_search, doOrderSearch);
		super.setCommandFunction(EAllCmd.select_order, doOrderSelectByDeliver);
		super.setCommandFunction(EAllCmd.moving_order, doDeliverMoving);
		super.setCommandFunction(EAllCmd.before_gotcha, doDeliverBeforeGotcha);
		super.setCommandFunction(EAllCmd.gotcha_order, doDeliverGotchaOrder);
		super.setCommandFunction(EAllCmd.delivering_order, doDeliverDeliveringOrder);
		super.setCommandFunction(EAllCmd.before_arrival, doDeliveryBeforeComplete);
		super.setCommandFunction(EAllCmd.arrival_in_order, doDeliverArriveInOrder);
		super.setCommandFunction(EAllCmd.complete_delivery, doDeliveryComplete);
		super.setCommandFunction(EAllCmd.confirm_complete_delivery, doSenderDeliveryConfirm);
		super.setCommandFunction(EAllCmd.sender_cancel_order, doOrderCancelBySender);
		super.setCommandFunction(EAllCmd.deliver_cancel_order, doOrderCancelByDeliver);
		super.setCommandFunction(EAllCmd.deliver_plan, doDeliverPlan);
		super.setCommandFunction(EAllCmd.order_search_byroute, doGetOrderByRoute);

		
//		super.setCommandFunction(EAllCmd.select_deliver, doDeliverSelectBySender);
//		super.setCommandFunction(EAllCmd.cancel_deliver, doDeliverCancelBySender);
//		super.setCommandFunction(EAllCmd.watch_order, doWatchOrderByDeliver);
//		super.setCommandFunction(EAllCmd.checkin_order, doOrderCheckInByDeliver);

	}
		
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderRequest = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderRequest data = new RecDataAddr().new DataOrderRequest(jnode);
		RecAddress from, to;
		if( (from = DbAppManager.getInst().getAddress(data.getScode(), data.getFrom_addrid())) == null)
			return res.setError(EAllError.invalid_from_addressid);
		if( (to = DbAppManager.getInst().getAddress(data.getScode(), data.getTo_addrid()))==null)
			return res.setError(EAllError.invalid_to_addressid);
		if(checkOrderData(res, data).getError() != EAllError.ok)
			return res;
		String orderid = KeyGen.makeKeyWithDate("order");
		
		if(DbAppManager.getInst().addOrder(data.getScode(), orderid, session.getUserId(), data.getFrom_addrid(), data.getTo_addrid(), 
				data.getName(), data.getNotice(), data.getSize(), data.getWeight(), data.getType(), 
				data.getPrice(), data.getBegintime(), data.getEndtime(), data.getPhotourl()) == false)
			return res.setError(EAllError.failed_to_saveorder);
		geoRepository.addLocation(orderid, from.lon, from.lat, to.lon, to.lat);
		
		DbAppManager.getInst().updateFilesEnabled(data.getScode(), data.getFileids(), true);	//업로딩된 파일을 enabled 시킴. enabled=false은 주기적으로 삭제 필요
		List<String> queries = new ArrayList<>();
		for(String fileid : data.getFileids())
			queries.add(DbTransaction.getInst().queryInsertOrderFile(fileid, orderid, session.getUserId(), EUserType.sender));
		if(queries.size()>0) {
			RecFile recFile = DbAppManager.getInst().getFileInfo(data.getScode(), data.getFileids().get(0));
			if(recFile!=null) {
				String thumbUrl = String.format("http://%s:%d/thumb?fileid=%s&scode=%s", recFile.fileserver, servicesConfig.getFileDownPort(), recFile.fileid, data.getScode());
				queries.add(DbTransaction.getInst().queryUpdatePhotoUrl(orderid, thumbUrl));
			}
			DbTransaction.getInst().transactionQuery(data.getScode(), queries);				//orderid 별 업로딩된 fileid 등록. usertype에 따라 구분 가능함
		}
		
		//[TODO] 우선 지정 Deliver가 있을 경우, 들어온 배송을 우선적으로 배정할 수 있도록 함
		session.setUserType(EUserType.sender);
		return res.setParam("orderid", orderid).setError(EAllError.ok);		//param : orderid
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderList = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderList data = new RecDataAddr().new DataOrderList(jnode);
		if(data.getOffset() < 0 || data.getCount() < 1)
			return res.setError(EAllError.invalid_offset_count);
		List<RecDeliveryOrder> orderList = DbAppManager.getInst().getOrderList(data.getScode(), session.getUserId(), data.getOffset(), data.getCount());
		if(orderList.size() < 1)
			return res.setError(EAllError.empty_order_list);
		List<String> orderids = orderList.stream().map(x -> x.orderid).collect(Collectors.toList());
		Map<String, Integer> deliverCountMap = DbAppManager.getInst().getDeliverCountByOrderId(data.getScode(), orderids.toArray(new String[orderids.size()]));
		if(deliverCountMap.size()>0) {
			//orderList.stream().filter(x -> deliverCountMap.containsKey(x.orderid)).map(x->x.deliverCount = deliverCountMap.get(x.orderid).intValue());
			for(RecDeliveryOrder order : orderList) {
				if(deliverCountMap.containsKey(order.orderid))
					order.deliverCount = deliverCountMap.get(order.orderid);
			}
		}
		Set<String> fromids = orderList.stream().map(x -> x.fromid).collect(Collectors.toSet());
		Set<String> toids = orderList.stream().map(x -> x.toid).collect(Collectors.toSet());
		
		fromids.addAll(toids);
		List<String> addrids = fromids.stream().collect(Collectors.toList());
		//List<Document> addrList = AddressMongoDb.getInst().getAddrs(addrids); //주소 데이터 
		List<RecAddress> addrList = DbAppManager.getInst().getAddressList(data.getScode(), addrids);
		//[TODO]진행상태 추가 필요
		session.setUserType(EUserType.sender);
		return res.setParam("data", this.getOrderSearchData(orderList, addrList)).setError(EAllError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderDetail = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderDetail data = new RecDataAddr().new DataOrderDetail(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAllError.no_order_data);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objNode = mapper.valueToTree(order);
		
		List<RecDeliveryApply> delivers = DbAppManager.getInst().getDeliverList(data.getScode(), order.orderid);	//the deliver list whose apply the order
		ArrayNode deliversNode = mapper.valueToTree(delivers);
		objNode.putArray("delivers").addAll(deliversNode);
		
		List<RecDeliveryPhoto> photoList = DbAppManager.getInst().getDeliveryPhotoList(data.getScode(), data.getOrderid());
		ArrayNode senderPhotos = mapper.valueToTree(photoList.stream().filter(x-> x.usertype == EUserType.sender).map(y->y.fileid).collect(Collectors.toList()));
		ArrayNode deliverPhotos = mapper.valueToTree(photoList.stream().filter(x-> x.usertype == EUserType.deliver).map(y->y.fileid).collect(Collectors.toList()));
		objNode.putArray("senderphotos").addAll(senderPhotos);
		objNode.putArray("deliverphotos").addAll(deliverPhotos);
		return res.setParam("data", objNode).setError(EAllError.ok);
	};
	
/*	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverSelectBySender = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
 * DataSelectDeliverBySender data = new RecDataAddr().new DataSelectDeliverBySender(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status != DbRecord.Empty)
			return res.setError(EAllError.already_assigned_order);
		
		if(DbAppManager.getInst().addDeliveryStatus(data.getScode(), data.getOrderid(), data.getDeliverid(), EDeliveryStatus.ready) == false)
			return res.setError(EAllError.failed_assign_deliver);
		
		//[TODO] Send a Push to deliver to let know the deliver be choosed
		DeliveryPushGenerator.sendDeliveryStatus(data.getScode(), session.getUserId(), data.getDeliverid(), order.orderid, EDeliveryStatus.ready, "no msg");
		return res.setError(EAllError.ok);
	};

	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverCancelBySender = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
	DataCancelDeliverBySender data = new RecDataAddr().new DataCancelDeliverBySender(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		if(order.senderid.equals(session.getUserId()) == false)
			return res.setError(EAllError.not_authorized_user);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_assigned_order);
		if(status.status != EDeliveryStatus.ready)
			return res.setError(EAllError.already_starting_order);
		if(DbAppManager.getInst().delDeliveryStatus(data.getScode(), data.getOrderid()) == false)
			return res.setError(EAllError.failed_cancel_delivery_ready);
		
		//[TODO] 이전에 선택된 delivery에게 푸시나 문자 전송해야 함(취소알림)
		DeliveryPushGenerator.sendDeliveryStatus(data.getScode(), session.getUserId(), data.getDeliverid(), order.orderid, EDeliveryStatus.cancel, "no msg");
		return res.setError(EAllError.ok);
	};
*/
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderSearch = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderSearch data = new RecDataAddr().new DataOrderSearch(jnode);
		RecAddress fromAddr, toAddr;
		if( (fromAddr = DbAppManager.getInst().getAddress(data.getScode(), data.getFrom_addrid())) == null)
			return res.setError(EAllError.invalid_from_addressid);
		if( (toAddr = DbAppManager.getInst().getAddress(data.getScode(), data.getTo_addrid()))==null)
			return res.setError(EAllError.invalid_to_addressid);
		
		Map<String, Location> fromMap = geoRepository.searchOrderFrom(fromAddr.lon, fromAddr.lat, 2000, 10, 0);
		Map<String, Location> toMap = geoRepository.searchOrderTo(toAddr.lon, toAddr.lat, 2000, 10, 1);
		List<String> removeKeys = new ArrayList<>();
		
		for(Entry<String, Location> from : fromMap.entrySet()) {
			Location toLoc = toMap.get(from.getKey());
			if(toLoc == null || toLoc.getRouteIndex() < from.getValue().getRouteIndex())
				removeKeys.add(from.getKey());
		}
		removeKeys.stream().forEach(x -> fromMap.remove(x));
		
		if(fromMap.size()<1)
			return res.setError(EAllError.no_search_result);
		
		String[] orderids = fromMap.keySet().toArray(new String[fromMap.size()]);
		List<RecDeliveryOrder> orderList = DbAppManager.getInst().getOrderListByIds(data.getScode(), orderids);

		List<String> addrids = orderList.stream().map(x -> x.getBuildIds()).flatMap(Collection::stream).collect(Collectors.toList()); 
		List<RecAddress> addrList = DbAppManager.getInst().getAddressList(data.getScode(), addrids);
		session.setUserType(EUserType.deliver);
		return res.setParam("data", this.getOrderSearchData(orderList, addrList)).setError(EAllError.ok);
	};
	
	//Deliver가 Order 선택 
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderSelectByDeliver = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderSelectByDeliver data = new RecDataAddr().new DataOrderSelectByDeliver(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		//if(order.begintime.getTime() < System.currentTimeMillis())		//[TODO] commented for test
		//	return res.setError(EAllError.late_delivery_request);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status != DbRecord.Empty)
			return res.setError(EAllError.already_occupied_order);
		
		RecUser user = DbAppManager.getInst().getUser(data.getScode(), session.getUserId()); //[TODO] commented for test
		if(user == DbRecord.Empty)
			return res.setError(EAllError.not_exist_deliver);
		
		/*if(DbAppManager.getInst().addDeliveryApply(data.getScode(), data.getOrderid(), session.getUserId(), user.username, 
				data.getBegintime(), data.getEndtime(), data.getPrice(), data.getDelivertype(), data.getDeliverytype()) == false)
			return res.setError(EAllError.failed_apply_order); */
		if(DbAppManager.getInst().addDeliveryApply(data.getScode(), data.getOrderid(), session.getUserId(), user.username) == false)
			return res.setError(EAllError.failed_apply_order);
		
		if(DbAppManager.getInst().addDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.assign) == false)
			return res.setError(EAllError.failed_assign_deliver);
		
		sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.assign, "Selected");
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};

/*	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderCheckInByDeliver = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
 * DataOrderCheckInByDelivers data = new RecDataAddr().new DataOrderCheckInByDelivers(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status == EDeliveryStatus.assign)
			return res.setError(EAllError.already_assigned_order);
		//Deliver 선택과 동시에 할당되도록 정책 변경 
//		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.assign) == false)
//			return res.setError(EAllError.failed_to_saveassign);
		
		//[TODO] Seder 에게 푸시나 메시지 전송해야 함 
		DeliveryPushGenerator.sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.assign, "no msg");
		return res.setError(EAllError.ok);
	};*/

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverMoving = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliverMoving data = new RecDataAddr().new DataDeliverMoving(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.assign)
			return res.setError(EAllError.not_assigned_order);
		
		String randomCode = "" + (new Random().nextInt(99999-10000)+10000);
		if(DbAppManager.getInst().updateDeliveryStartCode(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.start, randomCode) == false)
			return res.setError(EAllError.failed_to_savestartmoving);
		
		sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.start, "Moving! Gotcha Code: " + randomCode);
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverBeforeGotcha = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliverBeforeGotcha data = new RecDataAddr().new DataDeliverBeforeGotcha(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.start)
			return res.setError(EAllError.not_start_order);
		
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.gotcha5min)==false)
			return res.setError(EAllError.failed_to_savebeforegotcha);
		
		sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.gotcha5min, "Before Gotcha");
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverGotchaOrder = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliverGotcha data = new RecDataAddr().new DataDeliverGotcha(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		
		if(status.status == EDeliveryStatus.start || status.status == EDeliveryStatus.gotcha5min) {
			/*
			if(status.startcode!=null && status.startcode.equals(data.getStartcode())==false)	//[TODO] start code가 있을 경우에만 체크. check what start code is mandatory or not
				return res.setError(EAllError.invalid_start_passcode);
			*/
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.gotcha)==false)
				return res.setError(EAllError.failed_to_savegotcha);
			
			sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.gotcha, "Gotcha Order");
			session.setUserType(EUserType.deliver);
			return res.setError(EAllError.ok);
		}
		return res.setError(EAllError.not_started_order);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverDeliveringOrder = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliverDelivering data = new RecDataAddr().new DataDeliverDelivering(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.gotcha)
			return res.setError(EAllError.not_receipt_order);
		
		String randomCode = "" + (new Random().nextInt(99999-10000)+10000);
		if(DbAppManager.getInst().updateDeliveryEndCode(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.delivering, randomCode)==false)
			return res.setError(EAllError.failed_to_savedelivering);
		
		sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.delivering, "Delivering Order! Take Code: " + randomCode);
		
		//[TODO] Send Message to Receiver
		//sendDeliveryStatus()
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliveryBeforeComplete = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliveryBeforeComplete data = new RecDataAddr().new DataDeliveryBeforeComplete(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.delivering)
			return res.setError(EAllError.not_delivering_order);
		
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.before_delivered)==false)
			return res.setError(EAllError.failed_to_savebeforedelivered);
		
		sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.before_delivered, "Almost arrived the deliver.");
		
		//[TODO] Send Message to Receiver
		//sendDeliveryStatus()
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverArriveInOrder = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataArrivalInOrder data = new RecDataAddr().new DataArrivalInOrder(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		
		if(status.status == EDeliveryStatus.delivering || status.status == EDeliveryStatus.before_delivered) {
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.deliver_arrived)==false)
				return res.setError(EAllError.failed_to_savebeforedelivered);
			
			sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.deliver_arrived, "Arrived In Receiver.");
			//[TODO] Send Message to Receiver
			//sendDeliveryStatus()
			session.setUserType(EUserType.deliver);
			return res.setError(EAllError.ok);
		}
		return res.setError(EAllError.not_delivering_order);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliveryComplete = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliveryCompleteByDelivers data = new RecDataAddr().new DataDeliveryCompleteByDelivers(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.deliver_arrived)	//delivered 상태에서 passcode를 올바르게 입력하여 confirm 상태로 변경할 수 있어야 함.
			return res.setError(EAllError.not_arrived_order);
		
		if(data.getFileids().size()>0)
			DbAppManager.getInst().updateFilesEnabled(data.getScode(), data.getFileids(), true);	//업로딩된 파일을 enabled 시킴. enabled=false은 주기적으로 삭제 필요
		List<String> queries = new ArrayList<>();
		for(String fileid : data.getFileids())
			queries.add(DbTransaction.getInst().queryInsertOrderFile(fileid, data.getOrderid(), session.getUserId(), EUserType.deliver));
		if(queries.size()>0)
			DbTransaction.getInst().transactionQuery(data.getScode(), queries);				//orderid 별 업로딩된 fileid 등록. usertype에 따라 구분 가능함
		
		if(status.endcode.equals(data.getEndcode())==false) {
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.delivered)==false)
				return res.setError(EAllError.failed_to_savedelivered);
			else
				sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.delivered, "Delivery Finished.");
		}else {
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), session.getUserId(), EDeliveryStatus.confirm)==false)
				return res.setError(EAllError.failed_to_saveconfirm);
			else
				sendDeliveryStatus(data.getScode(), session.getUserId(), order.senderid, order.orderid, EDeliveryStatus.confirm, "Delivery Completed.");
		}
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doSenderDeliveryConfirm = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliveryConfirmBySender data = new RecDataAddr().new DataDeliveryConfirmBySender(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAllError.no_order_data);
		if(order.senderid.equals(session.getUserId())==false)
			return res.setError(EAllError.no_permission);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_allowed_order);
		if(status.status != EDeliveryStatus.delivered)
			return res.setError(EAllError.not_delivered_order);
		
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getDeliverid(), EDeliveryStatus.confirm)==false)
			return res.setError(EAllError.failed_to_saveconfirm);
			
		//[TODO] PUSH MESSAGE TO DELIVER
		sendDeliveryStatus(data.getScode(), session.getUserId(), data.getDeliverid(), order.orderid, EDeliveryStatus.confirm, "Confirm delivery.");
		//[TODO] Save to db message for history
		session.setUserType(EUserType.sender);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doWatchOrderByDeliver = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderDetailByDelivers data = new RecDataAddr().new DataOrderDetailByDelivers(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAllError.no_order_data);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.valueToTree(order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			node.put("status", EDeliveryStatus.none.getValue());
		else
			node.put("status", status.status.getValue());
		return res.setParam("data", node).setError(EAllError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderCancelBySender = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderCancelBySender data = new RecDataAddr().new DataOrderCancelBySender(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAllError.no_order_data);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty) {
			if(DbAppManager.getInst().updateOrderDisabled(data.getScode(), data.getOrderid()) == false)
				return res.setError(EAllError.failed_to_updateordercancel);
			return res.setError(EAllError.ok);
		}
		if(status.status != EDeliveryStatus.assign)
			return res.setError(EAllError.impossible_cancel_delivery);
		
//		if(DbAppManager.getInst().delDeliveryStatus(data.getScode(), data.getOrderid()) == false)
//			return res.setError(EAllError.failed_cancel_delivery_ready);	
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), status.deliverid, EDeliveryStatus.cancel_bysender)==false)
			return res.setError(EAllError.failed_to_cancelbysender);
		if(DbAppManager.getInst().updateOrderDisabled(data.getScode(), data.getOrderid()) == false)
			return res.setError(EAllError.failed_to_updateordercancel);
		sendDeliveryStatus(data.getScode(), order.senderid, status.deliverid, order.orderid, EDeliveryStatus.cancel_bysender, "Order Cancel by Sender.");
		session.setUserType(EUserType.sender);
		return res.setError(EAllError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doOrderCancelByDeliver = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderCancelByDelivers data = new RecDataAddr().new DataOrderCancelByDelivers(jnode);
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAllError.no_order_data);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			return res.setError(EAllError.not_assigned_order);
		if(status.status != EDeliveryStatus.assign)
			return res.setError(EAllError.impossible_cancel_delivery);
		
		//[TODO] 진행중인 상태의 Delivery를 배송자가 취소할 시나리오 필요 
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), status.deliverid, EDeliveryStatus.cancel_bydeliver)==false)
			return res.setError(EAllError.failed_to_cancelbysender);
		if(DbAppManager.getInst().updateOrderDisabled(data.getScode(), data.getOrderid()) == false)
			return res.setError(EAllError.failed_to_updateordercancel);
		sendDeliveryStatus(data.getScode(), status.deliverid, order.senderid, order.orderid, EDeliveryStatus.cancel_bysender, "Order Cancel by Deliver.");
		session.setUserType(EUserType.deliver);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDeliverPlan = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataDeliverPlan data = new RecDataAddr().new DataDeliverPlan(jnode);
		return res;
	};

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doGetOrderByRoute = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataOrderByRoute data = new RecDataAddr().new DataOrderByRoute(jnode);;
		if(data.getRouteList()==null || data.getRouteList().size()<1) 
			return res.setError(EAllError.empty_gpslist);
		Point[] routeArray = data.getRouteArray();
		List<Point> orderSearchPinList = new ArrayList<>();
		Point checkPoint = routeArray[0];
		double distance = servicesConfig.getGeoSearchNext() * 0.0075;	//re-calculate distance
		
		//[TODO] 가까운 거리의 배송물에 대해 검색되지 않음. 수정 필요
		
		for(int i=1; i<routeArray.length; i++) {
			if(Math.hypot(routeArray[i].getX() - checkPoint.getX(), routeArray[i].getY() - checkPoint.getY()) >= distance) {
				checkPoint = routeArray[i];
				orderSearchPinList.add(routeArray[i]);
			}
		}
		
		//1st filtering by orderid(duplicated for multiple georadius)
		int radius = 0;
		Map<String, Location> fromMap = new HashMap<>(), toMap = new HashMap<>();
		for(int i=0; i<orderSearchPinList.size(); i++) {
			Point pin = orderSearchPinList.get(i);
			radius = (i==0 || i==orderSearchPinList.size()-1) ? servicesConfig.getGeoSearchFirst()*1000 : servicesConfig.getGeoSearchNext()*1000;	//처음과 마지막은 반경 2km, 그외엔 1km 
			fromMap.putAll(geoRepository.searchOrderFrom(pin.getX(), pin.getY(), radius, 100, i));	
			toMap.putAll(geoRepository.searchOrderTo(pin.getX(), pin.getY(), radius, 100, i));
		}
		
		//2nd filtering by direction using Location::getRouteIndex. the index of to must bigger than the index of from
		for(Entry<String, Location> from : fromMap.entrySet()) {
			Location toLoc = toMap.get(from.getKey());
			if(toLoc == null || toLoc.getRouteIndex() < from.getValue().getRouteIndex())
				fromMap.remove(from.getKey());
		}
		
		if(fromMap.size()<1)
			return res.setError(EAllError.no_search_result);
		
		String[] orderids = fromMap.keySet().toArray(new String[fromMap.size()]);
		List<RecDeliveryOrder> orderList = DbAppManager.getInst().getOrderListByIds(data.getScode(), orderids);

		List<String> addrids = orderList.stream().map(x -> x.getBuildIds()).flatMap(Collection::stream).collect(Collectors.toList()); 
		List<RecAddress> addrList = DbAppManager.getInst().getAddressList(data.getScode(), addrids);
		
		DbAppManager.getInst().addRouteHistory(data.getScode(), session.getUserId(), data.getRouteList(), orderList.size());
		
		return res.setParam("data", this.getOrderSearchData(orderList, addrList)).setError(EAllError.ok);
	};
	
	private ArrayNode copySearshResultToResponse(ArrayNode arrNode) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode copyArrNode = mapper.createArrayNode();
		
		for(int i=0; i<arrNode.size(); i++) {
			JsonNode srcNode = arrNode.get(i).get("_source");
			if(srcNode == NullNode.instance)
				continue;
			copyArrNode.add(srcNode);
		}
		return copyArrNode;
	}
	
	private ResponseData<EAllError> checkOrderData(ResponseData<EAllError> res, DataOrderRequest data) {
		if(data.getName() == null || data.getName().length()<1) return res.setError(EAllError.empty_goods_name);
		if(data.getSize() == EGoodsSize.none) return res.setError(EAllError.empty_goods_size);
		if(data.getWeight() == EGoodsWeight.none) return res.setError(EAllError.empty_goods_weight);
		if(data.getType() == EGoodsType.none) return res.setError(EAllError.empty_goods_type);
		if(data.getPrice() < 1) return res.setError(EAllError.empty_goods_price);
		if(data.getBegintime() < 1) return res.setError(EAllError.empty_order_begintime);
		if(data.getEndtime() < 1) return res.setError(EAllError.empty_order_endtime);
		return res.setError(EAllError.ok); 
	}
	
	private JsonNode getOrderSearchData(List<RecDeliveryOrder> orderList, List<RecAddress> addrList) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode orderNode = mapper.createObjectNode();
		try {
			ArrayNode arrOrder = mapper.valueToTree(orderList);
			ArrayNode arrAddr = mapper.valueToTree(addrList);
//			for(Document doc : addrList) {
//				arrAddr.add(mapper.readTree(doc.toJson()));
//			}
			orderNode.putArray("orderlist").addAll(arrOrder);
			orderNode.putArray("addrlist").addAll(arrAddr);
		}catch(Exception e) {
			
		}
		return orderNode;
	}

	
	private void sendDeliveryStatus(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		QueueDeliveryStatus qds = new DeliveryStatusMsg().makeStatusObject(scode, from, to, orderid, status, msg);
		if(qds != null)
			try {
				sendMessageManager.sendDeliveryStatus(qds);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
	}
}
