package com.ccz.appinall.services.action.address;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.library.util.ResourceLoaderService;
import com.ccz.appinall.services.controller.address.AddressInference;
import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.ccz.appinall.services.repository.elasticsearch.AddressElasticSearch;
import com.fasterxml.jackson.core.JsonProcessingException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AddressElasticTests {

	@Autowired ElasticSearchManager elasticSearchManager;
	@Autowired AddressElasticSearch addressElasticSearch;

	@Autowired
	ResourceLoaderService resourceLoaderService;
	
	@Autowired
	ServicesConfig servicesConfig;
	
	final int MAX_DOC_SIZE = 5;
	final String collectionName = "korea";
	
	@Test
	@Ignore
	public void findAddresByRest() throws JsonProcessingException, IOException {
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		addressElasticSearch.init();
		AddressInference ai = new AddressInference("고덕로 롯데캐슬");
		String resBody = addressElasticSearch.searchAddrByRest(ai);
		System.out.println(resBody);
	}
	public void findAddress() throws InterruptedException, ExecutionException, UnknownHostException, JsonProcessingException {
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		addressElasticSearch.init();

		try {
			AddressInference ai = new AddressInference("고덕로 롯데캐슬");
			SearchResponse res = addressElasticSearch.searchAddr(ai);

			for(SearchHit hit : res.getHits()) {
				System.out.println(hit.getScore() + " - " +hit.getSourceAsString());
			}
			
			ai = new AddressInference("고덕로 롯데캐슬");
			res = addressElasticSearch.searchAddr(ai);
			for(SearchHit hit : res.getHits())
				System.out.println(hit.getSourceAsString());
			
			ai = new AddressInference("상인동 1149");
			res = addressElasticSearch.searchAddr(ai);
			for(SearchHit hit : res.getHits())
				System.out.println(hit.getSourceAsString());
			
			ai = new AddressInference("화원읍 구라리 1557");
			res = addressElasticSearch.searchAddr(ai);
			for(SearchHit hit : res.getHits())
				System.out.println(hit.getSourceAsString());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveToFile() throws InterruptedException, ExecutionException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("seoul.txt"), "EUC-KR"); 
			
		InputStream is = getClass().getResourceAsStream("/static/seoul.txt");
		BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
		Callable<Boolean> task = () -> {
			String line = reader.readLine();
			int count = 0;
			while ( (line = reader.readLine())!=null) {
			  String[] sp = line.split("\\|", -1);
			  String addr = makeRoadAddress(sp);
			  out.write(addr+"\n");
			  System.out.println(++count + "");
			  if(count>5)
				  return true;
			}
			return true;
		};
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Boolean> future = executor.submit(task);
		future.get();
		reader.close();
		out.close();
	}
	
	public void searchAddress() throws UnknownHostException, InterruptedException, ExecutionException, UnsupportedEncodingException {
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		addressElasticSearch.init();
		SearchResponse res = addressElasticSearch.search("강동롯데캐슬");
		for(SearchHit hit : res.getHits())
			System.out.println(hit.getSourceAsString());
	}
	
	//@Test
	public void insertAddressToElasticSearch() throws InterruptedException, ExecutionException, IOException {
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		addressElasticSearch.init();
		addressElasticSearch.setMappings();

		List<ElasticSourcePair> pairs = new ArrayList<>();
		InputStream is = getClass().getResourceAsStream("/static/seoul.txt");
		BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
		Callable<Boolean> task = () -> {
			String line = reader.readLine();
			int count = 0;
			while ( (line = reader.readLine())!=null) {
			  String[] sp = line.split("\\|", -1);
			  Document doc = CommonAddressUtils.makeDocument(sp);
			  addressElasticSearch.insert(new ElasticSourcePair((String)doc.get("buildid"), doc.toJson()));
			  /*pairs.add(new ElasticSourcePair((String)doc.get("buildid"), doc.toJson()));
			  if(pairs.size() >= MAX_DOC_SIZE) {
				  addressElasticSearch.insertAddress(servicesConfig.getElasticType(), pairs);
				  pairs.clear();
			  }*/
			  System.out.println(++count + "");
			}
			//if(pairs.size()>0)
			//	addressElasticSearch.insertAddress(servicesConfig.getElasticType(), pairs);
			return true;
		};
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Boolean> future = executor.submit(task);
		future.get();
		reader.close();

	}
	
	private String makeRoadAddress(String[] sp) throws UnsupportedEncodingException {
		return sp[13] + "," + sp[1] +" "+ sp[3]+" "+(sp[5].length()>0?" ":"")+sp[8]+" "+sp[11]+ (sp[12].equals("0")==false?"-"+sp[12]:"");
	}

}
