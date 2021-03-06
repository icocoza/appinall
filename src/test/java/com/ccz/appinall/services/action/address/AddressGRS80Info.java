package com.ccz.appinall.services.action.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.library.util.ResourceLoaderService;
import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.ccz.appinall.services.model.elasticsearch.EntrcInfo;
import com.ccz.appinall.services.repository.elasticsearch.AddressElasticSearch;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressGRS80Info {

	@Autowired
	ResourceLoaderService resourceLoaderService;
	@Autowired ElasticSearchManager elasticSearchManager;
	@Autowired AddressElasticSearch addressElasticSearch;

	@Autowired
	ServicesConfig servicesConfig;
	
	//final int MAX_DOC_SIZE = 5;
	final String collectionName = "korea";
	Map<String, EntrcInfo> entMap = new ConcurrentHashMap<>();
	
	@Test
	@Ignore
	public void loadGRS80Address() throws InterruptedException, ExecutionException, IOException {
		AddressElasticSearch addressElasticSearch = new AddressElasticSearch();
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		addressElasticSearch.init();
		
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
				List<ElasticSourcePair> bulkList = new ArrayList<>();
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
/*					  bulkList.add(new ElasticSourcePair((String)doc.get("buildid"), doc.toJson()));
					  if(bulkList.size()>=128) {
						  if(addressElasticSearch.bulkInsert(bulkList)==false) {
							  System.out.println("Insert Failed");
							  return false;
						  }
						  bulkList.clear();
						  System.out.println("@");
					  }
*/
						addressElasticSearch.insert(new ElasticSourcePair((String)doc.get("buildid"), doc.toJson()));
						System.out.printf(".");
						if(++count>128) {
							System.out.println("@");
							count=0;
						}
					}else
						System.out.printf("mismatched");
				}
/*				  	if(bulkList.size() > 0) {
				  		if(addressElasticSearch.bulkInsert(bulkList)==false)
				  			System.out.println("*****Failed to insert");
				  		bulkList.clear();
				  		System.out.println("@");
				  	}
*/
			  	return true;
			};
			
			executor = Executors.newFixedThreadPool(1);
			future = executor.submit(task);
			future.get();
			reader2.close();
			System.out.println("finish updating elastic search");
	}
}
