package com.ccz.appinall.services.action.address;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;

import com.ccz.appinall.library.module.elasticsearch.ElasticRestMgr;
import com.ccz.appinall.library.module.elasticsearch.ElasticTransportMgr;
import com.ccz.appinall.services.entity.elasticsearch.ElasticSourcePair;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AddressElasticSearch {
	private String indexDb, typeTable;
	
	public boolean init(String clusterName, String nodeName, String hostNameOrIp, int port, String index, String type, String settings) throws UnknownHostException {
		this.indexDb = index;
		this.typeTable = type;
		ElasticTransportMgr.getInst().init(clusterName, nodeName, hostNameOrIp, port);
		if(ElasticTransportMgr.getInst().hasIndexInCluster(indexDb) == false)
			return ElasticTransportMgr.getInst().createIndex(indexDb, settings);
		ElasticRestMgr.getInst().init(hostNameOrIp, 9200);
		return true;
	}

	public void setSettings(String settings) {
		if(ElasticTransportMgr.getInst().hasSettings(indexDb) == false)
			ElasticTransportMgr.getInst().putSettings(indexDb, settings);
	}

	public void setMappings(String mappings) {
		//if(ElasticTransportMgr.getInst().hasMappings(indexDb, typeTable) == false)
		ElasticTransportMgr.getInst().putMappings(indexDb, typeTable, mappings);
	}
	
	public boolean bulkInsert(List<ElasticSourcePair> pairs) {
		return ElasticTransportMgr.getInst().bulkInsert(indexDb, typeTable, pairs);
	}
	
	public void insertAddress(ElasticSourcePair pair) {
		ElasticTransportMgr.getInst().insert(indexDb, typeTable, pair.id, pair.json).forcedRefresh();
	}

	public void updateAddress(ElasticSourcePair pair) throws InterruptedException, ExecutionException {
		ElasticTransportMgr.getInst().update(indexDb, typeTable, pair.id, pair.json).forcedRefresh();
	}

	public SearchResponse searchAddress(String query) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
		return ElasticTransportMgr.getInst().querySearch(indexDb, typeTable, query);
	}
	
	public SearchResponse searchAddress(AddressInference ai) throws InterruptedException, ExecutionException, JsonProcessingException, UnsupportedEncodingException {
		return ElasticTransportMgr.getInst().querySearch(indexDb, typeTable, ai.getElasticSearchQuery());
	}
	
	public String searchAddresByRest(AddressInference ai) throws JsonProcessingException, IOException {
		return ElasticRestMgr.getInst().querySearch(indexDb, ai.getElasticSearchQuery());
	}
}
