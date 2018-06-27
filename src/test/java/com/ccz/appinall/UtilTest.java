package com.ccz.appinall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class UtilTest {
	@Test
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

}
