package com.ccz.appinall.library.module.redisqueue;

import org.apache.log4j.Logger;

import com.ccz.appinall.services.entity.redis.QueueCmd;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisQueueReader {
	private static Logger logger = Logger.getLogger(RedisQueueReader.class.getName());
	
	private String key;
	private RedisQueueRepository queueServerRepository;
	private ObjectMapper objectMapper = new ObjectMapper();
	private QueueCmd queueCmd;
	private String popData;
	
	public RedisQueueReader(String key, RedisQueueRepository queueServerRepository) {
		this.key = key;
		this.queueServerRepository = queueServerRepository;
	}
	
	public boolean popData() {
		popData = this.queueServerRepository.popCommand(key);
		if(null == popData) {
			return false;
		}
		try {
			queueCmd = (QueueCmd) objectMapper.readValue(popData, QueueCmd.class);
		}catch(Exception e) {
			logger.error(e);
			queueCmd = null;
			return false;
		}
		return true;
	}
	
	public String getCommand() throws Exception {
		return queueCmd.cmd.getValue();
	}
	
	public String getJsonData() {	return popData;	}
	
}
