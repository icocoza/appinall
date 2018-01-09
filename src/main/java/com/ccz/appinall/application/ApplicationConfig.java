package com.ccz.appinall.application;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {
	@Autowired
	private Environment env;
	
	@Value("${websocket.port}")
    private int websocketPort;

    @Value("${websocket.path}")
    private String websocketPath;

    @Value("${so.keepalive}")
    private boolean keepAlive;
    public boolean getKeepAlive() {	return keepAlive;	}
    
    @Value("${so.backlog}")
    private int backlog;

    @Value("${so.reuseaddr}")
    private boolean reuseaddr;
    public boolean getReuseAddr() {	return reuseaddr;	}
    
    @Value("${so.linger}")
    private int linger;
    
    @Value("${admin.mysql.url}")
    private String adminMysqlUrl;
    
    @Value("${admin.mysql.user}")
    private String adminMysqlUser;
    
    @Value("${admin.mysql.pw}")
    private String adminMysqlPw;
    
    @Value("${admin.mysql.poolname}")
    private String adminMysqlPoolname;
    
    @Value("${admin.mysql.dbname}")
    private String adminMysqlDbName;

    @Value("${fileupload.dir}")
    private String fileuploadDir;
    
    @Value("${keystore.path}")
    	private String keystorePath;
    
    @Value("${keystore.password}")
    	private String keystorePassword;
    
    @Bean(name = "webSocketPort")
    public InetSocketAddress wsPort() {
        return new InetSocketAddress(websocketPort);
    }
    
    @Bean(name = "webSocketPath") 
    public String getWebSocketPath() {
    		return websocketPath;
    }
    
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    		return new JedisConnectionFactory();
    }
    
	@Bean(name = "dataSource")
	public DriverManagerDataSource getDataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    driverManagerDataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
	    driverManagerDataSource.setUrl(env.getProperty("spring.datasource.url"));
	    driverManagerDataSource.setUsername(env.getProperty("spring.datasource.username"));
	    driverManagerDataSource.setPassword(env.getProperty("spring.datasource.password"));
	    return driverManagerDataSource;
	}
}
