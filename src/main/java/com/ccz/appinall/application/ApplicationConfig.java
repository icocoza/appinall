package com.ccz.appinall.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {
	
    @Value("${fileupload.dir}")
    private String fileuploadDir;
    
    @Value("${keystore.path}")
    	private String keystorePath;
    
    @Value("${keystore.password}")
    	private String keystorePassword;

}
