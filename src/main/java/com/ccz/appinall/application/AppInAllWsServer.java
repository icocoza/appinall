package com.ccz.appinall.application;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.application.ws.AppInAllFileHandler;
import com.ccz.appinall.application.ws.AppInAllServiceHandler;
import com.ccz.appinall.application.ws.AppInAllWebsocketInitializer;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.common.rdb.DbCommonManager;
import com.ccz.appinall.library.module.elasticsearch.ElasticSearchManager;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.repository.elasticsearch.AddressElasticSearch;
import com.ccz.appinall.services.repository.elasticsearch.BoardElasticSearch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInAllWsServer {

	@Autowired ServicesConfig servicesConfig;
	@Autowired AppInAllWebsocketInitializer appInAllWebsocketInitializer;
	
	@Autowired AppInAllServiceHandler appInAllServiceAction;
	@Autowired AppInAllFileHandler appInAllFileAction;
	@Autowired ElasticSearchManager elasticSearchManager;
	@Autowired AddressElasticSearch addressElasticSearch;
	@Autowired BoardElasticSearch boardElasticSearch;
	
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    
	public boolean start() throws InterruptedException, UnknownHostException {
		if(initDatabase() == false)
			return false;
		if(initElasticSearch() == false)
			return false;
		//initMongoDb();
		initGlobalFolder();
		
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup();
		
	    appInAllWebsocketInitializer.AddAction(appInAllServiceAction.init());
	    appInAllWebsocketInitializer.AddAction(appInAllFileAction.init());
	    
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.localAddress(new InetSocketAddress(servicesConfig.getWebsocketPort()));
		bootstrap.childHandler(appInAllWebsocketInitializer);
		
		channelFuture = bootstrap.bind().sync();
		return true;
	}
	
	public void closeSync() throws InterruptedException {
		channelFuture.channel().closeFuture().sync();
	}
	
	public void stop() {
		channelFuture.channel().close();
	}
	
	private Map<ChannelOption<?>, Object> channelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
	    options.put(ChannelOption.SO_KEEPALIVE, servicesConfig.isKeepAlive());
	    options.put(ChannelOption.SO_BACKLOG, servicesConfig.getBacklog());
	    options.put(ChannelOption.SO_REUSEADDR, servicesConfig.isReuseAddr());
	    options.put(ChannelOption.SO_LINGER, servicesConfig.getLinger());
	    return options;
	}
	
	public boolean initDatabase() {
        if(DbAppManager.getInst().createAdminDatabase(servicesConfig.getAdminMysqlUrl(), 
        		servicesConfig.getAdminMysqlDbName(), servicesConfig.getAdminMysqlOption(),
        		servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw()) == false) {
        		log.error("Fail to Create the Database for Admin");
        		return false;
        }
        if(DbAppManager.getInst().initAdmin(servicesConfig.getAdminMysqlPoolname(), 
        		servicesConfig.getAdminMysqlUrl(), servicesConfig.getAdminMysqlDbName(), 
        		servicesConfig.getAdminMysqlOption(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw(), 4, 4) == false) {
        		log.error("Fail to Init Admin DB Table");
        		return false;
        }
        if(DbAppManager.getInst().initAdminApp() == false) {
        		log.error("Fail to Init App DB Table");
        		return false;
        }
        if(DbCommonManager.getInst().createCommonDatabase(servicesConfig.getAdminMysqlUrl(), 
        		servicesConfig.getAdminMysqlOption(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw()) == false) {
        	log.error("Fail to Create the Database for Common");
        	return false;
        }
        if(DbCommonManager.getInst().initApp(4,  4) == false) {
    		log.error("Fail to Init Common DB Table");
    		return false;
        }
        return true;
	}
	
	public boolean initElasticSearch() throws UnknownHostException {
		if( elasticSearchManager.init(servicesConfig.getElasticCluster(), servicesConfig.getElasticNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort()) == false)
			return false;
		if(addressElasticSearch.init() == false)
			return false;
		return boardElasticSearch.init();
	}
	
	//public void initMongoDb() {
	//	AddressMongoDb.getInst().init(servicesConfig.getMongoDbUrl(), servicesConfig.getMongoDbPort(), 
	//			servicesConfig.getAddressMongoDatabase(), servicesConfig.getAddressMongocollection());
	//}
	
	private final String SCRAP = "/scrap/";
	private final String SCRAPCROP = "/scrapcrop/";
	private void initGlobalFolder() {
		String scrap = servicesConfig.getFileUploadDir() + SCRAP;
		File fscrap = new File(scrap);
		if(fscrap.exists()==false)
			fscrap.mkdirs();
		String scrapcrop = servicesConfig.getFileUploadDir() + SCRAPCROP;
		File fscrapcrop = new File(scrapcrop);
		if(fscrapcrop.exists()==false)
			fscrapcrop.mkdirs();
	}
}
