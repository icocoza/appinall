package com.ccz.appinall.library.module.tcp;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.ccz.appinall.library.server.handler.RawPacketDataHandler;
import com.ccz.appinall.library.server.handler.ServiceSelectionRawDataHandler;
import com.ccz.appinall.library.type.DataType.IpPort;
import com.ccz.appinall.library.type.inf.IServiceAction;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RawConnection {
	final IpPort ipp;
	private Channel channel;
	private EventLoopGroup nioEventLoop;
	
	private List<IServiceAction> serviceActionList;
	
	public RawConnection(IpPort ipp, List<IServiceAction> actionList) {
		this.ipp = ipp;
		this.serviceActionList = actionList;
	}
	
	public void connect() throws Exception {
		nioEventLoop = new NioEventLoopGroup();
		try{
			Bootstrap bs = new Bootstrap().group(nioEventLoop).channel(NioSocketChannel.class).
							  remoteAddress(new InetSocketAddress(ipp.getIp(), ipp.getPort())).
							  handler( new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel channel) {					
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast("aggregator", new RawPacketDataHandler());
						pipeline.addLast("handler", new ServiceSelectionRawDataHandler(serviceActionList));	
					}
				    @Override
				    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				        super.channelInactive(ctx);
				    }
			});			
			ChannelFuture cf = bs.connect().sync();
			channel = cf.channel();
		}
		finally{			
		}
	}
	
	public void close() {		
		try {
			channel.close();
			nioEventLoop.shutdownGracefully().sync();
		} catch (Exception e) {
		}
	}
	
	public synchronized void write(String data) {
		try {			
			byte[] buf = data.getBytes("UTF-8");
			byte[] bytes = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(buf.length).array();
			ByteBuf byBuf = channel.alloc().buffer(buf.length+8);
			byBuf.writeBytes(bytes);
			byBuf.writeBytes(buf);
			channel.writeAndFlush(byBuf);
		} catch (Exception e) {
		} 	
	}
	
	public boolean isAvailable() {
		if(channel==null)
			return false;
		return channel.isOpen() && channel.isWritable();
	}
}
