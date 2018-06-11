package com.ccz.appinall.library.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.google.common.io.ByteStreams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SSLUtil {
	
	@Autowired
	ServicesConfig serverConfig;
	
	public SSLContext makeSSL() {		 
		try {
			InputStream in = getClass().getResourceAsStream(serverConfig.getKeystorePath());
			byte[] bytes = ByteStreams.toByteArray(in);
			final String keystoreData = Base64.getEncoder().encodeToString(bytes);

	        SSLContext serverContext = SSLContext.getInstance("TLSv1.2"); 	 
	        final KeyStore ks = KeyStore.getInstance("JKS");	 
	        ks.load(new ByteArrayInputStream(Base64.getDecoder().decode(keystoreData)), serverConfig.getKeystorePassword().toCharArray());
	 
	        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());	 
	        kmf.init(ks, serverConfig.getKeystorePassword().toCharArray());	 
	        serverContext.init(kmf.getKeyManagers(), null, null);
	        return serverContext;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}	
}
