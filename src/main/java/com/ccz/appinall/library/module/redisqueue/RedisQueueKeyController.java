package com.ccz.appinall.library.module.redisqueue;

import java.util.ArrayList;
import java.util.List;

public class RedisQueueKeyController<T> {
	private String key;
	private int threadCount;
	private List<IRedisQueueWorker<T>> workerList = new ArrayList<>();
	private Thread[] workerThreads;
	
	private RedisQueueRepository queueServerRepository;
	
	public RedisQueueKeyController(String key, int threadCount) {
		this.key = key;
		this.threadCount = threadCount;
	}
	
	public String getKey() { return key;	}
	
	public void addWorker(IRedisQueueWorker<T> redisQueueWorker) {
		workerList.add(redisQueueWorker);
	}
	
	public void setQueueServerRepository(RedisQueueRepository queueServerRepository) {
		this.queueServerRepository = queueServerRepository;
	}
	
	public void startRedisQueueWorker() {
		if(null != workerThreads) {
			return;
		}
		workerThreads = new Thread[threadCount];
		
		for(Thread workerThread : workerThreads) {
			RedisQueueRunnable<T> runnable = new RedisQueueRunnable<T>(key, queueServerRepository);

			workerList.forEach(runnable::addWorker);

			workerThread = new Thread(runnable);
			workerThread.start();
		}
	}
	
	public void stopRedisQueue() {
		try {
			for(Thread workerThread : workerThreads) {
				workerThread.interrupt();
			}
		}catch(Exception e) {
		}
	}
	
}
