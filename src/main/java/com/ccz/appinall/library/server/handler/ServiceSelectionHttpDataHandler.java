package com.ccz.appinall.library.server.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceHandler;
import com.ccz.appinall.library.util.ProtocolWriter;

import io.netty.channel.ChannelHandlerContext;

@Component
public class ServiceSelectionHttpDataHandler extends ServiceSelectionHandler<IDataAccess> {
	
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public ServiceSelectionHttpDataHandler(List<IServiceHandler> serviceActionList) {
		this.serviceActionList = serviceActionList;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IDataAccess msg) throws Exception {
		doGet(ctx, msg);
	}
    
	private void doGet(ChannelHandlerContext ctx, IDataAccess data) {
		try {
			IServiceHandler action = ctx.channel().attr(propertyServiceAction).get();
			if(action == null) {
				action = findAction(data.getAction());
				ctx.channel().attr(propertyServiceAction).set(action);
				ctx.channel().attr(chAttributeKey.getWriteKey()).set(new ProtocolWriter().new WriteHttpDefault());
			}
			action.process(ctx.channel(), data);
		} catch (Exception e) {
		}	
	}
	
	private void doPost(ChannelHandlerContext ctx, IDataAccess data) {		
		doGet(ctx, data);
	}

	private void doMultipart(ChannelHandlerContext ctx, IDataAccess data) {
		doGet(ctx, data);
	}

}
