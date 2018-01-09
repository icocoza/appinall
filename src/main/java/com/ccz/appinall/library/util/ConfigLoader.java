package com.ccz.appinall.library.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigLoader {
	
	private static ConfigLoader s_pThis;
	public static ConfigLoader getInst() {		return s_pThis = (s_pThis == null ? new ConfigLoader() : s_pThis);	}
	public static void freeInst() {		s_pThis = null;		}
	
	String configFilename;
	Properties props = new Properties();
	
	public void loadConfig() {
		loadConfig("config.cfg");
	}
	
	public void loadConfig(String filename) {
		props = new Properties();
		configFilename = filename;
		
		InputStream is = null;
	    try {
	        File file = new File(configFilename);
	        is = new FileInputStream(file);
	    }
	    catch (Exception e) { 
			is = null;
		}
	 
	    try {
	        if (is == null) {
	            is = getClass().getResourceAsStream(configFilename);
	        }	 
	        props.load(is);
	    }
	    catch (Exception e) {
		}	 
	}
	
	public int getInt(String name) {
		return new Integer(props.getProperty(name, "0"));
	}

	public int getInt(String name, int defaultValue) {
		return new Integer(props.getProperty(name, defaultValue+""));
	}
	
	public long getLong(String name) {
		return new Long(props.getProperty(name, "0"));
	}

	public long getLong(String name, int defaultValue) {
		return new Long(props.getProperty(name, defaultValue+""));
	}
	
	public String getString(String name) {
		return props.getProperty(name, "");
	}
	
	public String getString(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}
	
	public void put(String name, int value) {
		put(name, ""+value);
	}
	
	public void put(String name, int value, String comments) {
		put(name, ""+value, comments);
	}
	public void put(String name, long value) {
		put(name, ""+value);
	}
	
	public void put(String name, long value, String comments) {
		put(name, ""+value, comments);
	}

	public void put(String name, String value) {
		try {
			props.setProperty(name, value);
			File file = new File(configFilename);
	        OutputStream out = new FileOutputStream( file );
			props.store(out, "Properties");
		} catch (Exception e) {
		}        
	}
	
	public void put(String name, String value, String comments) {
		try {
			props.setProperty(name, value);
			File file = new File(configFilename);
	        OutputStream out = new FileOutputStream(file);
			props.store(out, comments);
		} catch (Exception e) {
		}        
	}

}
