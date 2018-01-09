package com.ccz.appinall.application.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.application.ApplicationConfig;
import com.ccz.appinall.application.server.websocket.AppInAllServiceAction;
import com.ccz.appinall.application.server.websocket.AppInAllWebsocketInitializer;
import com.ccz.appinall.services.action.db.DbAppManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class AppInAllWebsocketServer {

	@Autowired
	ApplicationConfig applicationConfig;
	@Autowired
	AppInAllWebsocketInitializer appInAllWebsocketInitializer;
	
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    
	public void start() throws InterruptedException {
		initDatabase();
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup();
	    workerGroup = new NioEventLoopGroup();
		
	    appInAllWebsocketInitializer.AddAction(new AppInAllServiceAction());
	    
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.localAddress(new InetSocketAddress(applicationConfig.getWebsocketPort()));
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
	    options.put(ChannelOption.SO_KEEPALIVE, applicationConfig .getKeepAlive());
	    options.put(ChannelOption.SO_BACKLOG, applicationConfig.getBacklog());
	    options.put(ChannelOption.SO_REUSEADDR, applicationConfig.getReuseAddr());
	    options.put(ChannelOption.SO_LINGER, applicationConfig.getLinger());
	    return options;
	}
	
	public void initDatabase() {
        DbAppManager.getInst().createAdminDatabase(applicationConfig.getAdminMysqlUrl(), applicationConfig.getAdminMysqlDbName(), applicationConfig.getAdminMysqlUser(), applicationConfig.getAdminMysqlPw());
        DbAppManager.getInst().initAdmin(applicationConfig.getAdminMysqlPoolname(), applicationConfig.getAdminMysqlUrl(), applicationConfig.getAdminMysqlDbName(), applicationConfig.getAdminMysqlUser(), applicationConfig.getAdminMysqlPw(), 4, 8);
        DbAppManager.getInst().initAdminApp();
	}
}
