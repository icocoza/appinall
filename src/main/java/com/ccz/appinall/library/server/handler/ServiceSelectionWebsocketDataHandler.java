package com.ccz.appinall.library.server.handler;

import java.util.List;

import com.ccz.appinall.config.DefaultPropertyKey;
import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;
import com.ccz.appinall.library.util.ProtocolWriter;

import io.netty.channel.ChannelHandlerContext;

public class ServiceSelectionWebsocketDataHandler extends ServiceSelectionHandler<IDataAccess> {

	public ServiceSelectionWebsocketDataHandler(List<IServiceAction> serviceActionList) {
		this.serviceActionList = serviceActionList;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IDataAccess da) throws Exception {
		if(da.dataType() == EDataStoreType.wstext)
			textData(ctx, da);
	}

	private void textData(ChannelHandlerContext ctx, IDataAccess da) {	//JSON data or Custom
		try {
			IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
			if(action == null) {
				action = findAction(da.getAction());
				ctx.channel().attr(propertyServiceAction).set(action);
				ctx.channel().attr(DefaultPropertyKey.writePropertyKey).set(new ProtocolWriter().new WriteWebsocket());
			}
			if(action!=null)
				action.process(ctx.channel(), da);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
