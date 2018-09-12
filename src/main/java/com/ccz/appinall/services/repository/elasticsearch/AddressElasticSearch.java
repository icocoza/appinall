package com.ccz.appinall.services.repository.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ccz.appinall.services.controller.address.AddressInference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AddressElasticSearch extends CommonElasticSearch{

	@Value("${elastic.address.index}")
    private String index;
	
	@Value("${elastic.address.type}")
    private String type;

	public boolean init() throws UnknownHostException {
		String settings = resourceLoaderService.loadText("/static/addrsetting.cfg");	//셋팅은 index 생성과 함께 만들어져야 한다.
		String mappings = resourceLoaderService.loadText("/static/addrkormapping.cfg");
		
		return super.init(index, type, settings, mappings);
	}

	public void setMappings() {
		String mappings = resourceLoaderService.loadText("/static/addrkormapping.cfg");
		elasticSearchManager.setMappings(index, type, mappings);
	}

	public SearchResponse searchAddr(AddressInference ai) throws InterruptedException, ExecutionException, JsonProcessingException, UnsupportedEncodingException {
		return elasticSearchManager.search(index, type, ai.getElasticSearchQuery());
	}
	
	public String searchAddrByRest(AddressInference ai) throws JsonProcessingException, IOException {
		return elasticSearchManager.searchByRest(index, ai.getElasticSearchQuery());
	}

	public JsonNode searchAddrByRestJson(AddressInference ai) throws JsonProcessingException, IOException {
		String res = this.searchAddrByRest(ai);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(res);
	}

}
