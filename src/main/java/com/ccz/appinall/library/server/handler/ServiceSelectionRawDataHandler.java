package com.ccz.appinall.library.server.handler;

import java.util.List;

import com.ccz.appinall.common.config.DefaultPropertyKey;
import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;
import com.ccz.appinall.library.util.ProtocolWriter;

import io.netty.channel.ChannelHandlerContext;

public class ServiceSelectionRawDataHandler extends ServiceSelectionHandler<IDataAccess> {
	
	public ServiceSelectionRawDataHandler(List<IServiceAction> serviceActionList) {
		this.serviceActionList = serviceActionList;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IDataAccess da) throws Exception {
		if(da.dataType() == EDataStoreType.string)
			stringData(ctx, da);
		else if(da.dataType() == EDataStoreType.file)
			fileData(ctx, da);
	}

	private void stringData(ChannelHandlerContext ctx, IDataAccess da) {	//JSON data or Custom
		try {
			IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
			if(action == null) {
				action = findAction(da.getAction());
				ctx.channel().attr(propertyServiceAction).set(action);
				ctx.channel().attr(DefaultPropertyKey.writePropertyKey).set(new ProtocolWriter().new WritePlainText());
			}
			if(action!=null)
				action.process(ctx.channel(), da);
		} catch (Exception e) {
		}
	}
	
	private void fileData(ChannelHandlerContext ctx,  IDataAccess da) {
		try {
			IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
			if(action == null) {
				action = findAction(da.getAction());
				//process file
			}
			if(action!=null)
				action.process(ctx.channel(), da);
		} catch (Exception e) {
		}
	}
	
}
