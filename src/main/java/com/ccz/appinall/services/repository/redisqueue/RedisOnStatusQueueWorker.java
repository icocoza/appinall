package com.ccz.appinall.services.repository.redisqueue;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.module.fcm.FCMConnMgr;
import com.ccz.appinall.library.module.fcm.FCMConnection;
import com.ccz.appinall.library.module.redisqueue.IRedisQueueWorker;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.db.RecPushToken;
import com.ccz.appinall.services.model.redis.QueueCmd;
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
		QueueCmd header = (QueueCmd) objectMapper.readValue(json, QueueCmd.class);
		if(null == header || null == header.to)
			return false;
		RecPushToken epid = DbAppManager.getInst().getEpid(header.scode, header.to);
		String msgid = KeyGen.makeKey("msgid");
		FCMConnection conn = FCMConnMgr.getInst().getConnection(header.scode);
		boolean bOk = conn.send(epid.epid, msgid, json, 1);
		if(bOk==false) {
			DbAppManager.getInst().addFailedPushMsg(header.scode, msgid, header.to, header.to, epid.epid, json);
		}
		return bOk;
	}

}
