package com.ccz.appinall.library.type;

import com.ccz.appinall.library.util.QueuedBuffer;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

public class WebsocketPacketData  extends QueuedBuffer {

	@Getter @Setter private boolean filemode = false;	//좀더 gogerous한 방법은 없나?
	
	public WebsocketPacketData(ChannelHandlerContext ctx) {
		super(ctx);
	}

}