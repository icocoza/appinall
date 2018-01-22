package com.ccz.appinall.services.entity.elasticsearch;

import org.bson.Document;

public class ElasticSourcePair {
	public String id;
	public String json;
	
	private String grsId;
	
	public ElasticSourcePair(String id, String json) {
		this.id = id;
		this.json = json;
	}
	
	public ElasticSourcePair(Document doc) {
		this.id = (String)doc.get("buildmgr");
		this.json = doc.toJson();
	}
}

/*
	private Document makeElasticDocument(String[] sp) {
		Document doc = new Document();
		doc.put("zip", sp[0]);	//우편번호 
		doc.put("sido", sp[1]);	//시
		doc.put("sigu", sp[3]);	//시군구
		doc.put("eub", sp[5]);	//읍면 
		doc.put("rname", sp[8]);	//도로명 
		doc.put("buildno", sp[11]);	//건물번호본번 
		doc.put("buildsubno", sp[12]);	//건물번호부번 
		doc.put("buildmgr", sp[13]);	//건물관리번호 
		doc.put("delivery", sp[14]);	//다량배달처명 
		doc.put("buildname", sp[15]);	//시군구용건물명 
		doc.put("dongname", sp[17]);	//법정동명 
		doc.put("liname", sp[18]);	//리명 
		doc.put("hjdongname", sp[19]);	//행정동명 
		doc.put("jino", sp[21]);	//지번본번 
		doc.put("jisubno", sp[23]);	//지번부번 
		doc.put("base", Boolean.parseBoolean(sp[10])); //지하유무 
		doc.put("mnt", Boolean.parseBoolean(sp[20]));	//산유무 
		return doc;
	}
*/