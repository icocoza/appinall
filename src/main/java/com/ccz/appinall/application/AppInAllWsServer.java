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
import com.ccz.appinall.services.controller.address.AddressElasticSearch;
import com.ccz.appinall.services.controller.address.AddressMongoDb;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
    
	public void start() throws InterruptedException, UnknownHostException {
		initDatabase();
		initElasticSearch();
		initMongoDb();
		
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
	
	public void initDatabase() {
        DbAppManager.getInst().createAdminDatabase(servicesConfig.getAdminMysqlUrl(), servicesConfig.getAdminMysqlDbName(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw());
        DbAppManager.getInst().initAdmin(servicesConfig.getAdminMysqlPoolname(), servicesConfig.getAdminMysqlUrl(), servicesConfig.getAdminMysqlDbName(), servicesConfig.getAdminMysqlUser(), servicesConfig.getAdminMysqlPw(), 4, 8);
        DbAppManager.getInst().initAdminApp();
	}
	
	public void initElasticSearch() throws UnknownHostException {
		AddressElasticSearch.getInst().init(servicesConfig.getElasticClusterName(), servicesConfig.getElasticClusterNode(), 
				servicesConfig.getElasticUrl(), servicesConfig.getElasticPort(), 
				servicesConfig.getElasticIndex(), servicesConfig.getElasticType(), null);
	}
	
	public void initMongoDb() {
		AddressMongoDb.getInst().init(servicesConfig.getMongoDbUrl(), servicesConfig.getMongoDbPort(), 
				servicesConfig.getAddressMongoDatabase(), servicesConfig.getAddressMongocollection());
	}
}
