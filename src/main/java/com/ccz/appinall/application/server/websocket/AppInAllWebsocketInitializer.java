package com.ccz.appinall.application.server.websocket;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ccz.appinall.library.server.handler.ServiceSelectionWebsocketDataHandler;
import com.ccz.appinall.library.server.handler.WebsocketPacketDataHandler;
import com.ccz.appinall.library.type.inf.IServiceAction;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
public class AppInAllWebsocketInitializer extends ChannelInitializer<SocketChannel> {
	private List<IServiceAction> serviceActionList = new LinkedList<IServiceAction>();
	
	@Autowired
	@Qualifier("webSocketPath")
	private String websocketPath;
	
	public AppInAllWebsocketInitializer() {
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.config().setKeepAlive(true);
		channel.config().setOption(ChannelOption.SO_REUSEADDR, true);
		channel.config().setOption(ChannelOption.SO_LINGER, 0);
		ChannelPipeline pipeline = channel.pipeline();		
		pipeline.addLast(new HttpServerCodec(), new HttpObjectAggregator(65536), new WebSocketServerProtocolHandler(websocketPath));
		pipeline.addLast(new WebsocketPacketDataHandler());
		pipeline.addLast(new ServiceSelectionWebsocketDataHandler(serviceActionList));
	}

	public AppInAllWebsocketInitializer AddAction(IServiceAction serviceAction) {
		this.serviceActionList.add(serviceAction);
		return this;
	}
}
