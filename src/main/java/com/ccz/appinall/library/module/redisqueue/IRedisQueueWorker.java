package com.ccz.appinall.library.module.redisqueue;


public interface IRedisQueueWorker<T> {
	
	T getCommand();
	boolean doWork(String json) throws Exception;
	
	default boolean isWorkerCommand(T cmd) {
		return getCommand() == cmd;
	}
}
