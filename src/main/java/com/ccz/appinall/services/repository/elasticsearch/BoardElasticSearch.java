package com.ccz.appinall.services.repository.elasticsearch;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ccz.appinall.services.controller.address.AddressInference;
import com.ccz.appinall.services.controller.board.ElkBoard;
import com.ccz.appinall.services.controller.board.RecDataBoard.AddBoard;
import com.ccz.appinall.services.model.elasticsearch.ElasticSourcePair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BoardElasticSearch extends CommonElasticSearch {

	@Value("${elastic.board.index}")
    private String index;
	
	@Value("${elastic.board.type}")
    private String type;

	public boolean init() throws UnknownHostException {
		String settings = resourceLoaderService.loadText("/static/addrsetting.cfg");	//셋팅은 index 생성과 함께 만들어져야 한다.
		String mappings = resourceLoaderService.loadText("/static/boardmapping.cfg");
		
		return super.init(index, type, settings, mappings);
	}
	
	public boolean addBoard(String boardid, String category, String writer, AddBoard board) {
		ElkBoard elkBoard = new ElkBoard(boardid, category, writer, board);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String elkJson = mapper.writeValueAsString(elkBoard);
			super.insert(new ElasticSourcePair(boardid, elkJson));	
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	public String searchBoardByRest(String jsonQuery)  {
		try {
			return elasticSearchManager.searchByRest(index, jsonQuery);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public List<String> copySearshResultToResponse(ArrayNode arrNode) {
		ObjectMapper mapper = new ObjectMapper();
		//ArrayNode copyArrNode = mapper.createArrayNode();
		List<String> list = new ArrayList<>();
		for(int i=0; i<arrNode.size(); i++) {
			JsonNode srcNode = arrNode.get(i).get("_source");
			if(srcNode == NullNode.instance)
				continue;
			list.add(srcNode.asText());
		}
		return list;
	}



}
