package com.ccz.appinall.library.server.initializer;

import javax.net.ssl.SSLEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.util.SSLUtil;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

@Component
public class WebsocketSecureInitializer extends WebsocketInitializer {
	
	@Autowired ServicesConfig servicesConfig;
	
	public WebsocketSecureInitializer() {
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		SSLEngine engine = SSLUtil.getInst().makeSSL("", "").createSSLEngine();
		engine.setUseClientMode(false);
		engine.setNeedClientAuth(false);		
		pipeline.addFirst(new SslHandler(engine));
		
		super.initChannel(channel);
	}
}
