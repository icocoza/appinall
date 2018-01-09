package com.ccz.appinall.library.server.handler;

import com.ccz.appinall.library.datastore.WebsocketTextData;
import com.ccz.appinall.library.type.WebsocketPacketData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;

public class WebsocketPacketDataHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	public final AttributeKey<WebsocketPacketData> property = AttributeKey.valueOf(WebsocketPacketData.class.getSimpleName());
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		WebsocketPacketData wsdata = ctx.channel().attr(property).get();
    	if(wsdata==null)
    		 ctx.channel().attr(property).set(wsdata = new WebsocketPacketData(ctx));
    	wsdata.write(msg.content());
    	
    	if(msg instanceof TextWebSocketFrame)
    		ctx.fireChannelRead(new WebsocketTextData(wsdata));
    	else
    		ctx.close();	//[TODO] add other types if you want ... binary, continous, close, ping, pong
	}

}
