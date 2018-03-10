package com.ccz.appinall.application;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.application.ws.AppInAllServiceAction;
import com.ccz.appinall.application.ws.AppInAllWebsocketInitializer;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.services.controller.address.AddressCommandAction;
import com.ccz.appinall.services.controller.address.AddressElasticSearch;

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

	@Autowired
	ApplicationConfig applicationConfig;
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	AppInAllWebsocketInitializer appInAllWebsocketInitializer;
	@Autowired
	AppInAllServiceAction appInAllServiceAction;
	
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
		
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup();
		
	    appInAllWebsocketInitializer.AddAction(appInAllServiceAction);
	    
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.localAddress(new InetSocketAddress(servicesConfig.getWebsocketPort()));
		bootstrap.childHandler(appInAllWebsocketInitializer);
		
//		Map<ChannelOption<?>, Object> tcpChannelOptions = channelOptions();
//        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
//        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
//           bootstrap.option(option, tcpChannelOptions.get(option));
//        }
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
        return true;
	}
	
	public boolean initElasticSearch() throws UnknownHostException {
		return AddressElasticSearch.getInst().init(servicesConfig.getElasticCluster(), servicesConfig.getElasticNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
	}
	
	//public void initMongoDb() {
	//	AddressMongoDb.getInst().init(servicesConfig.getMongoDbUrl(), servicesConfig.getMongoDbPort(), 
	//			servicesConfig.getAddressMongoDatabase(), servicesConfig.getAddressMongocollection());
	//}
}
