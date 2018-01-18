package com.ccz.appinall.services.action.address;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;

import com.ccz.appinall.library.module.elasticsearch.ElasticTransportMgr;
import com.ccz.appinall.services.entity.elasticsearch.ElasticSourcePair;

public class AddressElasticSearch {
	private String indexDb, typeTable;
	
	public boolean init(String clusterName, String nodeName, String hostNameOrIp, int port, String index, String type, String settings) throws UnknownHostException {
		this.indexDb = index;
		this.typeTable = type;
		ElasticTransportMgr.getInst().init(clusterName, nodeName, hostNameOrIp, port);
		if(ElasticTransportMgr.getInst().hasIndexInCluster(indexDb) == false)
			return ElasticTransportMgr.getInst().createIndex(indexDb, settings);
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
	
	public boolean insertAddress(List<ElasticSourcePair> pairs) {
		return ElasticTransportMgr.getInst().bulkInsert(indexDb, typeTable, pairs);
	}
	
	public void insertAddress(ElasticSourcePair pair) {
		ElasticTransportMgr.getInst().insert(indexDb, typeTable, pair.id, pair.json).forcedRefresh();
	}
	

	public SearchResponse searchAddress(String index, String type, String word) throws InterruptedException, ExecutionException {
		return ElasticTransportMgr.getInst().multiMatchSearch(index, type, word, "sido", "sigu", "rname", "buildname", "hjdongname", "delivery", "liname", "eub");
	}
	
}
