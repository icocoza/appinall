package com.ccz.appinall.library.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
	
	private static ResourceLoader s_pThis;
	public static ResourceLoader getInst() {	return s_pThis = (s_pThis==null ? new ResourceLoader() : s_pThis); }
	public static void freeInst() {	s_pThis = null;	}
	
	public void loadConfig() {
		configInputStream = loadConfig("config.cfg");
	}
	
	private InputStream configInputStream = null;
	public InputStream loadConfig(String filename) {
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
	
	public InputStream getStream() {
		return configInputStream;
	}
	
	public String getAllText() throws IOException {
		return getAllText(configInputStream, "UTF-8");
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
