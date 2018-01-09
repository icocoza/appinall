package com.ccz.appinall.library.module.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticTransportMgr {
	
	static ElasticTransportMgr s_pThis;
	public static ElasticTransportMgr getInst() { return s_pThis = (s_pThis == null) ? new ElasticTransportMgr() : s_pThis;	}
	public static void freeInst() {		s_pThis = null;		}
	
	TransportClient transportClient;
	
	@SuppressWarnings("resource")
	public void init(String clusterName, String nodeName, String hostNameOrIp, int port) throws UnknownHostException {
		if(transportClient!=null)
			return;
		Settings settings = Settings.builder().put("cluster.name", clusterName).put("node.name", nodeName).build();
		transportClient = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostNameOrIp), port));
	}
	
	public boolean createIndex(String index){
		return transportClient.admin().indices().prepareCreate(index).get().isAcknowledged();
	}
	
	public boolean createIndex(String index, String settings) {
		return transportClient.admin().indices().prepareCreate(index).setSettings(settings, XContentType.JSON).get().isAcknowledged();
	}

	public boolean createIndex(String index, int shardCnt, int replicaCnt) {
		Settings.Builder settingsBuilder = Settings.builder();
		settingsBuilder.put("number_of_shards",shardCnt);
		settingsBuilder.put("number_of_replicas",replicaCnt);
		return transportClient.admin().indices().prepareCreate(index).setSettings(settingsBuilder).get().isAcknowledged();
	}

	public boolean deleteIndex(String index) {
		return transportClient.admin().indices().prepareDelete(index).get().isAcknowledged();
	}
	
	public boolean putSettings(String index, String type, String settings) {
		return transportClient.admin().indices().prepareUpdateSettings(index).setSettings(settings, XContentType.JSON).get().isAcknowledged();
	}
	
	public boolean putMappings(String index, String type, String mappings) {
		return transportClient.admin().indices().preparePutMapping(index).setType(type).setSource(mappings, XContentType.JSON).get().isAcknowledged();
	}
	
	public IndexResponse insert(String json, String index, String type, String id) {
		if(id==null || id.length()<1)
			return transportClient.prepareIndex(index, type).setSource(json, XContentType.JSON).get();
		return transportClient.prepareIndex(index, type, id).setSource(json, XContentType.JSON).get();
	}
	
	public UpdateResponse update(Map<String, Object> json, String index, String type, String id) throws InterruptedException, ExecutionException {
		UpdateRequest update = new UpdateRequest().index(index).type(type).id(id).doc(json);
		return transportClient.update(update).get();
	}
	
	public UpdateResponse upsert(Map<String, Object> old, Map<String, Object> newone, String index, String type, String id) throws InterruptedException, ExecutionException {
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(newone);
		UpdateRequest update = new UpdateRequest().index(index).type(type).id(id).doc(old).upsert(indexRequest);
		return transportClient.update(update).get();
	}
	
	public GetResponse get(String index, String type, String id) {
		return transportClient.prepareGet(index, type, id).get();
	}
	public SearchResponse matchSearch(String index, String type, String field, String word) throws InterruptedException, ExecutionException {
		return transportClient.prepareSearch(index).setTypes(type).setQuery(QueryBuilders.matchQuery(field, word)).execute().get();
	}
	
	public DeleteResponse remove(String index, String type, String id) {
		return transportClient.prepareDelete(index, type, id).get();
	}
	
}
