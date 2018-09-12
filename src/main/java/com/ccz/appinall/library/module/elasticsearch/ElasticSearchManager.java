package com.ccz.appinall.library.module.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ElasticSearchManager {
	@Autowired private ElasticTransportMgr elasticTransportMgr;
	@Autowired private ElasticRestApiMgr elasticRestApiMgr;
	
	public boolean init(String clusterName, String nodeName, String hostNameOrIp, int port) {
		try {
			elasticTransportMgr.init(clusterName, nodeName, hostNameOrIp, port);
			elasticRestApiMgr.init(hostNameOrIp, 9200);
			return true;
		}catch(Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public boolean createIndex(String index, String settings) {
		if(elasticTransportMgr.hasIndexInCluster(index) == false)
			return elasticTransportMgr.createIndex(index, settings);
		return true;
	}
	
	public boolean hasIndex(String index) {
		return elasticTransportMgr.hasIndex(index);
	}
	
	public boolean dropIndex(String index) {
		try {
			return elasticTransportMgr.deleteIndex(index);
		}catch(Exception e) {
			System.out.println(index + "is not found.");
			return false;
		}
	}

	public void setSettings(String index, String settings) {
		if(elasticTransportMgr.hasSettings(index) == false)
			elasticTransportMgr.putSettings(index, settings);
	}

	public void setMappings(String index, String type, String mappings) {
		if(elasticTransportMgr.hasMappings(index, type) == false)
			elasticTransportMgr.putMappings(index, type, mappings);
	}
	
	public boolean bulkInsert(String index, String type, List<ElasticSourcePair> pairs) {
		return elasticTransportMgr.bulkInsert(index, type, pairs);
	}
	
	public void insertRecord(String index, String type, ElasticSourcePair pair) {
		elasticTransportMgr.insert(index, type, pair.id, pair.json).forcedRefresh();
	}

	public void updateRecord(String index, String type, ElasticSourcePair pair) throws InterruptedException, ExecutionException {
		elasticTransportMgr.update(index, type, pair.id, pair.json).forcedRefresh();
	}

	public SearchResponse search(String index, String type, String query) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
		return elasticTransportMgr.querySearch(index, type, query);
	}
		
	public String searchByRest(String index, String query) throws JsonProcessingException, IOException {
		return elasticRestApiMgr.querySearch(index, query);
	}
	
	public String bulkInsertByRest(String index, String type, List<Document> docs) throws IOException {
		return elasticRestApiMgr.bulkInsert(index, type, docs);
	}

}
