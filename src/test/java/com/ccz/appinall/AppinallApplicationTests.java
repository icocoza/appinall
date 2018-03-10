package com.ccz.appinall;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.model.db.RecAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppinallApplicationTests {
	@Autowired
	ServicesConfig servicesConfig;
	
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
		if(DbAppManager.getInst().getAddress("oyw", "3611010100102030249000001")==DbRecord.Empty) {
			System.out.println("failed get address");
			return;
		}
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
