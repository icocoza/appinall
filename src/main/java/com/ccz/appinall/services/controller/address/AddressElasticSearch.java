package com.ccz.appinall.services.controller.address;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;

import com.ccz.appinall.library.module.elasticsearch.ElasticRestApiMgr;
import com.ccz.appinall.library.module.elasticsearch.ElasticTransportMgr;
import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AddressElasticSearch {
	
	static AddressElasticSearch s_pThis;
	public static AddressElasticSearch getInst() { return s_pThis = (s_pThis == null) ? new AddressElasticSearch() : s_pThis;	}
	public static void freeInst() {		s_pThis = null;		}

	private String indexDb, typeTable;
	private ElasticTransportMgr elasticTransportMgr;
	
	public boolean init(String clusterName, String nodeName, String hostNameOrIp, int port, String index, String type, String settings) throws UnknownHostException {
		this.indexDb = index;
		this.typeTable = type;
		elasticTransportMgr = new ElasticTransportMgr();
		elasticTransportMgr.init(clusterName, nodeName, hostNameOrIp, port);
		if(elasticTransportMgr.hasIndexInCluster(indexDb) == false)
			return elasticTransportMgr.createIndex(indexDb, settings);
		ElasticRestApiMgr.getInst().init(hostNameOrIp, 9200);
		return true;
	}

	public void setSettings(String settings) {
		if(elasticTransportMgr.hasSettings(indexDb) == false)
			elasticTransportMgr.putSettings(indexDb, settings);
	}

	public void setMappings(String mappings) {
		//if(elasticTransportMgr.hasMappings(indexDb, typeTable) == false)
		elasticTransportMgr.putMappings(indexDb, typeTable, mappings);
	}
	
	public boolean bulkInsert(List<ElasticSourcePair> pairs) {
		return elasticTransportMgr.bulkInsert(indexDb, typeTable, pairs);
	}
	
	public void insertAddress(ElasticSourcePair pair) {
		elasticTransportMgr.insert(indexDb, typeTable, pair.id, pair.json).forcedRefresh();
	}

	public void updateAddress(ElasticSourcePair pair) throws InterruptedException, ExecutionException {
		elasticTransportMgr.update(indexDb, typeTable, pair.id, pair.json).forcedRefresh();
	}

	public SearchResponse searchAddress(String query) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
		return elasticTransportMgr.querySearch(indexDb, typeTable, query);
	}
	
	public SearchResponse searchAddress(AddressInference ai) throws InterruptedException, ExecutionException, JsonProcessingException, UnsupportedEncodingException {
		return elasticTransportMgr.querySearch(indexDb, typeTable, ai.getElasticSearchQuery());
	}
	
	public String searchAddresByRest(AddressInference ai) throws JsonProcessingException, IOException {
		return ElasticRestApiMgr.getInst().querySearch(indexDb, ai.getElasticSearchQuery());
	}
	
	public JsonNode searchAddresByRestJson(AddressInference ai) throws JsonProcessingException, IOException {
		String res = this.searchAddresByRest(ai);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(res);
	}

}
