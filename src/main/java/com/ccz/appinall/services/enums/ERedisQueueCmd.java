package com.ccz.appinall.services.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ERedisQueueCmd {
	none("none"), owy_status("owy_status");
	
	public final String value;
	
	private ERedisQueueCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	private static final Map<String, ERedisQueueCmd> redisQueueMap;
	
	static {
		redisQueueMap = new ConcurrentHashMap<>();
		Arrays.stream(ERedisQueueCmd.values()).forEach(cmd -> redisQueueMap.put(cmd.getValue(), cmd));
	}
	
	static public ERedisQueueCmd getType(String cmd) {
		if(cmd==null) {
			return none;
		}
		ERedisQueueCmd ecmd = redisQueueMap.get(cmd);
		return ecmd != null ? ecmd : none;
	}
}
