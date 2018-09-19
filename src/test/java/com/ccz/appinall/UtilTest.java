package com.ccz.appinall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.library.module.scrap.HtmlNode;
import com.ccz.appinall.library.module.scrap.HtmlScrapper;
import com.ccz.appinall.library.util.StrUtil;

@RunWith(SpringRunner.class)
@SpringBootTest

public class UtilTest {
	@Test
	@Ignore
	public void testCollectToList() {
		List<String> strNumList = new ArrayList<>();
		
		strNumList.add("5");
		strNumList.add("9");
		strNumList.add("4");
		strNumList.add("8");
		strNumList.add("3");
		strNumList.add("7");
		strNumList.add("0");
		
		List<Integer> intNumList = strNumList.stream().map(Integer::parseInt).collect(Collectors.toList());
		intNumList.forEach(x->System.out.println(x+""));
			
	}

	@Test
	@Ignore
	public void testWebScrapper() {
		HtmlNode node = HtmlScrapper.doScrap("https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=103&oid=005&aid=0001128057");
		System.out.println(node.getMainTitle());
		System.out.println(node.getSubTitle());
		System.out.println(node.getImageUrl());
		System.out.println(node.getShortBody());
	}
	
	@Test
	@Ignore
	public void testWebProtocolParser() {
		String linkContent = "테스트 URL 테스트 입니다. https://news.v.daum.net/v/20180903150103126?rcmd=rn 두번째 링크는 https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=101&oid=018&aid=0004189465 공갈 링크는 http://www.aaa3423.634 입니다. 어떻게 될까요?";
		List<String> list = StrUtil.extractUrls(linkContent);
		list.forEach(x ->System.out.println(x));
		
		List<HtmlNode> htmls = list.stream().map(x -> HtmlScrapper.doScrap(x)).filter(x -> x.isEmpty()==false).collect(Collectors.toList());
		htmls.forEach(x -> System.out.println(x.getMainTitle()));
	}
}
