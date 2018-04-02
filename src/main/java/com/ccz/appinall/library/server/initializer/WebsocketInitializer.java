package com.ccz.appinall.library.server.initializer;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
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
public class WebsocketInitializer  extends ChannelInitializer<SocketChannel>{

	private List<IServiceHandler> serviceActionList = new LinkedList<IServiceHandler>();

	@Autowired ServicesConfig servicesConfig;
	@Autowired ServiceSelectionWebsocketDataHandler serviceSelectionWebsocketDataHandler;
	
	public WebsocketInitializer() {
	}
	
	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		channel.config().setKeepAlive(true);
		channel.config().setOption(ChannelOption.SO_REUSEADDR, true);
		channel.config().setOption(ChannelOption.SO_LINGER, 0);
		ChannelPipeline pipeline = channel.pipeline();
		serviceSelectionWebsocketDataHandler.setActionList(serviceActionList);
		
		pipeline.addLast(new HttpServerCodec(), new HttpObjectAggregator(65536), new WebSocketServerProtocolHandler(servicesConfig.getWebSocketPath()));
		pipeline.addLast(new WebsocketPacketDataHandler());
		pipeline.addLast(serviceSelectionWebsocketDataHandler);
	}

	public WebsocketInitializer AddAction(IServiceHandler serviceAction) {
		this.serviceActionList.add(serviceAction);
		return this;
	}

}
