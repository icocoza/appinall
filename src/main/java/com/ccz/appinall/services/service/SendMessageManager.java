package com.ccz.appinall.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.redis.QueueDeliveryStatus;
import com.ccz.appinall.services.model.redis.QueueGpsInfo;
import com.ccz.appinall.services.model.redis.SessionData;
import com.ccz.appinall.services.repository.redis.QueueServerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SendMessageManager {
	
	@Autowired
	SessionService sessionService;
	@Autowired
	SessionManager sessionManager;
	@Autowired
	QueueServerRepository queueServerRepository;
	
	public void sendDeliveryStatus(QueueDeliveryStatus qds) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(qds);
		
		SessionItem si = sessionManager.get(qds.to);
		if(si != null) {	//exist session in this server
			si.getCh().writeAndFlush(json);
			return;
		} 
		SessionData sd = sessionService.getUserSession(qds.to);
		if(sd != null) {	//exist session on the other server
			queueServerRepository.enqueueServerCommand(sd.getIp(), json);
			return;
		}
		//send push for not exist session
		//QueueCmd를 fcm_push로 변경해야 함
		qds.cmd = ERedisQueueCmd.fcm_push;
		json = objectMapper.writeValueAsString(qds);
		queueServerRepository.enqueueQueueCommand(json);
	}
	
	public void sendGpsInfo(QueueGpsInfo gps) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(gps);
		
		SessionItem si = sessionManager.get(gps.to);
		if(si != null) {	//exist session in this server
			si.getCh().writeAndFlush(json);
			return;
		} 
		SessionData sd = sessionService.getUserSession(gps.to);
		if(sd != null)
			queueServerRepository.enqueueServerCommand(sd.getIp(), json);
	}
	
}
