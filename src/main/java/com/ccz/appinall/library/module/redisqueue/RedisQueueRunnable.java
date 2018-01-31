package com.ccz.appinall.library.module.redisqueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class RedisQueueRunnable<T> implements Runnable {
	private static Logger logger = Logger.getLogger(RedisQueueRunnable.class.getName());
	
	private RedisQueueReader redisQueueReader;
	private Map<T, IRedisQueueWorker<T>> workMap = new ConcurrentHashMap<>();

	public RedisQueueRunnable(String key, RedisQueueRepository queueServerRepository) {
		redisQueueReader = new RedisQueueReader(key, queueServerRepository);
	}
		
	public void addWorker(IRedisQueueWorker<T> redisQueueWorker) {
		if(null == redisQueueWorker) {
			return;
		}
		workMap.put((T) redisQueueWorker.getCommand(), redisQueueWorker);
	}
	
	@Override
	public void run() {
		while(Thread.currentThread().isInterrupted()==false) {
			try {
				if(redisQueueReader.popData()==false) {
					Thread.sleep(500);
					continue;
				}
				IRedisQueueWorker<T> worker = workMap.get(redisQueueReader.getCommand());
				if(null != worker) {
					worker.doWork(redisQueueReader.getJsonData());
				}
				//[TODO] doWork 리턴값이 false 경우, 해당 command를 별도로 저장해 두어야 할것!!!
				//...
				continue;
			}catch(Exception e) {
				logger.error(e);
			}
			try {
				Thread.sleep(5000);	//예외 발생(cf, Redis Server Shutdown) 재시도 대기시간 5초 
			}catch(Exception e) {
			}
		}
	}

}
