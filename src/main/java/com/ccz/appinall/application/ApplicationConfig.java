package com.ccz.appinall.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {
	
    @Value("${keystore.path}")
    	private String keystorePath;
    
    @Value("${keystore.password}")
    	private String keystorePassword;

}
