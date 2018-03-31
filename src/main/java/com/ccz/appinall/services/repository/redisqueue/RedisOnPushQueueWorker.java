package com.ccz.appinall.services.repository.redisqueue;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.module.fcm.FCMConnMgr;
import com.ccz.appinall.library.module.fcm.FCMConnection;
import com.ccz.appinall.library.module.redisqueue.IRedisQueueWorker;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.db.RecPushToken;
import com.ccz.appinall.services.model.redis.QueueDeliveryStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisOnPushQueueWorker implements IRedisQueueWorker<ERedisQueueCmd> {

	private final ERedisQueueCmd cmd = ERedisQueueCmd.fcm_push;
	
	@Override
	public ERedisQueueCmd getCommand() {	return cmd;		}

	@Override
	public boolean doWork(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		QueueDeliveryStatus deliveryStatus = (QueueDeliveryStatus) objectMapper.readValue(json, QueueDeliveryStatus.class);
		if(null == deliveryStatus || null == deliveryStatus.to)
			return false;
		RecPushToken epid = DbAppManager.getInst().getEpid(deliveryStatus.scode, deliveryStatus.to);
		String msgid = KeyGen.makeKey("msgid");
		FCMConnection conn = FCMConnMgr.getInst().getConnection(deliveryStatus.scode);
		boolean bOk = conn.send(epid.epid, msgid, json, 1);
		if(bOk==false) {
			DbAppManager.getInst().addFailedPushMsg(deliveryStatus.scode, msgid, deliveryStatus.to, deliveryStatus.to, epid.epid, json);
		}
		return bOk;
	}

}
