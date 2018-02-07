package com.ccz.appinall.common.config;

import javax.servlet.MultipartConfigElement;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.ccz.appinall.library.module.redisqueue.RedisQueueManager;
import com.ccz.appinall.services.enums.ERedisQueueCmd;
import com.ccz.appinall.services.model.redis.SessionInfo;

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
	
	@Value("${redisqueue.maxcount}")
	private int redisQueueCount;
	
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
    
	@Bean(name="redisQueueManager") 
	public RedisQueueManager<ERedisQueueCmd> redisQueueManager() {
		return new RedisQueueManager<ERedisQueueCmd>();
	}
	
    //Tomcat large file upload connection reset
    //http://www.mkyong.com/spring/spring-file-upload-and-connection-reset-issue/
    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbedded() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();

        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });

        return tomcat;

    }

}
