package com.ccz.appinall.services.action.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.application.server.http.admin.business.service.ResourceLoaderService;
import com.ccz.appinall.services.ServicesConfig;
import com.ccz.appinall.services.entity.elasticsearch.ElasticSourcePair;
import com.ccz.appinall.services.entity.elasticsearch.EntrcInfo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressGRS80Info {

	@Autowired
	ResourceLoaderService resourceLoaderService;
	
	@Autowired
	ServicesConfig servicesConfig;
	
	//final int MAX_DOC_SIZE = 5;
	final String collectionName = "korea";
	Map<String, EntrcInfo> entMap = new ConcurrentHashMap<>();
	@Test
	public void loadGRS80Address() throws InterruptedException, ExecutionException, IOException {
		AddressElasticSearch addressElasticSearch = new AddressElasticSearch();
		addressElasticSearch.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
		
			System.out.println("start reading the address db");
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
			System.out.println("finsih reading the address db");
			System.out.println("start converting the UTM-K to the WGS84");

			List<EntrcInfo> entrcList = new ArrayList<EntrcInfo>();
			Set<Entry<String, EntrcInfo>> entries = entMap.entrySet();
			for(Entry<String, EntrcInfo> item : entries) {
				entrcList.add(item.getValue());
				if(entrcList.size() >= 128) {
					CommonAddressUtils.updateCoordination(entrcList);
					entrcList.clear();
				}
			}
			if(entrcList.size() > 0) {
				CommonAddressUtils.updateCoordination(entrcList);
				entrcList.clear();
			}
			System.out.println("finsih converting");
			
			System.out.println("start updating elastic search db");
			InputStream is2 = getClass().getResourceAsStream("/static/seoul.txt");
			BufferedReader reader2  = new BufferedReader(new InputStreamReader(is2, "EUC-KR"));
			List<ElasticSourcePair> pairs = new ArrayList<>();
			task = () -> {
				String line = reader2.readLine();
				int count = 0;
				while ( (line = reader2.readLine())!=null) {
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
					  try {
					  addressElasticSearch.updateAddress(new ElasticSourcePair((String)doc.get("buildmgr"), doc.toJson()));
					  }catch(Exception e) {
						  e.printStackTrace();
						  return false;
					  }
				  }else
					  System.out.println("mismatch cnt: "+(++count));
				}
				return true;
			};
			
			executor = Executors.newFixedThreadPool(1);
			future = executor.submit(task);
			future.get();
			reader2.close();
			System.out.println("finish updating elastic search");
	}
}
