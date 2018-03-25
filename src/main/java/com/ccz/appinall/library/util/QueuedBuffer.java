package com.ccz.appinall.library.util;

import java.nio.ByteBuffer;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class QueuedBuffer {
	protected ChannelHandlerContext ctx;
	protected CompositeByteBuf compositeBuf;
	
	public QueuedBuffer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		compositeBuf = ctx.alloc().compositeBuffer();		
	}

	public void Clear() {
		compositeBuf.clear();
	}
	
	public void write(ByteBuf buf) { 
		compositeBuf = compositeBuf.writeBytes(buf);
		//compositeBuf.writerIndex(compositeBuf.writerIndex() + buf.writerIndex());
	}
	
	public void write(byte[] data) { 
		ByteBuf buf = Unpooled.copiedBuffer(data);
		this.write(buf);
	}
	
    public byte[] getBytes(int offset, int length) {
		if(compositeBuf.writerIndex()<1)
			return null;
	    	if(length > compositeBuf.writerIndex() - offset)
	    		length = compositeBuf.writerIndex() - offset;
	    	if(length < 1)
	    		return null;
	    	byte[] data = new byte[length];   		
	    	//compositeBuf.readerIndex(offset);
	    	compositeBuf.getBytes(offset, data, 0, length);
		return data;
    }
    
    public ByteBuffer getByteBuffer(int offset, int length) {
    		byte[] buf = getBytes(offset, length);
    		return ByteBuffer.wrap(buf);
    }
    
    public ByteBuffer readByteBuffer(int length) {
		if(compositeBuf.writerIndex()<1)
			return null;
	    	if(length > compositeBuf.writerIndex())
	    		length = compositeBuf.writerIndex();
	    	byte[] buf = read(length);
	    	return ByteBuffer.wrap(buf);
    }
    
    public byte[] read(int length) {
    		if(compositeBuf.writerIndex()<1)
    			return null;
	    	if(length > compositeBuf.writerIndex())
	    		length = compositeBuf.writerIndex();
	    	byte[] data = new byte[length];   		
	    	compositeBuf.readBytes(data, 0, length);
	    	compositeBuf.discardReadBytes();
		return data;
    }
    
    public byte[] readAll() {
	    	byte[] data = read(compositeBuf.writerIndex());
	    	compositeBuf.discardReadBytes();
	    	return data;
    }
    
    public int size() {
    		return compositeBuf.writerIndex();
    }
    
    public int size(int offset) {
    		return size() - offset;
    }
	
    protected ByteBufInputStream getByteBufInputStream()  {
    		return new ByteBufInputStream(compositeBuf);
    }    
    
    public boolean discardBuf() {
    		return discardBuf(Integer.MAX_VALUE);
    }
    
    public boolean discardBuf(int offset) {
	    	if(compositeBuf.writerIndex() < offset)
	    		offset = compositeBuf.writerIndex();
	    	compositeBuf.readerIndex(offset);
	    	compositeBuf = compositeBuf.discardReadBytes();
	    	return true;
    }
    
    public int readIndex() {
    		return compositeBuf.readerIndex();
    }
    
    public int writeIndex() {
    		return compositeBuf.writerIndex();
    }
    
    public String toString() {
    		return compositeBuf.toString(Charsets.UTF_8);
    }
    
}
