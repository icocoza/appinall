package com.ccz.appinall.services.repository.redisqueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.library.module.redisqueue.IRedisQueueWorker;
import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.redis.QueueDeliveryStatus;
import com.ccz.appinall.services.repository.redis.QueueServerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisOnStatusQueueWorker implements IRedisQueueWorker<ERedisQueueCmd> {

	private final ERedisQueueCmd cmd = ERedisQueueCmd.delivery_status;
	
	@Autowired
	QueueServerRepository queueServerRepository;
	@Autowired
	SessionManager sessionManager;
	
	@Override
	public ERedisQueueCmd getCommand() {
		return cmd;
	}

	//a delivery_status message from the other server
	@Override
	public boolean doWork(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		QueueDeliveryStatus qds = (QueueDeliveryStatus) objectMapper.readValue(json, QueueDeliveryStatus.class);
		if(null == qds || null == qds.to)
			return false;
		
		SessionItem si = sessionManager.get(qds.to);
		if(si != null) {	//exist session in this server
			si.getCh().writeAndFlush(json);
			return true;
		}
		//이미 다른 서버에서 넘어 온 메시지이기에, 현 서버에 세션이 존재하지 않으면 바로 PUSH 발송
		//QueueCmd를 fcm_push로 변경해야 함
		qds.cmd = ERedisQueueCmd.fcm_push;
		json = objectMapper.writeValueAsString(qds);
		 
		queueServerRepository.enqueueQueueCommand(json);
		return true;
	}

}
