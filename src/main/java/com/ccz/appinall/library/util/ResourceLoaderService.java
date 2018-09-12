package com.ccz.appinall.library.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceLoaderService {
	
	@Autowired ResourceLoader resourceLoader;
	
	public String loadText(String filepath) {
		try {
			return resourceLoader.getAllText(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String loadText(String filepath, String encoding) {
		try {
			return resourceLoader.getAllText(filepath, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
