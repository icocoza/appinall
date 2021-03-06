package com.ccz.appinall.application.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.library.module.fcm.FCMConnMgr;
import com.ccz.appinall.library.module.redisqueue.RedisQueueKeyController;
import com.ccz.appinall.library.module.redisqueue.RedisQueueManager;
import com.ccz.appinall.library.module.redisqueue.RedisQueueRepository;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.type.inf.ICommandProcess;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceHandler;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.controller.admin.AdminCommandAction;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.board.BoardCommandAction;
import com.ccz.appinall.services.controller.channel.ChannelCommandAction;
import com.ccz.appinall.services.controller.delivery.DeliveryCommandAction;
import com.ccz.appinall.services.controller.file.FileCommandAction;
import com.ccz.appinall.services.controller.file.FileSession;
import com.ccz.appinall.services.controller.friend.FriendCommandAction;
import com.ccz.appinall.services.controller.location.LocationCommandAction;
import com.ccz.appinall.services.controller.message.MessageCommandAction;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.repository.redis.DeliverChannelRepository;
import com.ccz.appinall.services.repository.redis.OrderGeoRepository;
import com.ccz.appinall.services.repository.redis.SessionRepository;
import com.ccz.appinall.services.repository.redisqueue.RedisOnPushQueueWorker;
import com.ccz.appinall.services.repository.redisqueue.RedisOnStatusQueueWorker;
import com.ccz.appinall.services.service.SendMessageManager;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInAllServiceHandler  implements IServiceHandler {
	
	private final String serviceCode = "appserver";
	//private List<ICommandProcess> cmdProcess = new ArrayList<>();
	private Map<EAllCmd, ICommandFunction> cmdFuncMap = new ConcurrentHashMap<>();
	//private CommonAction fileCmd = new CommonAction(attrAuthSessionKey);
	
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	RedisQueueManager<ERedisQueueCmd> redisQueueManager;
	@Autowired
	SendMessageManager sendMessageManager;
	@Autowired
	SessionManager sessionManager;
	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	DeliverChannelRepository deliverChannelRepository;
	
	@Autowired AdminCommandAction adminCommandAction;
	@Autowired AuthCommandAction authCommandAction;
	@Autowired DeliveryCommandAction deliveryCommandAction;
	@Autowired BoardCommandAction boardCommandAction;
	@Autowired ChannelCommandAction channelCommandAction;
	@Autowired FriendCommandAction friendCommandAction;
	@Autowired MessageCommandAction messageCommandAction;
	@Autowired AddressCommandAction addressCommandAction;
	@Autowired LocationCommandAction locationCommandAction;
	@Autowired FileCommandAction fileCommandAction;
	
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public AppInAllServiceHandler() { }
	
	public IServiceHandler init() {
		
		if(cmdFuncMap.size() < 1) {
			cmdFuncMap.putAll(adminCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(authCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(deliveryCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(boardCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(channelCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(friendCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(messageCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(addressCommandAction.getCommandFunctions());// AddressCommandAction(attrAuthSessionKey));
			cmdFuncMap.putAll(locationCommandAction.getCommandFunctions());
			cmdFuncMap.putAll(fileCommandAction.getCommandFunctions());
		}
		//between server
		RedisQueueKeyController<ERedisQueueCmd> interServerQueueKeyController = new RedisQueueKeyController<ERedisQueueCmd>(RedisQueueRepository.INTER_SERVER_KEY + StrUtil.getHostIp(), servicesConfig.getRedisQueueCount());
		interServerQueueKeyController.addWorker(new RedisOnStatusQueueWorker());
		redisQueueManager.addController(interServerQueueKeyController);
		
		//global
		RedisQueueKeyController<ERedisQueueCmd> globalQueueKeyController = new RedisQueueKeyController<ERedisQueueCmd>(RedisQueueRepository.REDIS_QUEUE_KEY, servicesConfig.getRedisQueueCount());
		globalQueueKeyController.addWorker(new RedisOnPushQueueWorker());
		redisQueueManager.addController(globalQueueKeyController);

		redisQueueManager.startRedisQueue();

		try {
			FCMConnMgr.getInst().createConnectionPool(servicesConfig.getFcmPoolName(), servicesConfig.getFcmSenderId(), servicesConfig.getFcmSenderKey(), 
					servicesConfig.getFcmUrl(), servicesConfig.getFcmPort(), servicesConfig.getFcmInitCount(), servicesConfig.getFcmMaxCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public boolean isService(String serviceType) {
		return this.serviceCode.equals(serviceType);
	}

	@Override
	public boolean process(Channel ch, IDataAccess da) {
		log.info(da.getStringData());
		return processJsonData(ch, da.getJsonData());
	}

	@Override
	public void send(Channel ch, String data) {
		log.info("[RESPONSE]" + data);
		IWriteProtocol wp = ch.attr(chAttributeKey.getWriteKey()).get();
		wp.write(ch, data);
	}

	@Override
	public void onClose(Channel ch) {
		AuthSession as = ch.attr(chAttributeKey.getAuthSessionKey()).get();
		if(as==null)
			return;
		as.getCleanUpIds().forEach(x->deliverChannelRepository.delChannelUser(x, as.getUserId()));
		sessionManager.del(as.getKey());
		sessionRepository.delete(as.getUserId(), as.getSessionData());
		ch.attr(chAttributeKey.getAuthSessionKey()).set(null);
	}
	
	@SuppressWarnings("unchecked")
	private boolean processJsonData(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EAllError> res = new ResponseData<EAllError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		
		AuthSession session = ch.attr(chAttributeKey.getAuthSessionKey()).get();
		if(session == null) {
			/*if(cmd.isNeedSession()) {
				log.info("[SESSION] WrongSession");
				send(ch, res.setError(ETaxiError.WRONG_SESSION).toJsonString());
				res.clear();
				return;
			}*/
			session = new AuthSession(ch);
		}

		@SuppressWarnings({ "rawtypes", "unlikely-arg-type" })
		EAllCmd eCmd = EAllCmd.getType(cmd);
		ICommandFunction cmdFunc = cmdFuncMap.get(eCmd);
		if(cmdFunc == null) {
			log.info("Unknown Command");
			return false;
		}
		if(eCmd.isNeedSession()==true)
			res = (ResponseData<EAllError>) cmdFunc.doAction(session, res, jdata);
		else
			res = (ResponseData<EAllError>) cmdFunc.doAction(ch, res, jdata);
		this.send(ch, res.toJsonString());
		return true;
	}
	
}
