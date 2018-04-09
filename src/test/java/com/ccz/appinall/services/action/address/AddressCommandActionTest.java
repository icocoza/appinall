package com.ccz.appinall.services.action.address;

import java.net.UnknownHostException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.controller.address.AddressElasticSearch;
import com.ccz.appinall.services.enums.EDeliverType;
import com.ccz.appinall.services.enums.EDeliverMethod;
import com.ccz.appinall.services.enums.EGoodsSize;
import com.ccz.appinall.services.enums.EGoodsType;
import com.ccz.appinall.services.enums.EGoodsWeight;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AddressCommandActionTest {

	@Autowired
	AddressCommandAction addressCommandAction;
	@Autowired
	ServicesConfig servicesConfig;
	private void init()  throws UnknownHostException{
		if(DbAppManager.getInst().createAdminDatabase(servicesConfig.getAdminMysqlUrl(), "owy", servicesConfig.getAdminMysqlOption(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw())==false)
			return;
		if(DbAppManager.getInst().initApp("owy", 2, 3)==false)
			return;
		AddressElasticSearch.getInst().init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
		//AddressMongoDb.getInst().init(servicesConfig.getMongoDbUrl(), servicesConfig.getMongoDbPort(), 
		//		servicesConfig.getAddressMongoDatabase(), servicesConfig.getAddressMongocollection());
	}
	@Test
	@Ignore
	public void testActions()  throws UnknownHostException{
		init();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "search").put("userid", "user001").put("search", "고덕로 131");
		//addressCommandAction.processJsonData(null, node);
		
		String srcBuildId01 = addressCommandAction.result.getJsonData().get(0).get("buildid").asText();
		System.out.println("id: " + srcBuildId01);

		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "search").put("userid", "user001").put("search", "상인동 1149");
		//addressCommandAction.processJsonData(null, node);
		
		String srcBuildId02 = addressCommandAction.result.getJsonData().get(0).get("buildid").asText();
		System.out.println("id: " + srcBuildId02);
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderrequest").put("userid", "user001").put("to_addrid", srcBuildId01).put("from_addrid", srcBuildId02);
		node.put("name", "testuser").put("notice", "fragile").put("size", EGoodsSize.mediumbox.getValue()).put("weight", EGoodsWeight.under5kg.getValue()).put("type", EGoodsType.envelop.getValue());
		node.put("price", 5000).put("begintime", System.currentTimeMillis()).put("endtime", System.currentTimeMillis()+ 1000000);
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		String orderid = addressCommandAction.result.getParams().get("orderid");
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderlist").put("userid", "user001").put("offset", 0).put("count", 10);
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderdetail").put("userid", "user001").put("orderid", orderid);
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());

		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "deliversearchorder").put("userid", "deliver001").put("to_addrid", "1174010700104140002000001").put("from_addrid", "1123010300111490012000004")
		.put("size", EGoodsSize.mediumbox.getValue()).put("weight", EGoodsWeight.under5kg.getValue()).put("type", EGoodsType.envelop.getValue());
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());

		long startTime = System.currentTimeMillis() + 3600000;
		long endTime = System.currentTimeMillis() + 7200000;
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "deliverselectorder").put("userid", "deliver001").put("orderid", orderid).put("begintime", startTime).put("endtime", endTime)
		.put("price", 5000).put("delivertype", EDeliverType.personal.getValue()).put("deliverytype", EDeliverMethod.car.getValue());
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderlist").put("userid", "user001").put("offset", 0).put("count", 10);
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderdetail").put("userid", "user001").put("orderid", orderid);
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "orderselectdeliver").put("userid", "user001").put("orderid", orderid).put("deliverid", "deliver001");
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());
		
//		node.removeAll();
//		node.put("scode", "owy").put("rcode", "r001").put("cmd", "ordercanceldeliver").put("userid", "user001").put("orderid", orderid).put("deliverid", "deliver001");
//		addressCommandAction.processJsonData(null, node);
//		System.out.println(addressCommandAction.result.toString());

//		node.removeAll();
//		node.put("scode", "owy").put("rcode", "r001").put("cmd", "delivercheckinorder").put("userid", "deliver001").put("orderid", orderid);
//		addressCommandAction.processJsonData(null, node);
//		System.out.println(addressCommandAction.result.toString());
		
//		node.removeAll();
//		node.put("scode", "owy").put("rcode", "r001").put("cmd", "delivermoving").put("userid", "deliver001").put("orderid", "orderd6d013d01c18464a8bd657a586f73a3c20180130074502");
//		addressCommandAction.processJsonData(null, node);
//		System.out.println(addressCommandAction.result.toString());
		
//		node.removeAll();
//		node.put("scode", "owy").put("rcode", "r001").put("cmd", "delivergotcha").put("userid", "deliver001").put("orderid", "orderd6d013d01c18464a8bd657a586f73a3c20180130074502").put("startcode", "48827");
//		addressCommandAction.processJsonData(null, node);
//		System.out.println(addressCommandAction.result.toString());
		
//		node.removeAll();
//		node.put("scode", "owy").put("rcode", "r001").put("cmd", "deliverdelivering").put("userid", "deliver001").put("orderid", "orderd6d013d01c18464a8bd657a586f73a3c20180130074502").put("startcode", "48827");
//		addressCommandAction.processJsonData(null, node);
//		System.out.println(addressCommandAction.result.toString());

		node.removeAll();
		node.put("scode", "owy").put("rcode", "r001").put("cmd", "deliverdeliverycomplete").put("userid", "deliver001").put("orderid", "orderd6d013d01c18464a8bd657a586f73a3c20180130074502").put("endcode", "48827").put("message", "thank you");
		//addressCommandAction.processJsonData(null, node);
		System.out.println(addressCommandAction.result.toString());

	}
}

/*
		private String orderid, deliverid;
		private long begintime, endtime;	//deliver가 제안하는 시작시간, 끝시간
		private int price;
		private EDeliverType delivertype;
		private EDeliveryType deliverytype;

 * */
