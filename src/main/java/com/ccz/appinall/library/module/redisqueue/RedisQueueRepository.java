package com.ccz.appinall.library.module.redisqueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisQueueRepository {

	@Autowired
    private RedisTemplate<String, String> queueOperations;

	public static final String INTER_SERVER_KEY = "ccz:intersrv:";
	public static final String REDIS_QUEUE_KEY  = "ccz:queue";
	
	public void pushInterServerCommand(String ip, String json) {	//message exchange between servers 
		queueOperations.opsForList().leftPush(INTER_SERVER_KEY + ip, json);
	}
	
	public void pushQueueCommand(String json) {					//put any queue command
		queueOperations.opsForList().leftPush(REDIS_QUEUE_KEY, json);
	}

	public String popCommand(String key) {						//INTER_SERVER_KEY + IP if InterServer message, else REDIS_QUEUE_KEY 
		return queueOperations.opsForList().rightPop(key);	
	}
	
}
