package com.ccz.appinall.library.server.initializer;

import java.util.LinkedList;
import java.util.List;

import com.ccz.appinall.library.server.handler.RawPacketDataHandler;
import com.ccz.appinall.library.server.handler.ServiceSelectionRawDataHandler;
import com.ccz.appinall.library.type.inf.IServiceAction;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class RawSocketServerInitializer  extends ChannelInitializer<SocketChannel> {
	protected List<IServiceAction> mActionList = new LinkedList<IServiceAction>();
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new RawPacketDataHandler());
		pipeline.addLast(new ServiceSelectionRawDataHandler(mActionList));		
	}
	
	public RawSocketServerInitializer AddAction(IServiceAction act) {
		mActionList.add(act);
		return this;
	}
}
