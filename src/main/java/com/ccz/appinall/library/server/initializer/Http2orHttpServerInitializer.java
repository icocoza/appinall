package com.ccz.appinall.library.server.initializer;

import java.security.cert.CertificateException;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLException;

import com.ccz.appinall.library.server.handler.HttpPacketDataHandler;
import com.ccz.appinall.library.server.handler.ServiceSelectionHttpDataHandler;
import com.ccz.appinall.library.type.inf.IServiceAction;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import static io.netty.handler.codec.http2.Http2SecurityUtil.CIPHERS;

public class Http2orHttpServerInitializer extends ChannelInitializer<SocketChannel> {

	protected List<IServiceAction> mActionList = new LinkedList<IServiceAction>();
	private boolean ssl;
	private String uploadDir;
	
	public Http2orHttpServerInitializer(boolean ssl, String uploadDir) {
		this.ssl = ssl;
		this.uploadDir = uploadDir;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if(ssl) {
			final SslContext sslCtx = configureTLS();
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		//pipeline.addLast(new Http2orHttpHandler());
		pipeline.addLast(new HttpPacketDataHandler(uploadDir));
		pipeline.addLast(new ServiceSelectionHttpDataHandler(mActionList));		
	}
	
	public Http2orHttpServerInitializer AddAction(IServiceAction act) {
		mActionList.add(act);
		return this;
	}
	
	private static SslContext configureTLS() throws CertificateException, SSLException {
		SelfSignedCertificate ssc = new SelfSignedCertificate();
		ApplicationProtocolConfig apn = new ApplicationProtocolConfig(Protocol.ALPN,
				// NO_ADVERTISE is currently the only mode supported by both
				// OpenSsl and JDK providers.
				SelectorFailureBehavior.NO_ADVERTISE,
				// ACCEPT is currently the only mode supported by both OpenSsl
				// and JDK providers.
				SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1);

		return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey(), null)
				.ciphers(CIPHERS, SupportedCipherSuiteFilter.INSTANCE).applicationProtocolConfig(apn).build();
	}
	
	
}
