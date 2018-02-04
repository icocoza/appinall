package com.ccz.appinall.services.entity.redis;

import com.ccz.appinall.services.type.enums.ERedisQueueCmd;

public class QueueCmd {
	public String scode;
	public ERedisQueueCmd cmd;
	public String to, from;
	public String fromname;
}
