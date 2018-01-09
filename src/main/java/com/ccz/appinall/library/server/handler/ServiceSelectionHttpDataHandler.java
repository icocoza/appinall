package com.ccz.appinall.library.server.handler;

import java.util.List;

import com.ccz.appinall.config.DefaultPropertyKey;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;
import com.ccz.appinall.library.util.ProtocolWriter;

import io.netty.channel.ChannelHandlerContext;

public class ServiceSelectionHttpDataHandler extends ServiceSelectionHandler<IDataAccess> {
	public ServiceSelectionHttpDataHandler(List<IServiceAction> serviceActionList) {
		this.serviceActionList = serviceActionList;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IDataAccess msg) throws Exception {
		doGet(ctx, msg);
	}
    
	private void doGet(ChannelHandlerContext ctx, IDataAccess data) {
		try {
			IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
			if(action == null) {
				action = findAction(data.getAction());
				ctx.channel().attr(propertyServiceAction).set(action);
				ctx.channel().attr(DefaultPropertyKey.writePropertyKey).set(new ProtocolWriter().new WriteHttpDefault());
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
