package com.ccz.appinall.library.server.handler;

import com.ccz.appinall.library.type.RawPacketData;
import com.ccz.appinall.library.type.inf.IDataStore;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class RawPacketDataHandler extends ChannelInboundHandlerAdapter {
	
	public final AttributeKey<RawPacketData> property = AttributeKey.valueOf(RawPacketData.class.getSimpleName());
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	RawPacketData rawdata = ctx.channel().attr(property).get();
	    	if(rawdata==null)
	    		 ctx.channel().attr(property).set(rawdata = new RawPacketData(ctx));
	    	rawdata.write((ByteBuf)msg);
	    	IDataStore dataStore = null;
	    	while( (dataStore = rawdata.getDonePacket()) != null)
	   			ctx.fireChannelRead(dataStore);
    }   
}
