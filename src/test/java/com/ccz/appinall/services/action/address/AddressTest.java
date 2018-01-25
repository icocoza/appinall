package com.ccz.appinall.services.action.address;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressTest {

	private static final Pattern VALID_PATTERN = Pattern.compile("[0-9]+|[A-Z]+");

	@Test
	public void testAddressInference() {
		AddressInference ai = new AddressInference("대구시 달서구 상인동 1149번지");
		System.out.println(ai.toFormat());
		System.out.println(ai.toString());
		ai = new AddressInference("강동구 고덕로 131길 442 강동롯데캐슬 1101");
		System.out.println(ai.toString());
	}
	
	public void testText() {
		String regex = "[0-9][0-9-]*[0-9]";
		if("123-12".matches(regex))
			System.out.println("yes");
		if("12312".matches(regex))
			System.out.println("yes1");
		if("12312-".matches(regex))
			System.out.println("yes2");		
		if("-12312".matches(regex))
			System.out.println("yes3");		
		if("12A12".matches(regex))
			System.out.println("yes4");		
		if("-".matches(regex))
			System.out.println("yes5");	
		String someString = "상화로1149-1";
	    //String regex = "((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))";
		//String regex = "((?<=[ㄱ-ㅎ가-힣])(?=[0-9]))|((?<=[0-9])(?=[ㄱ-ㅎ가-힣]))";
	    System.out.println(Arrays.asList(someString.split(regex)));
	    
		List<String> list = parse("test123");
		for(String s : list)
			System.out.println(s);
		list = parse("456test123");
		for(String s : list)
			System.out.println(s);
	}
	private List<String> parse(String toParse) {
	    List<String> chunks = new LinkedList<String>();
	    Matcher matcher = VALID_PATTERN.matcher(toParse);
	    while (matcher.find()) {
	        chunks.add( matcher.group() );
	    }
	    return chunks;
	}

}
