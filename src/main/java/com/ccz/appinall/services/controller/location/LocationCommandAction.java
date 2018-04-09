package com.ccz.appinall.services.controller.location;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.location.RecDataLocation.*;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.ELocationCmd;
import com.ccz.appinall.services.enums.ELocationError;
import com.ccz.appinall.services.model.db.RecDeliveryApply;
import com.ccz.appinall.services.model.db.RecDeliveryOrder;
import com.ccz.appinall.services.model.redis.QueueGpsInfo;
import com.ccz.appinall.services.repository.redis.DeliverChannelRepository;
import com.ccz.appinall.services.repository.redis.DeliverLocRepository;
import com.ccz.appinall.services.service.SendMessageManager;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocationCommandAction extends CommonAction {
	
	@Autowired
	DeliverLocRepository deliverLocRepository;
	@Autowired
	DeliverChannelRepository deliverChannelRepository;
	@Autowired 
	SendMessageManager sendMessageManager;
	
	public LocationCommandAction() {
		super.setCommandFunction(ELocationCmd.geoloc.getValue(), doGeoLoc);
		super.setCommandFunction(ELocationCmd.joinchannel.geoloc.getValue(), doJoinChannel);
		super.setCommandFunction(ELocationCmd.leavechannel.geoloc.getValue(), doLeaveChannel);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<ELocationError> res = new ResponseData<ELocationError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<ELocationError>) cmdFunc.doAction(session, res, jdata);
			send(ch, res.toString());
			return true;
		}
		return false;
	}


	ICommandFunction<AuthSession, ResponseData<ELocationError>, JsonNode> doGeoLoc = (AuthSession ss, ResponseData<ELocationError> res, JsonNode jnode) -> {
		GpsInfo data = new RecDataLocation().new GpsInfo(jnode);
		deliverLocRepository.setLocation(ss.getUserId(), data.getLon(), data.getLat());
		Set<String> attendees = deliverChannelRepository.getChannelUser(ss.getUserId());
		for(String user : attendees) {
			QueueGpsInfo gps = new QueueGpsInfo(data.getScode(), ss.getUserId(), user, data);
			try {
				sendMessageManager.sendGpsInfo(gps);
			}catch(Exception e) {
			}
		}
		return res.setError(ELocationError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<ELocationError>, JsonNode> doJoinChannel = (AuthSession ss, ResponseData<ELocationError> res, JsonNode jnode) -> {
		JoinChannel data = new RecDataLocation().new JoinChannel(jnode);
		RecDeliveryOrder order = DbAppManager.getInst().getOrder(data.getScode(), data.getOrderid());
		if(order.senderid.equals(ss.getUserId())==false)
			return res.setError(ELocationError.invalid_orderid);
		
		List<RecDeliveryApply> delivers = DbAppManager.getInst().getDeliverList(data.getScode(), data.getOrderid());
		boolean matched = delivers.stream().anyMatch(x -> x.deliverid.equals(data.getDeliverid()));
		if(matched==false) 
			return res.setError(ELocationError.mismatch_orderid_deliverid);
		
		ss.addCleanUpId(data.getDeliverid());	//need clear 
		
		deliverChannelRepository.addChannelUser(data.getDeliverid(), ss.getUserId());
		return res.setError(ELocationError.ok);
	};

	ICommandFunction<AuthSession, ResponseData<ELocationError>, JsonNode> doLeaveChannel = (AuthSession ss, ResponseData<ELocationError> res, JsonNode jnode) -> {
		LeaveChannel data = new RecDataLocation().new LeaveChannel(jnode);
		ss.delCleanUpId(data.getDeliverid());
		
		deliverChannelRepository.delChannelUser(data.getDeliverid(), ss.getUserId());
		return res;
	};

}
