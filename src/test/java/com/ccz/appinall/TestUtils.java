package com.ccz.appinall;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.library.util.StrUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUtils {

	@Test
	@Ignore
	public void testUtils() {
		System.out.println(StrUtil.isFileName("testfile.png"));
		System.out.println(StrUtil.isFileName("1testfile.png"));
		System.out.println(StrUtil.isFileName("!@testfile.png"));
	}
}
