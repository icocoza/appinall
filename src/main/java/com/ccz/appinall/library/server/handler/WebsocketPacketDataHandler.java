package com.ccz.appinall.library.server.handler;

import com.ccz.appinall.library.datastore.WebsocketBinaryData;
import com.ccz.appinall.library.datastore.WebsocketTextData;
import com.ccz.appinall.library.type.WebsocketPacketData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;

public class WebsocketPacketDataHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
	
	private final AttributeKey<WebsocketPacketData> attrWebsocketData = AttributeKey.valueOf(WebsocketPacketData.class.getSimpleName());
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame data) throws Exception {
		WebsocketPacketData wsdata = ctx.channel().attr(attrWebsocketData).get();
	    	if(wsdata==null)
	    		 ctx.channel().attr(attrWebsocketData).set(wsdata = new WebsocketPacketData(ctx));
	    	wsdata.write(data.content());
	    	
	    	if(wsdata.isFilemode()==false)//data instanceof TextWebSocketFrame)
	    		ctx.fireChannelRead(new WebsocketTextData(wsdata));
	    	else
	    		ctx.fireChannelRead(new WebsocketBinaryData(wsdata));
	    		//ctx.close();	//[TODO] add other types if you want ... binary, continous, close, ping, pong
	}

}
