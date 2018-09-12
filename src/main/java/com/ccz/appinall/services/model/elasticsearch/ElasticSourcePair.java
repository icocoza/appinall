package com.ccz.appinall.services.model.elasticsearch;

import org.bson.Document;

public class ElasticSourcePair {
	public String id;
	public String json;
	
	public ElasticSourcePair(String id, String json) {
		this.id = id;
		this.json = json;
	}
	
	public ElasticSourcePair(Document doc) {
		this.id = (String)doc.get("buildid");
		this.json = doc.toJson();
	}
	
}
