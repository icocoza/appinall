package com.ccz.appinall.library.type;

import com.ccz.appinall.library.util.QueuedBuffer;

import io.netty.channel.ChannelHandlerContext;

public class WebsocketPacketData  extends QueuedBuffer {

	public WebsocketPacketData(ChannelHandlerContext ctx) {
		super(ctx);
	}

}