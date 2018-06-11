package com.ccz.appinall.application.ws;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ccz.appinall.library.server.handler.ServiceSelectionWebsocketDataHandler;
import com.ccz.appinall.library.server.handler.WebsocketPacketDataHandler;
import com.ccz.appinall.library.type.inf.IServiceHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
public class AppInAllWebsocketInitializer extends ChannelInitializer<SocketChannel> {
	private List<IServiceHandler> serviceActionList = new LinkedList<IServiceHandler>();
	
	@Autowired
	@Qualifier("webSocketPath")
	private String websocketPath;
	
	@Autowired ServiceSelectionWebsocketDataHandler serviceSelectionWebsocketDataHandler;
	
	public AppInAllWebsocketInitializer() {
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.config().setKeepAlive(true);
		channel.config().setOption(ChannelOption.SO_REUSEADDR, true);
		channel.config().setOption(ChannelOption.SO_LINGER, 0);
		ChannelPipeline pipeline = channel.pipeline();
		serviceSelectionWebsocketDataHandler.setActionList(serviceActionList);
		pipeline.addLast(new HttpServerCodec(), new HttpObjectAggregator(65536), new WebSocketServerProtocolHandler(websocketPath));
		pipeline.addLast(new WebsocketPacketDataHandler());
		pipeline.addLast(serviceSelectionWebsocketDataHandler);
	}

	public AppInAllWebsocketInitializer AddAction(IServiceHandler serviceAction) {
		this.serviceActionList.add(serviceAction);
		return this;
	}
}
