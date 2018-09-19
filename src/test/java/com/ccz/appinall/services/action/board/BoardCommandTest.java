package com.ccz.appinall.services.action.board;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.board.BoardCommandAction;
import com.ccz.appinall.services.controller.board.RecDataBoard;
import com.ccz.appinall.services.controller.board.RecDataBoard.AddBoard;
import com.ccz.appinall.services.repository.elasticsearch.BoardElasticSearch;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardCommandTest {
	
	@Autowired ServicesConfig servicesConfig;
	@Autowired ElasticSearchManager elasticSearchManager;
	@Autowired BoardElasticSearch boardElasticSearch;
	@Autowired BoardCommandAction boardCommandAction;

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
		//if(this.boardElasticSearch.addBoard("boardId2", "categoryId", "Stephen", addBoard) == true)
		//	log.info("success insert to elk");
		//else
		//	log.info("failed insert to elk");
		
		addBoard = new RecDataBoard().new AddBoard(); 
		addBoard.title = "title Tom";
		addBoard.content = "content Stephen";
		this.boardElasticSearch.addBoard("boardId3", "categoryId", "Stephen", addBoard);
	}
	
	@Test
	@Ignore
	public void searchBoardTest() throws InterruptedException, ExecutionException, IOException {
		init();
		try {
		}catch(Exception e) {
			//log.error(e.getMessage());
		}
	}
	
	@Test
	@Ignore
	public void scrapBoardTest() {
		String url = "https://m.post.naver.com/viewer/postView.nhn?volumeNo=16608647&memberNo=35869883";
		System.out.println(StrUtil.getUrlExt(url));
		boardCommandAction.findAndAddScrap("apartment", "https://m.post.naver.com/viewer/postView.nhn?volumeNo=16608647&memberNo=35869883");
	}
	
}
