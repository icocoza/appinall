package com.ccz.appinall.library.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

//ref. https://alvinalexander.com/java/jwarehouse/netty-4.1/example/src/main/java/io/netty/example/http2/tiles/
//[TODO] Not Working
@Deprecated	//Not Working 
public class Http2orHttpHandler extends ApplicationProtocolNegotiationHandler {
	
	private static final int MAX_CONTENT_LENGTH = 1024 * 1024;	//1M
	private String uploadDir;
	
	public Http2orHttpHandler(String uploadDir) {
		super(ApplicationProtocolNames.HTTP_2);
		this.uploadDir = uploadDir;
	}

	@Override
	protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
		if (ApplicationProtocolNames.HTTP_2.equals(protocol))
            configureHttp2(ctx);
		else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol))
            configureHttp1(ctx);
		else
			throw new IllegalStateException("unknown protocol: " + protocol);
	}

    private void configureHttp2(ChannelHandlerContext ctx) {
        DefaultHttp2Connection 	  connection = new DefaultHttp2Connection(true);
        InboundHttp2ToHttpAdapter listener = new InboundHttp2ToHttpAdapterBuilder(connection)
                .propagateSettings(true).validateHttpHeaders(false)
                .maxContentLength(MAX_CONTENT_LENGTH).build();

        ctx.pipeline().addLast(new HttpToHttp2ConnectionHandlerBuilder()
                .frameListener(listener)
                .connection(connection).build());

        ctx.pipeline().addLast(new HttpPacketDataHandler(uploadDir));
    }

    private void configureHttp1(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addLast(new HttpPacketDataHandler(uploadDir));
    }
}
