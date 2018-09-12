package com.ccz.appinall.library.module.elasticsearch;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.bson.Document;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

@Component
public class ElasticRestApiMgr {

//	static ElasticRestApiMgr s_pThis;
//	public static ElasticRestApiMgr getInst() { return s_pThis = (s_pThis == null) ? new ElasticRestApiMgr() : s_pThis;	}
//	public static void freeInst() {		s_pThis = null;		}

	RestClient restClient;
	
	public void init(String hostNameOrIp, int port) {
		if(restClient == null)
			restClient = RestClient.builder(new HttpHost(hostNameOrIp, 9200, "http")).build();
	}
	
	public void close() throws IOException {
		if(restClient!=null)
			restClient.close();
	}
	
	public String querySearch(String index, String query) throws IOException {
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		String endPoint = String.format("/%s/_search", index);
		Response res = restClient.performRequest("POST", endPoint, params, entity);
		return EntityUtils.toString(res.getEntity());
	}

	public String querySearch(String index, String type, String query) throws IOException {
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		String endPoint = String.format("/%s/%s/_search", index, type);
		Response res = restClient.performRequest("POST", endPoint, params, entity);
		return EntityUtils.toString(res.getEntity());
	}

	public String bulkInsert(String index, String type, List<Document> docs) throws IOException {
		Map<String, String> params = Collections.emptyMap();
		String actionMetaData = String.format("{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\" } }%n", index, type);
		String bulkBody = docs.stream().map(x -> actionMetaData + x.toJson()).collect(Collectors.joining("\n"));
		HttpEntity entity = new NStringEntity(bulkBody, ContentType.APPLICATION_JSON);
		String endPoint = String.format("/%s/%s/_bulk", index, type);
		Response res = restClient.performRequest("POST", endPoint, params, entity);
		return EntityUtils.toString(res.getEntity());
	}

}
