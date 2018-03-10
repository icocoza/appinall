package com.ccz.appinall.application.ws;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.DefaultPropertyKey;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.library.module.fcm.FCMConnMgr;
import com.ccz.appinall.library.module.redisqueue.RedisQueueKeyController;
import com.ccz.appinall.library.module.redisqueue.RedisQueueManager;
import com.ccz.appinall.library.module.redisqueue.RedisQueueRepository;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.inf.ICommandProcess;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.controller.admin.AdminCommandAction;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.board.BoardCommandAction;
import com.ccz.appinall.services.controller.channel.ChannelCommandAction;
import com.ccz.appinall.services.controller.friend.FriendCommandAction;
import com.ccz.appinall.services.controller.location.LocationCommandAction;
import com.ccz.appinall.services.controller.message.MessageCommandAction;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.repository.redisqueue.RedisOnStatusQueueWorker;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInAllServiceAction  implements IServiceAction {
	public final AttributeKey<AuthSession> aptSession = AttributeKey.valueOf("APT_SESSION");
	
	private final String serviceCode = "appserver";
	private List<ICommandProcess> cmdProcess = new ArrayList<>();
	//private CommonAction fileCmd = new CommonAction(aptSession);
	
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	RedisQueueManager<ERedisQueueCmd> redisQueueManager;
	
	@Autowired
	public AppInAllServiceAction(ServicesConfig servicesConfig, RedisQueueManager<ERedisQueueCmd> redisQueueManager, AddressCommandAction addressCommandAction) {
		addressCommandAction.setSessionKey(aptSession);
		
		this.servicesConfig = servicesConfig;
		this.redisQueueManager = redisQueueManager; 
		cmdProcess.add(new AdminCommandAction(aptSession));
		cmdProcess.add(new AuthCommandAction(aptSession));
		cmdProcess.add(new BoardCommandAction(aptSession));
		cmdProcess.add(new ChannelCommandAction(aptSession));
		cmdProcess.add(new FriendCommandAction(aptSession));
		cmdProcess.add(new MessageCommandAction(aptSession));
		cmdProcess.add(addressCommandAction);// AddressCommandAction(aptSession));
		cmdProcess.add(new LocationCommandAction(aptSession));
		
		RedisQueueKeyController<ERedisQueueCmd> interServerQueueKeyController = new RedisQueueKeyController<ERedisQueueCmd>(RedisQueueRepository.INTER_SERVER_KEY + StrUtil.getHostIp(), servicesConfig.getRedisQueueCount());
		interServerQueueKeyController.addWorker(new RedisOnStatusQueueWorker());
		redisQueueManager.addController(interServerQueueKeyController);
		
		RedisQueueKeyController<ERedisQueueCmd> globalQueueKeyController = new RedisQueueKeyController<ERedisQueueCmd>(RedisQueueRepository.REDIS_QUEUE_KEY, servicesConfig.getRedisQueueCount());
		globalQueueKeyController.addWorker(new RedisOnStatusQueueWorker());
		redisQueueManager.addController(globalQueueKeyController);

		redisQueueManager.startRedisQueue();

		try {
			FCMConnMgr.getInst().createConnectionPool(servicesConfig.getFcmPoolName(), servicesConfig.getFcmSenderId(), servicesConfig.getFcmSenderKey(), 
					servicesConfig.getFcmUrl(), servicesConfig.getFcmPort(), servicesConfig.getFcmInitCount(), servicesConfig.getFcmMaxCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isService(String serviceType) {
		return this.serviceCode.equals(serviceType);
	}

	@Override
	public boolean process(Channel ch, IDataAccess da) {
		if( da instanceof HttpMultipart) {
			
			return true; 
		}
		if(da.isJson())
			return processJsonData(ch, da.getJsonData());
		else
			return processPacketData(ch, da.getSplitData());
	}

	@Override
	public void send(Channel ch, String data) {
		IWriteProtocol wp = ch.attr(DefaultPropertyKey.writePropertyKey).get();
		wp.write(ch, data);
	}

	@Override
	public void onClose(Channel ch) {
		AuthSession as = ch.attr(aptSession).get();
		if(as==null)
			return;
		SessionManager.getInst().del(as.getKey());
		ch.attr(aptSession).set(null);
	}
	/** 
	 * @param ch channel handle from netty
	 * @param data splitted string data (splitted by AsciiSplitter.CHUNK(27)
	 * 		  [service type][return code][command][api type][data]
	 * @return true if match command, else false
	 */
	private boolean processPacketData(Channel ch, String[] data) {
		for(ICommandProcess process : cmdProcess)
			if(process.processPacketData(ch, data)==true)
				return true;
		
//		ResponseData<EAptError> res = new ResponseData<EAptError>(data[0], data[1], data[2]);
//		switch(EAptCmd.getType(res.getCommand())) {
//		case eNone:
//			break;
//		default:
//			return false;
//		}
//		if(res != null)
//			send(ch, res.toString());
		return true;
	}
	
	private boolean processJsonData(Channel ch, JsonNode jdata) {
		log.info(jdata.toString());
		for(ICommandProcess process : cmdProcess)
			if(process.processJsonData(ch, jdata)==true)
				return true;
		return false;
	}
}
