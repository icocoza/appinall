package com.ccz.appinall.services.action.board;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.services.controller.board.ElkBoard;
import com.ccz.appinall.services.controller.board.RecDataBoard;
import com.ccz.appinall.services.controller.board.RecDataBoard.AddBoard;
import com.ccz.appinall.services.enums.EAddrType;
import com.ccz.appinall.services.repository.elasticsearch.BoardElasticSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardCommandTest {
	
	@Autowired ServicesConfig servicesConfig;
	@Autowired ElasticSearchManager elasticSearchManager;
	@Autowired BoardElasticSearch boardElasticSearch;

	private void init()  throws UnknownHostException{
		elasticSearchManager.init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort());
		boardElasticSearch.init();
	}
	
	@Test
	@Ignore
	public void addBoardTest() throws UnknownHostException {
		init();
		AddBoard addBoard = new RecDataBoard().new AddBoard(); 
		addBoard.title = "title Stephen";
		addBoard.content = "content";
		if(this.boardElasticSearch.addBoard("boardId2", "categoryId", "Stephen", addBoard) == true)
			log.info("success insert to elk");
		else
			log.info("failed insert to elk");
		
		addBoard = new RecDataBoard().new AddBoard(); 
		addBoard.title = "title Tom";
		addBoard.content = "content Stephen";
		this.boardElasticSearch.addBoard("boardId3", "categoryId", "Stephen", addBoard);
	}
	
	@Test
	public void searchBoardTest() throws InterruptedException, ExecutionException, IOException {
		init();
		try {
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
	
}
