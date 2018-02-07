package com.ccz.appinall.application.http.admin.business.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.ccz.appinall.library.util.ResourceLoader;

@Service
public class ResourceLoaderService {

	public String loadText(String filepath) {
		try {
			return ResourceLoader.getInst().getAllText(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String loadText(String filepath, String encoding) {
		try {
			return ResourceLoader.getInst().getAllText(filepath, encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
