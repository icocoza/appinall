package com.ccz.appinall.library.util;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.mortbay.log.Log;

public class StrUtil {
	
	private static final String URL_PATTERN = "\\(?\\b((http|https)://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
	//private static final String URL_PATTERN = (http\:\/\/){0,1}(www){0,1}[\.]{0,1}[a-zA-Z0-9_]+\.[a-zA-Z0-9_]+(\/{1}[a-zA-Z0-9_\.]+)*;
	public static List<String> extractUrls(String text) {
		ArrayList<String> links = new ArrayList<>();
		 
		Pattern p = Pattern.compile(URL_PATTERN);
		Matcher m = p.matcher(text);
		while(m.find()) {
			String urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")"))
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			links.add(urlStr);
		}
		return links;
	}
	
	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	static public boolean isEmail(String email) {
	    return email.matches(EMAIL_PATTERN);
	}
	
	private static final String PHONE_PATTERN = "^\\(?(\\d{3})\\)?[- ]?(\\d{3,4})[- ]?(\\d{4})$";
	static public boolean isPhone(String phone) {
	    return phone.matches(PHONE_PATTERN);
	}

	static public boolean isAlphaNumeric(String name) {
	    return name.matches("^[a-zA-Z0-9]+$");
	}
	
	static public boolean isFileName(String name) {
		return name.matches("^[A-Za-z0-9-_,\\s]+[.]{1}[A-Za-z]{3}$");
	}

	static String ALPHA_NUMERIC_PATTERN = "((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))";
	static public List<String> splitAlphaNumeric(String str) {
		return Arrays.asList(str.split(ALPHA_NUMERIC_PATTERN));
	}
	static String KOREAN_NUMERIC_PATTERN = "((?<=[ㄱ-ㅎ가-힣])(?=[0-9]))|((?<=[0-9])(?=[ㄱ-ㅎ가-힣]))";
	static public List<String> splitKoreanNumeric(String str) {
		return Arrays.asList(str.split(KOREAN_NUMERIC_PATTERN));
	}
	static String NUMERIC_DASH_PATTERN = "[0-9][0-9-]*[0-9]";
	static public boolean isNumericDash(String str) {
		return str.matches(NUMERIC_DASH_PATTERN);
	}
	static String NUMERIC_PATTERN = "[0-9]";
	static public boolean isNumeric(String str) {
		return str.matches(NUMERIC_DASH_PATTERN);
	}
	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
	static public boolean isImageFile(String s) {
		return s.matches(IMAGE_PATTERN);
	}
	static public String getUuid(String prefix) {
		return prefix + UUID.randomUUID().toString().replaceAll("-", "");
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
				if(ni.getDisplayName().startsWith("u"))
					continue;

				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while(inetAddresses.hasMoreElements()) { 
					
					InetAddress ia = inetAddresses.nextElement();
					if (ia.getHostAddress() != null && ia.getHostAddress().indexOf(".") != -1) {
						ip = ia.getHostAddress();
						isLoopBack = false;
						break;
					}
				}
				if (!isLoopBack)
					break;
			}
 			Log.info("Local System IP Address: " + ip);
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
	
	public static String getUrlExt(String url) {
		int i = url.lastIndexOf('/');
		if(i<0)
			return "";
		String ext = getFileExt(url.substring(i+1));
		if(ext.contains("?")) {
			return ext.substring(0, ext.indexOf("?"));
		}
		return ext;
	}
	
	public static boolean isMobile(String mdn) {
		if (mdn == null) return false;
		Pattern pattern = Pattern.compile("\\d{3}-\\d{3,4}-\\d{4}");	//11 digit with dash
		Matcher  matcher = pattern.matcher(mdn);
		if(matcher.matches())
			return true;

		pattern = Pattern.compile("^01\\d{1}\\d{3,4}\\d{4}$");//"^01(?:0|1|6-9|)");
		matcher = pattern.matcher(mdn);
		if(matcher.matches() ==true && (mdn.length()==10 || mdn.length()==11))
			return true;
		return false;
	}
	
	public static boolean isHangul(String keyword)
	{
		if (keyword == null) return false;
		Pattern pattern = Pattern.compile("^[ㄱ-ㅎ가-힣]*$");
		Matcher  matcher = pattern.matcher(keyword);
		if(matcher.matches())
			return true;
		return false;
	}
}
