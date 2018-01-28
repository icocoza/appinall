package com.ccz.appinall.services.action.address;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.action.CommonAction;
import com.ccz.appinall.services.action.address.RecDataAddr.*;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecDeliveryOrder;
import com.ccz.appinall.services.entity.db.RecDeliveryStatus;
import com.ccz.appinall.services.repository.redis.GeoRepository;
import com.ccz.appinall.services.repository.redis.GeoRepository.Location;
import com.ccz.appinall.services.type.enums.EAddrCmd;
import com.ccz.appinall.services.type.enums.EAddrError;
import com.ccz.appinall.services.type.enums.EDeliveryStatus;
import com.ccz.appinall.services.type.enums.EGoodsSize;
import com.ccz.appinall.services.type.enums.EGoodsType;
import com.ccz.appinall.services.type.enums.EGoodsWeight;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.netty.channel.Channel;

@Service
public class AddressCommandAction extends CommonAction {

	final int MAX_LIST_COUNT = 20;
	
	public ResponseData<EAddrError> result;
	
	@Autowired
	GeoRepository geoRepository;
	
	public AddressCommandAction() {
		super(null);
	}
	public AddressCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return false;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		ResponseData<EAddrError> res = new ResponseData<EAddrError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		switch(EAddrCmd.getType(res.getCommand())) {
		case search:
			res = this.doSearch(res, new RecDataAddr().new DataSearchAddr(jdata));
			break;
		case orderrequest:
			res = this.doOrderRequest(res, new RecDataAddr().new DataOrderRequest(jdata));
			break;
		case orderlist:
			res = this.doOrderList(res, new RecDataAddr().new DataOrderList(jdata));
			break;
		case orderdetail:
			res = this.doOrderDetail(res, new RecDataAddr().new DataOrderDetail(jdata));
			break;
		case orderselectdeliver:
			res = this.doDeliverSelectBySender(res, new RecDataAddr().new DataSelectDeliverBySender(jdata));
			break;
		case ordercanceldeliver:
			res = this.doDeliverCancelBySender(res, new RecDataAddr().new DataCancelDeliverBySender(jdata));
			break;
		case deliversearchorder:
			res = this.doOrderSearch(res, new RecDataAddr().new DataOrderSearch(jdata));
			break;
		case deliverselectorder:
			res = this.doOrderSelectByDeliver(res, new RecDataAddr().new DataOrderSelectByDelivers(jdata));
			break;
		case delivercheckinorder:
			res = this.doOrderCheckInByDeliver(res, new RecDataAddr().new DataOrderCheckInByDelivers(jdata));
			break;
		case delivermoving:
			res = this.doDeliverMoving(res, new RecDataAddr().new DataDeliverMoving(jdata));
			break;
		case delivergotcha:
			res = this.doDeliverGotchaOrder(res, new RecDataAddr().new DataDeliverGotcha(jdata));
			break;
		case deliverdelivering:
			res = doDeliverDeliveringOrder(res, new RecDataAddr().new DataDeliverDelivering(jdata));
			break;
		case deliverdeliverycomplete:
			res = doDeliverDeliveryComplete(res, new RecDataAddr().new DataDeliveryCompleteByDelivers(jdata));
			break;
		case senderdeliveryconfirm:
			res = doDeliverDeliveryConfirm(res, new RecDataAddr().new DataDeliveryConfirmBySender(jdata));
			break;
		case watchorder:
			res = this.doWatchOrderByDeliver(res, new RecDataAddr().new DataOrderDetailByDelivers(jdata));
			break;
		case ordercancel:
			res = this.doOrderCancelByDeliver(res, new RecDataAddr().new DataOrderCancelByDelivers(jdata));
			break;
		case deliverplan:
			res = this.doDeliverPlan(res, new RecDataAddr().new DataDeliverPlan(jdata));
			break;
		default:
			return false;
		}
		result = res;
		if(res != null && ch != null) 
			send(ch, res.toString());
		return true;
	}

	/*
	 * 1. fail 된 검색어를 별도로 모아두어야 함
	 * 2. 한 사용자에 의해 반복적으로 검색어가 들어 올 경우 모아두어야 함 
	 * */
	private ResponseData<EAddrError> doSearch(ResponseData<EAddrError> res, DataSearchAddr data) {
		JsonNode jsonNode =  null;
		AddressInference ai = new AddressInference(data.getSearch());
		try {
			jsonNode = AddressElasticSearch.getInst().searchAddresByRestJson(ai);
		} catch (Exception e) {
			e.printStackTrace();
			return res.setError(EAddrError.failed_search);
		}
		if(jsonNode == NullNode.instance)
			return res.setError(EAddrError.invalid_search);
		JsonNode hitsNode = jsonNode.get("hits");
		if(hitsNode == null )
			return res.setError(EAddrError.empty_search);
		JsonNode totalNode = hitsNode.get("total");
		if(totalNode==null || totalNode.asInt() == 0)
			return res.setError(EAddrError.no_search_result);
		
		ArrayNode arrNode = copySearshResultToResponse((ArrayNode) hitsNode.get("hits"));
		return res.setData(arrNode).setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doOrderRequest(ResponseData<EAddrError> res, DataOrderRequest data) {
		Document from, to;
		if( (from=AddressMongoDb.getInst().getAddr(data.getFrom_addrid()))==null)
			return res.setError(EAddrError.invalid_from_addressid);
		if( (to=AddressMongoDb.getInst().getAddr(data.getTo_addrid()))==null)
			return res.setError(EAddrError.invalid_to_addressid);
		if(checkOrderData(res, data).getError() != EAddrError.ok)
			return res;
		String orderid = KeyGen.makeKeyWithDate("order");
		
		if(DbAppManager.getInst().addOrder(data.getScode(), orderid, data.getUserid(), data.getFrom_addrid(), data.getTo_addrid(), 
				data.getName(), data.getNotice(), data.getSize(), data.getWeight(), data.getType(), 
				data.getPrice(), data.getBegintime(), data.getEndtime(), data.getPhotourl()) == false)
			return res.setError(EAddrError.failed_to_saveorder);
		
		//geoRepository.addStartLocation(orderid, from.getDouble("lon"), from.getDouble("lac"));
		//geoRepository.addEndLocation(orderid, to.getDouble("lon"), to.getDouble("lac"));
		geoRepository.addLocation(orderid, from.getDouble("lon"), from.getDouble("lac"), to.getDouble("lon"), to.getDouble("lac"));
		
		return res.setParam(orderid).setError(EAddrError.ok);		//param : orderid
	}
	
	private ResponseData<EAddrError> doOrderList(ResponseData<EAddrError> res, DataOrderList data) {
		if(data.getOffset() < 0 || data.getCount() < 1)
			return res.setError(EAddrError.invalid_offset_count);
		List<RecDeliveryOrder> orderList = DbAppManager.getInst().getOrderList(data.getScode(), data.getUserid(), data.getOffset(), data.getCount());
		if(orderList.size() < 1)
			return res.setError(EAddrError.empty_order_list);
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.valueToTree(orderList);
		return res.setData(arrayNode).setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doOrderDetail(ResponseData<EAddrError> res, DataOrderDetail data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAddrError.no_order_data);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jnode = mapper.valueToTree(order);
		
		//[TODO] 배송 전달 요청자 리스트 전달해야 함 
		return res.setData(jnode).setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doDeliverSelectBySender(ResponseData<EAddrError> res, DataSelectDeliverBySender data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAddrError.not_exist_order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status != DbRecord.Empty)
			return res.setError(EAddrError.already_assigned_order);
		if(DbAppManager.getInst().addDeliveryStatus(data.getScode(), data.getOrderid(), data.getDeliverid(), EDeliveryStatus.ready) == false)
			return res.setError(EAddrError.failed_assign_deliver);
		return res.setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doDeliverCancelBySender(ResponseData<EAddrError> res, DataCancelDeliverBySender data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAddrError.not_exist_order);
		if(order.senderid.equals(data.getUserid()) == false)
			return res.setError(EAddrError.not_authorized_user);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_assigned_order);
		if(status.status != EDeliveryStatus.ready)
			return res.setError(EAddrError.already_starting_order);
		if(DbAppManager.getInst().delDeliveryStatus(data.getScode(), data.getOrderid()) == false)
			return res.setError(EAddrError.failed_cancel_delivery_ready);
		
		//[TODO] 이전에 선택된 delivery에게 푸시나 문자 전송해야 함(취소알림)
		
		return res.setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doOrderSearch(ResponseData<EAddrError> res, DataOrderSearch data) {
		Document from, to;
		if( (from=AddressMongoDb.getInst().getAddr(data.getFrom_addrid()))==null)
			return res.setError(EAddrError.invalid_from_addressid);
		if( (to=AddressMongoDb.getInst().getAddr(data.getTo_addrid()))==null)
			return res.setError(EAddrError.invalid_to_addressid);
		
		List<Location> fromList = geoRepository.searchOrderStart(from.getDouble("lon"), from.getDouble("lac"), 2000, 10);
		List<Location> toList = geoRepository.searchOrderEnd(to.getDouble("lon"), to.getDouble("lac"), 2000, 10);
		
		Set<String> toOrderIds = toList.stream().map(item->item.getOrderid()).collect(Collectors.toSet());		//to compare orderid contained
		List<String> matchOrderIdList = fromList.stream().filter(fromItem -> toOrderIds.contains(fromItem.getOrderid())).map(x->x.getOrderid()).collect(Collectors.toList());
		
		if(matchOrderIdList.size()<1)
			return res.setError(EAddrError.no_search_result);
		String[] orderids = matchOrderIdList.toArray(new String[matchOrderIdList.size()]);
		List<RecDeliveryOrder> orderList = DbAppManager.getInst().getOrderListByIds(data.getScode(), orderids);

		List<String> buildids = orderList.stream().map(x -> x.getBuildIds()).flatMap(Collection::stream).collect(Collectors.toList()); //insertsect between from and to
		List<Document> addrList = AddressMongoDb.getInst().getAddrs(buildids); //주소 데이터 
		
		return res.setData(this.getOrderSearchData(orderList, addrList)).setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doOrderSelectByDeliver(ResponseData<EAddrError> res, DataOrderSelectByDelivers data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAddrError.not_exist_order);
		if(order.begintime.getTime() < System.currentTimeMillis())
			return res.setError(EAddrError.late_delivery_request);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status != DbRecord.Empty)
			return res.setError(EAddrError.already_occupied_order);
		if(DbAppManager.getInst().addDeliveryApply(data.getScode(), data.getOrderid(), data.getDeliverid(), 
				data.getBegintime(), data.getEndtime(), data.getPrice(), data.getDelivertype(), data.getDeliverytype()) == false)
			return res.setError(EAddrError.failed_apply_order);
		
		//[TODO] order 에게 푸시나 온라인 메시지 보내야 함
		
		return res.setError(EAddrError.ok);
	}
	private ResponseData<EAddrError> doOrderCheckInByDeliver(ResponseData<EAddrError> res, DataOrderCheckInByDelivers data) {
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status == EDeliveryStatus.assign)
			return res.setError(EAddrError.already_assigned_order);
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getDeliverid(), EDeliveryStatus.assign) == false)
			return res.setError(EAddrError.failed_to_saveassign);
		
		//[TODO] Seder 에게 푸시나 메시지 전송해야 함 
		
		return res.setError(EAddrError.ok);
	}

	private ResponseData<EAddrError> doDeliverMoving(ResponseData<EAddrError> res, DataDeliverMoving data) {
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status != EDeliveryStatus.assign)
			return res.setError(EAddrError.not_assigned_order);
		String randomcode = "" + (new Random().nextInt(99999-10000)+10000);
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid(), EDeliveryStatus.start, randomcode) == false)
			return res.setError(EAddrError.failed_to_savestartmoving);
		
		//[TODO] Seder 에게 푸시나 메시지 전송해야 함, randomcode도 같이 전달
		
		
		return res.setError(EAddrError.ok);
	}

	private ResponseData<EAddrError> doDeliverGotchaOrder(ResponseData<EAddrError> res, DataDeliverGotcha data) {
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status != EDeliveryStatus.start)
			return res.setError(EAddrError.not_started_order);
		if(status.startcode.equals(data.getStartcode())==false)
			return res.setError(EAddrError.invalid_start_passcode);
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid(), EDeliveryStatus.gotcha)==false)
			return res.setError(EAddrError.failed_to_savegotcha);
		
		//[TODO] Seder 에게 푸시나 메시지 전송해야 함 
		
		return res.setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doDeliverDeliveringOrder(ResponseData<EAddrError> res, DataDeliverDelivering data) {
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status != EDeliveryStatus.gotcha)
			return res.setError(EAddrError.not_receipt_order);
		String randomcode = "" + (new Random().nextInt(99999-10000)+10000);
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid(), EDeliveryStatus.delivering, randomcode)==false)
			return res.setError(EAddrError.failed_to_savedelivering);
		
		return res.setError(EAddrError.ok);
	}

	private ResponseData<EAddrError> doDeliverDeliveryComplete(ResponseData<EAddrError> res, DataDeliveryCompleteByDelivers data) {
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status != EDeliveryStatus.delivering)
			return res.setError(EAddrError.not_delivering_order);
		
		//[TODO] Upload phto url
		
		if(status.endcode.equals(data.getEndcode())==false) {
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid(), EDeliveryStatus.delivered)==false)
				return res.setError(EAddrError.failed_to_savedelivered);
		}else {
			if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getUserid(), EDeliveryStatus.confirm)==false)
				return res.setError(EAddrError.failed_to_saveconfirm);
		}
			
		
		return res.setError(EAddrError.ok);
	}

	private ResponseData<EAddrError> doDeliverDeliveryConfirm(ResponseData<EAddrError> res, DataDeliveryConfirmBySender data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == DbRecord.Empty)
			return res.setError(EAddrError.no_order_data);
		if(order.senderid.equals(data.getUserid())==false)
			return res.setError(EAddrError.no_permission);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			return res.setError(EAddrError.not_allowed_order);
		if(status.status != EDeliveryStatus.delivered)
			return res.setError(EAddrError.not_delivered_order);
		
		if(DbAppManager.getInst().updateDeliveryStatus(data.getScode(), data.getOrderid(), data.getDeliverid(), EDeliveryStatus.confirm)==false)
			return res.setError(EAddrError.failed_to_saveconfirm);
			
		//[TODO] PUSH MESSAGE TO DELIVER
		//[TODO] Save to db message for history
		return res.setError(EAddrError.ok);
	}

	private ResponseData<EAddrError> doWatchOrderByDeliver(ResponseData<EAddrError> res, DataOrderDetailByDelivers data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAddrError.no_order_data);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.valueToTree(order);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty)
			node.put("status", EDeliveryStatus.none.getValue());
		else
			node.put("status", status.status.getValue());
		return res.setData(node).setError(EAddrError.ok);
	}
	
	private ResponseData<EAddrError> doOrderCancelByDeliver(ResponseData<EAddrError> res, DataOrderCancelByDelivers data) {
		RecDeliveryOrder order = (RecDeliveryOrder) DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order == null)
			return res.setError(EAddrError.no_order_data);
		RecDeliveryStatus status =  DbAppManager.getInst().getDeliveryStatus(data.getScode(), data.getOrderid());
		if(status == DbRecord.Empty || status.status == EDeliveryStatus.ready)
			return res.setError(EAddrError.not_assigned_order);
		if(status.status != EDeliveryStatus.assign)
			return res.setError(EAddrError.impossible_cancel_delivery);
		
		//[TODO] 진행중인 상태의 Delivery를 배송자가 취소할 시나리오 필요 
		
		return null;
	}

	private ResponseData<EAddrError> doDeliverPlan(ResponseData<EAddrError> res, DataDeliverPlan data) {
		return null;
	}
	
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
	
	private ResponseData<EAddrError> checkOrderData(ResponseData<EAddrError> res, DataOrderRequest data) {
		if(data.getName() == null || data.getName().length()<1) return res.setError(EAddrError.empty_goods_name);
		if(data.getSize() == EGoodsSize.none) return res.setError(EAddrError.empty_goods_size);
		if(data.getWeight() == EGoodsWeight.none) return res.setError(EAddrError.empty_goods_weight);
		if(data.getType() == EGoodsType.none) return res.setError(EAddrError.empty_goods_type);
		if(data.getPrice() < 1) return res.setError(EAddrError.empty_goods_price);
		if(data.getBegintime() < 1) return res.setError(EAddrError.empty_order_begintime);
		if(data.getEndtime() < 1) return res.setError(EAddrError.empty_order_endtime);
		return res.setError(EAddrError.ok); 
	}
	
	private JsonNode getOrderSearchData(List<RecDeliveryOrder> orderList, List<Document> addrList) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode orderNode = mapper.createObjectNode();
		try {
			List<String> orderJsonList = orderList.stream().map(x -> {
				try {
					return mapper.writeValueAsString(x);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return "";
				}
			}).collect(Collectors.toList());
			List<String> addrJsonList = addrList.stream().map(x -> {
				try {
					return mapper.writeValueAsString(x);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					return "";
				}
			}).collect(Collectors.toList());
			ArrayNode arrOrder = mapper.valueToTree(orderJsonList);
			ArrayNode arrAddr  = mapper.valueToTree(addrJsonList);
			
			orderNode.putArray("orderlist").addAll(arrOrder);
			orderNode.putArray("addrlist").addAll(arrAddr);
		}catch(Exception e) {
			
		}
		return orderNode;
	}
	

}
