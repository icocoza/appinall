package com.ccz.appinall.services.repository.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.library.util.ResourceLoaderService;
import com.ccz.appinall.services.controller.address.AddressInference;
import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class CommonElasticSearch {
	
	@Autowired protected ResourceLoaderService resourceLoaderService;
	@Autowired protected ElasticSearchManager elasticSearchManager;

	protected String index, type;
	
	abstract public boolean init() throws UnknownHostException;
		
	protected boolean init(String index, String type, String settings, String mappings) throws UnknownHostException {
		this.index = index;
		this.type = type;
		try {
			boolean result = elasticSearchManager.createIndex(index, settings);
			if(result == true)
				elasticSearchManager.setMappings(index, type, mappings);
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean bulkInsert(List<ElasticSourcePair> pairs) {
		return elasticSearchManager.bulkInsert(index, type, pairs);
	}
	
	public void insert(ElasticSourcePair pair) {
		elasticSearchManager.insertRecord(index, type, pair);
	}

	public SearchResponse search(String query) throws UnsupportedEncodingException, InterruptedException, ExecutionException  {
		return elasticSearchManager.search(index, type, query);
	}

	public String bulkInsertByRest(List<Document> docs) throws IOException {
		return elasticSearchManager.bulkInsertByRest(index, type, docs);
	}

}
