package com.ccz.appinall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.module.redisqueue.RedisQueueManager;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.type.inf.IServiceHandler;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.controller.admin.AdminCommandAction;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.ccz.appinall.services.controller.board.BoardCommandAction;
import com.ccz.appinall.services.controller.channel.ChannelCommandAction;
import com.ccz.appinall.services.controller.delivery.DeliveryCommandAction;
import com.ccz.appinall.services.controller.file.FileCommandAction;
import com.ccz.appinall.services.controller.friend.FriendCommandAction;
import com.ccz.appinall.services.controller.location.LocationCommandAction;
import com.ccz.appinall.services.controller.message.MessageCommandAction;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.db.RecAddress;
import com.ccz.appinall.services.repository.redis.DeliverChannelRepository;
import com.ccz.appinall.services.repository.redis.SessionRepository;
import com.ccz.appinall.services.service.SendMessageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppinallApplicationTests {
	@Autowired
	ServicesConfig servicesConfig;
	
	private Map<EAllCmd, ICommandFunction> cmdFuncMap = new ConcurrentHashMap<>();
	//private CommonAction fileCmd = new CommonAction(attrAuthSessionKey);
	
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
	
	
	@Before
	@Ignore
	public void init() {
		
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
	}
	@Test
	@Ignore
	public void funcLoadTest() {
		ICommandFunction cmdFunc = cmdFuncMap.get(EAllCmd.getType("signin"));
		if(cmdFunc == null) {
			System.out.println("Unknown Command");
			return;
		}
		System.out.println("Got It!");
	}
	@Test
	@Ignore
	public void mysqlAddressTest() {
		if(initDatabase() == false) {
			System.out.println("failed initDatabase");
			return;
		}
		
		//if(DbAppManager.getInst().addAddress("oyw", "test_buildid", "30149","테스트시", "테스트", "", "테스트로", "", "테스트빌딩", "", "", "", 254, 11, 0, 0, 127.183548, 36.285295) == false) {
		//	System.out.println("failed add address");
		//	return;
		//}
//		if(DbAppManager.getInst().getAddress("oyw", "3611010100102030249000001")==DbRecord.Empty) {
//			System.out.println("failed get address");
//			return;
//		}
		DbAppManager.getInst().getPoiByGps("oyw", 127.141918, 37.556377);
		List<String> buildids = new ArrayList<>();
		buildids.add("3611010100107360000000001");
		buildids.add("3611010100107710076000001");
		buildids.add("3611010100107710110000001");
		List<RecAddress> addrList = DbAppManager.getInst().getAddressList("oyw", buildids);
		for(RecAddress rec : addrList)
			System.out.println(rec.buildid);
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrAddr = mapper.valueToTree(addrList);
		System.out.println(arrAddr.toString());
	}

	public boolean initDatabase() {
        if(DbAppManager.getInst().createAdminDatabase(servicesConfig.getAdminMysqlUrl(), servicesConfig.getAdminMysqlDbName(), servicesConfig.getAdminMysqlOption(),
        		servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw()) == false) {
        		System.out.println("Fail to Create the Database for Admin");
        		return false;
        }
        if(DbAppManager.getInst().initAdmin(servicesConfig.getAdminMysqlPoolname(), servicesConfig.getAdminMysqlUrl(), 
        		servicesConfig.getAdminMysqlDbName(), servicesConfig.getAdminMysqlOption(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw(), 4, 4) == false) {
        		System.out.println("Fail to Init Admin DB Table");
        		return false;
        }
        if(DbAppManager.getInst().initApp("oyw", 2,3) == false) {
        		System.out.println("Fail to Init App DB Table");
        		return false;
        }
        return true;
	}
}
