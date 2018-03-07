package com.ccz.appinall.services.action.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.services.controller.address.AddressInference;
import com.ccz.appinall.services.model.elasticsearch.EntrcInfo;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AddressMongoDbTests {
	final int MAX_DOC_SIZE = 250;
	final String collectionName = "korea";
	
	
	public void findAddress() {
/*		AddressMongoDb.getInst().init("localhost", 27017, "address", collectionName);
		AddressInference ai = new AddressInference("고덕로131");
		List<Document> list = AddressMongoDb.getInst().findAddr(ai);
		list.forEach(item -> System.out.println(item.toJson()));
		ai = new AddressInference("상인동 1149");
		list = AddressMongoDb.getInst().findAddr(ai);
		list.forEach(item -> System.out.println(item.toJson()));
		
		ai = new AddressInference("화원읍 구라리 1557");
		list = AddressMongoDb.getInst().findAddr(ai);
		list.forEach(item -> System.out.println(item.toJson()));
		*/
	}
	
	//@Test
	public void insertAddressToMongoDB() throws IOException, InterruptedException, ExecutionException {
		
		System.out.println("start reading the address db");
		Map<String, EntrcInfo> entMap = new ConcurrentHashMap<>();
		{
			InputStream is = getClass().getResourceAsStream("/static/entrc_seoul.txt");
			BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
			Callable<Boolean> task = () -> {
				String line = reader.readLine();
				int count = 0;
				while ( (line = reader.readLine())!=null) {
				  String[] sp = line.split("\\|", -1);
				  EntrcInfo ent = CommonAddressUtils.makeGRS80Map(sp);
				  if(entMap.containsKey(ent.getId())==true)
					  break;
				  entMap.put(ent.getId(), ent);
				  System.out.println(++count + "");
				}
				return true;
			};
			ExecutorService executor = Executors.newFixedThreadPool(1);
			Future<Boolean> future = executor.submit(task);
			future.get();
			reader.close();
		}
			System.out.println("finsih reading the address db");
			System.out.println("start converting the UTM-K to the WGS84");
		{
			int count = 0;
			List<EntrcInfo> entrcList = new ArrayList<EntrcInfo>();
			Set<Entry<String, EntrcInfo>> entries = entMap.entrySet();
			for(Entry<String, EntrcInfo> item : entries) {
				entrcList.add(item.getValue());
				if(entrcList.size() >= 128) {
					CommonAddressUtils.updateCoordination(entrcList);
					entrcList.clear();
					System.out.println("@" + (++count)*128);
				}
				System.out.printf(".");
			}
			if(entrcList.size() > 0) {
				CommonAddressUtils.updateCoordination(entrcList);
				entrcList.clear();
			}
		}
		System.out.println("finsih converting");
		System.out.println("start saving to MongoDb");
		{
			//AddressMongoDb.getInst().init("localhost", 27017, "address", collectionName);
			//AddressMongoDb.getInst().createUpsertIndex();
			//AddressMongoDb.getInst().createSearchIndex();
			
			List<Document> docList = new ArrayList<>();
			InputStream is = getClass().getResourceAsStream("/static/seoul.txt");
			BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
			
			Callable<Boolean> task = () -> {
				String line = reader.readLine();
				int count = 0;
				while ( (line = reader.readLine())!=null) {
				  String[] sp = line.split("\\|", -1);
				  Document doc = CommonAddressUtils.makeDocument(sp);
				  if(entMap.containsKey((String)doc.get("id"))) {
					  EntrcInfo ent = entMap.get((String)doc.get("id"));
					  doc.put("x", ent.x);
					  doc.put("y", ent.y);
					  doc.put("lac", ent.getLac());
					  doc.put("lon", ent.getLon());
					  doc.put("lactitude", ent.getLactitude());
					  doc.put("longitude", ent.getLongitude());
					  doc.remove("id");
				  }
				  docList.add(doc);
				  if(docList.size() >= MAX_DOC_SIZE) {
					  //AddressMongoDb.getInst().bulkInsert(docList);
					  docList.clear();
				  }
				  System.out.println(++count + "");
				}
				//if(docList.size()>0)
					//AddressMongoDb.getInst().bulkInsert(docList);
				return true;
			};
			
			ExecutorService executor = Executors.newFixedThreadPool(1);
			Future<Boolean> future = executor.submit(task);
			future.get();
			reader.close();
		}
	}

}
