package com.ccz.appinall.library.server.initializer;

import javax.net.ssl.SSLEngine;

import com.ccz.appinall.library.util.SSLUtil;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

public class WebsocketSecureInitializer extends WebsocketInitializer {
	
	private String keystorePath, keyStorePw;
	
	public WebsocketSecureInitializer(String webSocketPath, String keystorePath, String keyStorePw) {
		super(webSocketPath);
		this.keystorePath = keystorePath;
		this.keyStorePw = keyStorePw;
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		SSLEngine engine = SSLUtil.getInst().makeSSL(keystorePath, keyStorePw).createSSLEngine();
		engine.setUseClientMode(false);
		engine.setNeedClientAuth(false);		
		pipeline.addFirst(new SslHandler(engine));
		
		super.initChannel(channel);
	}
}
