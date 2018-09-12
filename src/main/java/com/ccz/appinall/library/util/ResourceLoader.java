package com.ccz.appinall.library.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

@Component
public class ResourceLoader {
	
	private InputStream loadConfig(String filename) {
		InputStream is = null;
	    try {
	        File file = new File(filename);
	        is = new FileInputStream(file);
	    }
	    catch (Exception e) { 
			is = null;
		}
	 
	    try {
	        if (is == null) {
	            is = getClass().getResourceAsStream(filename);
	        }	 
	    }
	    catch (Exception e) {
		}	 
	    return is;
	}
	
	public String getAllText(String filepath) throws IOException {
		InputStream is = loadConfig(filepath);
		return getAllText(is, "UTF-8");
	}
	
	public String getAllText(String filepath, String encoding) throws IOException {
		InputStream is = loadConfig(filepath);
		return getAllText(is, encoding);
	}
	
	private String getAllText(InputStream inputStream, String encoding) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(encoding);
	}
	
}
