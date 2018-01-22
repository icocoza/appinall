package com.ccz.appinall.library.module.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.index.search.MultiMatchQuery.QueryBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.ccz.appinall.services.entity.elasticsearch.ElasticSourcePair;

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
		transportClient = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(hostNameOrIp), port));
	}
	
	public boolean hasIndex(String index) {
		try {
			return transportClient.admin().indices().prepareExists(index).execute().actionGet().isExists();
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean hasIndexInCluster(String index) {
	    IndexMetaData indexMetaData = transportClient.admin().cluster()
	            .state(Requests.clusterStateRequest())
	            .actionGet()
	            .getState()
	            .getMetaData()
	            .index(index);

	    return (indexMetaData != null);
	}
	
	public boolean createIndex(String index){
		return transportClient.admin().indices().prepareCreate(index).get().isAcknowledged();
	}
	
	public boolean createIndex(String index, String settings) {
		if(settings!=null)
			return transportClient.admin().indices().prepareCreate(index).setSettings(settings, XContentType.JSON).get().isAcknowledged();
		return transportClient.admin().indices().prepareCreate(index).get().isAcknowledged();
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
	
	public boolean hasSettings(String index) {
		GetSettingsResponse gsr = transportClient.admin().indices().prepareGetSettings(index).get();
		System.out.println(gsr.getSetting(index, "analysis"));
		return gsr.getSetting(index, "analysis") != null;
	}
	
	public boolean putSettings(String index, String settings) {
		return transportClient.admin().indices().prepareUpdateSettings(index).setSettings(settings, XContentType.JSON).get().isAcknowledged();
	}
	
	public boolean hasMappings(String index, String type) {
		GetMappingsResponse gmr = transportClient.admin().indices().prepareGetMappings(index).get();
		return gmr.getMappings().containsKey("southkorea");
	}
	
	public boolean putMappings(String index, String type, String mappings) {
		return transportClient.admin().indices().preparePutMapping(index).setType(type).setSource(mappings, XContentType.JSON).get().isAcknowledged();
	}
	
	public boolean bulkInsert(String index, String type, List<ElasticSourcePair> pairs) {
		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
		for(ElasticSourcePair pair : pairs) {
			IndexRequestBuilder indexRequestBuilder = transportClient.prepareIndex(index, type, pair.id);
            indexRequestBuilder.setSource(pair.json, XContentType.JSON).setRefreshPolicy("true");
            bulkRequestBuilder.add(indexRequestBuilder);
		}
		 BulkResponse bulkResponse = bulkRequestBuilder.get();
	     return bulkResponse.hasFailures();
	}
	
	public IndexResponse insert(String index, String type, String id, String json) {
		if(id==null || id.length()<1)
			return transportClient.prepareIndex(index, type).setSource(json, XContentType.JSON).get();
		return transportClient.prepareIndex(index, type, id).setSource(json, XContentType.JSON).get();
	}
	
	public UpdateResponse update(String index, String type, String id, String json) throws InterruptedException, ExecutionException {
		UpdateRequest update = new UpdateRequest().index(index).type(type).id(id).doc(json, XContentType.JSON);
		return transportClient.update(update).get();
	}
	
	public UpdateResponse upsert(String index, String type, String id, Map<String, Object> old, Map<String, Object> newone) throws InterruptedException, ExecutionException {
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
	
	public SearchResponse multiMatchSearch(String index, String type, String word, String... fieldNames) throws InterruptedException, ExecutionException {
		MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(word, fieldNames).type(MatchQuery.Type.PHRASE_PREFIX);
		return transportClient.prepareSearch(index).setTypes(type).setQuery(builder).execute().get();
	}
	
	public SearchRequestBuilder getAll(String index, String type) {
		return transportClient.prepareSearch(index).setTypes(type).setQuery(QueryBuilders.matchAllQuery());
	}

}
