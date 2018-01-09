package com.ccz.appinall.library.server.handler;

import java.util.List;

import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public abstract class ServiceSelectionHandler<T> extends SimpleChannelInboundHandler<IDataAccess> {

	protected final AttributeKey<IServiceAction> propertyServiceAction = AttributeKey.valueOf(IServiceAction.class.getSimpleName());
	
	protected List<IServiceAction> serviceActionList;
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	    	super.channelInactive(ctx);
	    	IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
	    	if(action != null) 	action.onClose(ctx.channel());
	    	ctx.channel().attr(propertyServiceAction).set(null);
	    	ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
	    	IServiceAction action = ctx.channel().attr(propertyServiceAction).get();
	    	if(action != null) action.onClose(ctx.channel());
	    	ctx.channel().attr(propertyServiceAction).set(null);
	    	ctx.close();
    }
    
    protected IServiceAction findAction(String action) {
		if(this.serviceActionList==null)
			return null;
		for(IServiceAction act : this.serviceActionList)
			if(act.isService(action))
				return act;
		return null;
	}

}
