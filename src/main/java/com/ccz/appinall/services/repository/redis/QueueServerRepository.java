package com.ccz.appinall.services.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QueueServerRepository {

	@Autowired
    private RedisTemplate<String, String> queueServerOperations;

	private final String AIA_SERVER_KEY = "aia:server:";
	private final String AIA_QUEUE_KEY = "aia:queue";
	
	public void enqueueServerCommand(String ip, String json) {	//서버간 메시지 교환 
		queueServerOperations.opsForList().leftPush(AIA_SERVER_KEY + ip, json);
	}
	
	public void enqueueQueueCommand(String json) {	//전체 서버 - 사용할 일 없을 듯 
		queueServerOperations.opsForList().leftPush(AIA_QUEUE_KEY, json);
	}

	public String dequeueQueueCommand(String key) {		// work for server queue or local queue according to the key 
		return queueServerOperations.opsForList().rightPop(key);
	}
	
	/////////////////////
	public Long dequeueServerCommandCount(String ip) {
		return queueServerOperations.opsForList().size(AIA_SERVER_KEY + ip);
	}
	
	public Long getQueueCommandCount() {
		return queueServerOperations.opsForList().size(AIA_QUEUE_KEY);
	}
}
