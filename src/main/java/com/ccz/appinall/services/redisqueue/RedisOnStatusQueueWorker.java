package com.ccz.appinall.services.redisqueue;

import org.bson.Document;

import com.ccz.appinall.library.module.fcm.FCMConnMgr;
import com.ccz.appinall.library.module.fcm.FCMConnection;
import com.ccz.appinall.library.module.redisqueue.IRedisQueueWorker;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecEpid;
import com.ccz.appinall.services.entity.redis.QueueHeader;
import com.ccz.appinall.services.type.enums.ERedisQueueCmd;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisOnStatusQueueWorker implements IRedisQueueWorker<ERedisQueueCmd> {

private final ERedisQueueCmd cmd = ERedisQueueCmd.owy_status;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public ERedisQueueCmd getCommand() {
		return cmd;
	}

	@Override
	public boolean doWork(String json) throws Exception {
		QueueHeader header = (QueueHeader) objectMapper.readValue(json, QueueHeader.class);
		if(null == header || null == header.to)
			return false;
		RecEpid epid = DbAppManager.getInst().getEpid(header.scode, header.to);
		FCMConnection conn = FCMConnMgr.getInst().getConnection(header.scode);
		conn.send(epid.epid, KeyGen.makeKey("msgid"), json, 1);
		return false;
	}

	private Document makePayload(String to, String from, String json) {
		Document doc = new Document();
		
		return doc;
	}
}
