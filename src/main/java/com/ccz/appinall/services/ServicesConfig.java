package com.ccz.appinall.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ccz.appinall.services.entity.redis.SessionInfo;

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
	
	@Value("${elastic.client.transport.sniff}")
	private boolean elasticSniff;
	
	@Value("${fcm.poolname}")
	private String fcmPoolName;
	
	@Value("${fcm.senderid}")
	private String fcmSenderId;
	
	@Value("${fcm.senderkey}")
	private String fcmSenderKey;
	
	@Value("${fcm.url}")
	private String fcmUrl;
	
	@Value("${fcm.port}")
	private int fcmPort;
	
	@Value("${fcm.initcount}")
	private int fcmInitCount;
	
	@Value("${fcm.maxcount}")
	private int fcmMaxCount;
	
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    		return new JedisConnectionFactory();
    }
    
    @Bean
    public RedisTemplate<String, SessionInfo> redisSessionIpTemplate() {
		final RedisTemplate< String, SessionInfo> template =  new RedisTemplate< String, SessionInfo>();
		template.setConnectionFactory( jedisConnectionFactory() );
		template.setKeySerializer( new StringRedisSerializer() );
		template.setHashValueSerializer( new GenericToStringSerializer< SessionInfo >( SessionInfo.class ) );
		template.setValueSerializer( new Jackson2JsonRedisSerializer< SessionInfo >( SessionInfo.class ) );
		return template;
    }
}
