package com.ccz.appinall.library.module.redisqueue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RedisQueueManager<T> {
	
	private List<RedisQueueKeyController<T>> queueKeyControllerList = new ArrayList<>();
	
	@Autowired
	private RedisQueueRepository queueServerRepository;
	
	public void addController(RedisQueueKeyController<T> redisQueueKeyController) {
		redisQueueKeyController.setQueueServerRepository(queueServerRepository);
		queueKeyControllerList.add(redisQueueKeyController);
	}
	
	public void startRedisQueue() {
		for(RedisQueueKeyController<T> queue : queueKeyControllerList) {
			queue.startRedisQueueWorker();
		}
	}
	
	public void stopRedisQueue() {
		for(RedisQueueKeyController<T> queue : queueKeyControllerList) {
			queue.stopRedisQueue();
		}
	}
	
}
