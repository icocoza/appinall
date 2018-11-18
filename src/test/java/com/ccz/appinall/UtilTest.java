package com.ccz.appinall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbCommonManager;
import com.ccz.appinall.library.module.scrap.HtmlNode;
import com.ccz.appinall.library.module.scrap.HtmlScrapper;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.domain.stock.DailySiseParser;
import com.ccz.appinall.services.service.schedule.StockDataScheduler;

@RunWith(SpringRunner.class)
@SpringBootTest

public class UtilTest {
	@Autowired ServicesConfig servicesConfig;
	@Autowired StockDataScheduler stockDataScheduler;
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
	public void testMobileNo() {
		String name = "성병규";
		
		if(StrUtil.isHangul("0102973924"))
			System.out.println("hangul");
		if(StrUtil.isHangul("강"))
			System.out.println("hangul");
		if(StrUtil.isHangul("성병규"))
			System.out.println("hangul");
		if(StrUtil.isHangul("강타A"))
			System.out.println("hangul");
		if(StrUtil.isHangul("abcdefg"))
			System.out.println("hangul");
		if(StrUtil.isHangul("강타-"))
			System.out.println("hangul");
		if(StrUtil.isHangul("강타1"))
			System.out.println("hangul");

		if(isLicense("123"))
			System.out.println("kinds of license");
		if(isLicense("1234"))
			System.out.println("kinds of license");
		if(isLicense("12345"))
			System.out.println("kinds of license");
		if(isLicense("123456"))
			System.out.println("kinds of license");
		if(isLicense("12-34"))
			System.out.println("kinds of license");
		if(StrUtil.isMobile("01029739242"))
			System.out.println("mobile");
		if(StrUtil.isMobile("0102973924"))
			System.out.println("mobile");
		if(StrUtil.isMobile("010297392425"))
			System.out.println("mobile");
		if(StrUtil.isMobile("010-2973-9242"))
			System.out.println("mobile");
		if(StrUtil.isMobile("10297392420"))
			System.out.println("mobile");
		if(StrUtil.isMobile("01029739"))
			System.out.println("mobile");
		if(StrUtil.isMobile("01929739244"))
			System.out.println("mobile");

	}
    public static boolean isLicense(String keyword) { //check last 4 or 5 digits
        if (keyword == null) return false;
        Pattern pattern = Pattern.compile("\\d{4,5}");	//
        Matcher  matcher = pattern.matcher(keyword);
        if(matcher.matches())
            return true;
        return false;
    }
	@Test
	@Ignore
	public void CrawlerWeb() throws IOException {
        if(DbCommonManager.getInst().createCommonDatabase(servicesConfig.getAdminMysqlUrl(), 
        		servicesConfig.getAdminMysqlOption(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw()) == false) {
        	return;
        }
        if(DbCommonManager.getInst().initApp(4,  4) == false) {
    		return;
        }

        
        stockDataScheduler.doSchedule();
		//DailySiseParser dsp = new DailySiseParser(71970, "", "https://finance.naver.com/item/sise.nhn?code=071970");
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
	
	@Test
	@Ignore
	public void testSort() {
		int[] A = {3, 8, 9, 7, 6};
		Solution s = new Solution();
		//System.out.println(s.solution(A)+"");
		//System.out.println(s.binaryGap(10001));
		int[] B = s.moveArray(A, 3);
		for(int i=0; i<B.length; i++)
			System.out.println(B[i]+"");
	}
	
	class Solution {
	    public int solution(int[] A) {
	        // write your code in Java SE 8
	        if(A==null || A.length<1)
	            return 1;
	        Integer[] what = Arrays.stream( A ).boxed().toArray( Integer[]::new );
	        Arrays.sort(what, new Comparator<Integer>(){  
	            @Override  
	            public int compare(Integer first, Integer second){  
	                 return first - second;
	            }  
	        }); 
	        //quickSort(A, 0, A.length-1);
	        for(int i=0; i<what.length-1; i++) {
	            if(what[i]<1 || what[i]==what[i+1])
	                continue;
	            if(what[i]+1 < what[i+1])
	                return what[i]+1;
	        }
	        int value = what[what.length-1]+1;
	        return value<1 ? 1 : value;
	    }
	    
	    public  int binaryGap(int x) {
	        String binaryStr = Integer.toBinaryString(x);
	        System.out.println(binaryStr);
	        int count = 0;
	        int maxcount = 0;
	        for(int i=0; i<binaryStr.length(); i++) {
	            if(binaryStr.charAt(i)=='0')
	            	count++;
	            else if(binaryStr.charAt(i)=='1') {
	            	if(maxcount<count)
	            		maxcount=count;
	            	count = 0;
	            }
	        }
	        return maxcount;
	    }
	    
	    public int[] moveArray(int[] A, int K) {
	        // write your code in Java SE 8
	    	int move = A.length>K? K: A.length % K;
	    	int[] B = new int[A.length];
	    	for(int i=0; i<A.length; i++) {
	    		int idx = i+move > A.length? (i+move)-A.length: i+move;
	    		idx-=1;
	    		B[i] = A[idx];
	    	}
	    	return B;
	    }
	}
}
