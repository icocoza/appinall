package com.ccz.appinall.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import lombok.Getter;

@Getter
@Configuration
public class ServicesConfig {
	
	@Value("classpath:static/addrkormapping.cfg")
    private Resource korea_addr_mapping;
	
	@Value("classpath:static/addrsetting.cfg")
    private Resource address_setting;

	@Value("${elastic.cluster-name}")
    private String elasticClusterName;
	
	@Value("${elastic.cluster-node}")
    private String elasticClusterNode;
	
	@Value("${elastic.url}")
	private String elasticUrl;
	
	@Value("${elastic.port}")
    private int elasticPort;

	@Value("${elastic.index_db}")
    private String elasticIndex;
	
	@Value("${elastic.type_table}")
    private String elasticType;
	
}
