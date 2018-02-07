package com.ccz.appinall.services.model.redis;

import com.ccz.appinall.services.enums.ERedisQueueCmd;

public class QueueCmd {
	public String scode;
	public ERedisQueueCmd cmd;
	public String to, from;
	public String fromname;
}
