package com.ccz.appinall.library.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ProtocolWriter {
	public interface IWriteProtocol {
		public void write(Channel ch, String data);
	}
	
	IWriteProtocol writeProtocol;
	
	public class WritePlainText implements IWriteProtocol {
		@Override
		public void write(Channel ch, String data) {
			try {			
				byte[] bytes = data.getBytes("UTF-8");
				ByteBuf byBuf = ch.alloc().buffer(bytes.length);
				ch.writeAndFlush(byBuf.writeBytes(bytes));
			} catch (UnsupportedEncodingException e) {
			}
		}		
	}
	
	public class WriteHttpDefault implements IWriteProtocol {

		@Override
		public void write(Channel ch, String data) {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION, Unpooled.wrappedBuffer(data.getBytes()) );
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
	        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,data.length());
	        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
	        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
	        ch.writeAndFlush(response);
		}		
	}
	
	public class WriteWebsocket implements IWriteProtocol {

		@Override
		public void write(Channel ch, String data) {
			try {			
				byte[] bytes = data.getBytes("UTF-8");
				ByteBuf byBuf = ch.alloc().buffer(bytes.length);
				ch.writeAndFlush(new TextWebSocketFrame(byBuf.writeBytes(bytes)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class WriteBinary implements IWriteProtocol {

		@Override
		public void write(Channel ch, String data) {
			try {			
				byte[] bytes = data.getBytes("UTF-8");
				byte[] byLen = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(bytes.length).array();
				ByteBuf byBuf = ch.alloc().buffer(bytes.length+8).writeBytes(byLen).writeBytes(bytes);
				ch.writeAndFlush(byBuf);
			} catch (UnsupportedEncodingException e) {
			}
		}		
	}
}
