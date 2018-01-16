package com.ccz.appinall.library.util;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

public class StrUtil {
	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_PATTERN = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
	static public boolean isEmail(String email) {
	    return email.matches(EMAIL_PATTERN);
	}

	static public boolean isPhone(String phone) {
	    return phone.matches(EMAIL_PATTERN);
	}

	static public boolean isAlphaNumeric(String name) {
	    return name.matches("^[a-zA-Z0-9]+$");
	}
	
	static public String getUuid(String prefix) {
		return prefix + UUID.randomUUID().toString();
	}
	
	static public String getSha1(String data) {
		return DigestUtils.sha1Hex(data);
	}
	static public String getSha256(String data) {
		return DigestUtils.sha256Hex(data);
	}

	static int appendix_count=0;
	static public String getSha1Uuid(String prefix) {
		return getSha1Uuid(prefix, String.format("%04d", ++appendix_count%10000));
	}

	static public String getSha256Uuid(String prefix) {
		return getSha1Uuid(prefix, String.format("%04d", ++appendix_count%10000));
	}

	static public String getSha1Uuid(String prefix, String footer) {
		return prefix + DigestUtils.sha1Hex(UUID.randomUUID().toString() + System.currentTimeMillis()) + footer;
	}
	static public String getSha256Uuid(String prefix, String footer) {
		return prefix + DigestUtils.sha256Hex(UUID.randomUUID().toString() + System.currentTimeMillis()) + footer;
	}

	private static String hostIp = null;
	public static String getHostIp() {
		if(hostIp!=null)
			return hostIp;
		try {
			String ip = null;

			boolean isLoopBack = true;
			Enumeration<NetworkInterface> en;		
			en = NetworkInterface.getNetworkInterfaces();

 			while(en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				if (ni.isLoopback())
					continue;

				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while(inetAddresses.hasMoreElements()) { 
					InetAddress ia = inetAddresses.nextElement();
					if (ia.getHostAddress() != null && ia.getHostAddress().indexOf(".") != -1) {
						ip = ia.getHostAddress();
						System.out.println(ip);
						isLoopBack = false;
						break;
					}
				}
				if (!isLoopBack)
					break;
			}
 			hostIp = ip;
			return ip;
		} catch (SocketException e) {
			return null;
		}
	}
	
	public static String getFileName(String filename) {
		int slash = filename.lastIndexOf('/');
		if(slash<0)
			slash = 0;
		int dot = filename.lastIndexOf('.');
		if (dot > 0) 
		    return filename.substring(slash+1, dot);
		else if(slash>=0)
			return filename.substring(slash);
		return filename;
	}
	
	public static String getFileExt(String filename) {
		String extension = "";

		int i = filename.lastIndexOf('.');
		if (i > 0) 
		    extension = filename.substring(i+1);
		return extension;
	}
	
	public String getFileExt(File file) {
	    String name = file.getName();
	    try {
	        return name.substring(name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
}
