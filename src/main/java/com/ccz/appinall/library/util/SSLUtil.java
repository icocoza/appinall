package com.ccz.appinall.library.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.google.common.io.ByteStreams;

public class SSLUtil {
	
	static SSLUtil s_pThis;
	static public SSLUtil getInst() {		return s_pThis = (s_pThis == null ? new SSLUtil() : s_pThis);		}
	static public void freeInst() {	s_pThis = null;	}
	
	public SSLContext makeSSL(String keystorePath, String keystorePw) {		 
		try {
			InputStream in = getClass().getResourceAsStream(keystorePath);
			byte[] bytes = ByteStreams.toByteArray(in);
		
			final String keystoreData = Base64.getEncoder().encodeToString(bytes);
			String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
			 
	        SSLContext sc = SSLContext.getInstance("TLSv1.2"); 	 
	        final KeyStore ks = KeyStore.getInstance("JKS");	 
	        ks.load(new ByteArrayInputStream(Base64.getDecoder().decode(keystoreData)), keystorePw.toCharArray());
	 
	        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);	 
	        kmf.init(ks, keystorePw.toCharArray()); 
	        sc.init(kmf.getKeyManagers(), null, null);
	        return sc;
		} catch (Exception e) {
			return null;
		} 
	}	
}
