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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.application.server.http.admin.business.service.ResourceLoaderService;
import com.ccz.appinall.services.ServicesConfig;
import com.ccz.appinall.services.entity.elasticsearch.ElasticSourcePair;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressElasticTests {

	@Autowired
	ResourceLoaderService resourceLoaderService;
	
	@Autowired
	ServicesConfig servicesConfig;
	
	final int MAX_DOC_SIZE = 5;
	final String collectionName = "korea";
	
	@Test
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
	
	public void searchAddress() throws UnknownHostException, InterruptedException, ExecutionException {
		AddressElasticSearch addressElasticSearch = new AddressElasticSearch();
		addressElasticSearch.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
		SearchResponse res = addressElasticSearch.searchAddress(servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), "강동롯데캐슬");
		for(SearchHit hit : res.getHits())
			System.out.println(hit.getSourceAsString());
	}
	
	//@Test
	public void insertAddressToElasticSearch() throws InterruptedException, ExecutionException, IOException {
		String settings = resourceLoaderService.loadText("/static/addrsetting.cfg");	//셋팅은 index 생성과 함께 만들어져야 한다.
		String mappings = resourceLoaderService.loadText("/static/addrkormapping.cfg");

		AddressElasticSearch addressElasticSearch = new AddressElasticSearch();
		addressElasticSearch.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), settings);
		
		addressElasticSearch.setMappings(mappings);

		List<ElasticSourcePair> pairs = new ArrayList<>();
		InputStream is = getClass().getResourceAsStream("/static/seoul.txt");
		BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
		Callable<Boolean> task = () -> {
			String line = reader.readLine();
			int count = 0;
			while ( (line = reader.readLine())!=null) {
			  String[] sp = line.split("\\|", -1);
			  Document doc = makeDocument(sp);
			  addressElasticSearch.insertAddress(new ElasticSourcePair((String)doc.get("buildmgr"), doc.toJson()));
			  /*pairs.add(new ElasticSourcePair((String)doc.get("buildmgr"), doc.toJson()));
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
	
	private Document makeDocument(String[] sp) {
		Document doc = new Document();
		doc.put("zip", sp[0]);	//우편번호 
		doc.put("sido", sp[1]);	//시
		doc.put("sigu", sp[3]);	//시군구
		doc.put("eub", sp[5]);	//읍면 
		doc.put("rcode", sp[7]); //도로명코드 
		doc.put("rname", sp[8]);	//도로명 
		doc.put("buildmgr", sp[13]);	//건물관리번호 
		doc.put("delivery", sp[14]);	//다량배달처명 
		doc.put("buildname", sp[15]);	//시군구용건물명 
		doc.put("dongname", sp[17]);	//법정동명 
		doc.put("liname", sp[18]);	//리명 
		doc.put("hjdongname", sp[19]);	//행정동명 
		doc.put("buildno", sp[11]);	//건물번호본번 
		doc.put("buildsubno", sp[12]);	//건물번호부번 
		doc.put("dongcode", sp[16]);	//법정동코드 
		doc.put("jino", sp[21]);	//지번본번 
		doc.put("eubseq", sp[22]);	//읍면동일련번호 
		doc.put("jisubno", sp[23]);	//지번부번 
		doc.put("base", sp[10]); //지하유무 
		doc.put("mnt", sp[20]);	//산유무 
		return doc;
	}
	
	private String makeRoadAddress(String[] sp) throws UnsupportedEncodingException {
		return sp[13] + "," + sp[1] +" "+ sp[3]+" "+(sp[5].length()>0?" ":"")+sp[8]+" "+sp[11]+ (sp[12].equals("0")==false?"-"+sp[12]:"");
	}

}
