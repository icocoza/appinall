package com.ccz.appinall.library.server.handler;

import com.ccz.appinall.library.datastore.HttpGet;
import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.library.datastore.HttpPost;
import com.ccz.appinall.library.type.HttpPacketData;
import com.ccz.appinall.library.type.enums.EHttpStatus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class HttpPacketDataHandler extends ChannelInboundHandlerAdapter {
	public final AttributeKey<HttpPacketData> property = AttributeKey.valueOf(HttpPacketData.class.getSimpleName());
	private String uploadDir;
	
	public HttpPacketDataHandler(String uploadDir) {
		this.uploadDir = uploadDir;
	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	HttpPacketData httpdata = ctx.channel().attr(property).get();
	    	if(httpdata==null)
	    		 ctx.channel().attr(property).set(httpdata = new HttpPacketData(ctx, uploadDir));
	    	httpdata.write((ByteBuf)msg);
	    	
	    	if(httpdata.isDecodeHeader()==false && httpdata.hasHttpHeader(2048)==false) //header check size is 2K
	    		return;
	    	
	    	if(httpdata.isDecodeHeader()==false)
	    		if(httpdata.decodeHttpHeader() != EHttpStatus.eOK) 	// [TODO] 클라 연결 끊어야 함..
	    			return;
	    	
	    	if(httpdata.isPostMethod())
	    		doPostData(ctx, httpdata);
	    	else
	    		doGetData(ctx, httpdata);
    }
    
    public void doGetData(ChannelHandlerContext ctx, HttpPacketData httpdata) {
	    	ctx.fireChannelRead(new HttpGet(httpdata));
	    	httpdata.discardBuf();
    }
    
    public void doPostData(ChannelHandlerContext ctx, HttpPacketData httpdata) {
	    	if(httpdata.hasPostData() == true) {
	    		if(httpdata.isMultipart()==false)
	    			ctx.fireChannelRead(new HttpPost(httpdata));
	    		else
	    			ctx.fireChannelRead(new HttpMultipart(httpdata));
	    		httpdata.discardBuf();
	    	}
    }
}
