package com.ccz.appinall.services.action.address;

import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.application.ApplicationConfig;
import com.ccz.appinall.services.ServicesConfig;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.type.enums.EGoodsSize;
import com.ccz.appinall.services.type.enums.EGoodsType;
import com.ccz.appinall.services.type.enums.EGoodsWeight;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressCommandActionTest {

	@Autowired
	ApplicationConfig applicationConfig;
	@Autowired
	AddressCommandAction addressCommandAction;
	@Autowired
	ServicesConfig servicesConfig;
	
	@Test
	public void testActions() throws UnknownHostException {
		if(DbAppManager.getInst().createAdminDatabase(applicationConfig.getAdminMysqlUrl(), "owy", applicationConfig.getAdminMysqlUser(), applicationConfig.getAdminMysqlPw())==false)
			return;
		if(DbAppManager.getInst().initApp("owy", 2, 3)==false)
			return;
		AddressElasticSearch.getInst().init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
		AddressMongoDb.getInst().init(applicationConfig.getMongoDbUrl(), applicationConfig.getMongoDbPort(), 
				 applicationConfig.getAddressMongoDatabase(), applicationConfig.getAddressMongocollection());
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "search").put("userid", "user001").put("search", "고덕로 131");
		
		addressCommandAction.processJsonData(null, node);
		
		String srcBuildId01 = addressCommandAction.result.getJsonData().get(0).get("buildid").asText();
		System.out.println("id: " + srcBuildId01);
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "search").put("userid", "user001").put("search", "상인동 1149");
		addressCommandAction.processJsonData(null, node);
		
		String srcBuildId02 = addressCommandAction.result.getJsonData().get(0).get("buildid").asText();
		System.out.println("id: " + srcBuildId02);
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderrequest").put("senderid", "user001").put("to_addrid", srcBuildId01).put("from_addrid", srcBuildId02);
		node.put("name", "testuser").put("notice", "fragile").put("size", EGoodsSize.mediumbox.getValue()).put("weight", EGoodsWeight.under5kg.getValue()).put("type", EGoodsType.envelop.getValue());
		node.put("price", 5000).put("begintime", System.currentTimeMillis()).put("endtime", System.currentTimeMillis()+ 1000000);
		addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
	}
}

/*
		private String senderid;
		private String to_addrid, from_addrid; 
		private String name, notice;
		private EGoodsSize size;		
		private EGoodsWeight weight;	
		private EGoodsType type;	
		private int price;	
		private long begintime, endtime;	
		private String photourl;

 * */
