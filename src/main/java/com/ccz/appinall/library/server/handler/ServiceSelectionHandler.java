package com.ccz.appinall.library.server.handler;

import java.util.List;

import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public abstract class ServiceSelectionHandler<T> extends SimpleChannelInboundHandler<IDataAccess> {

	protected final AttributeKey<IServiceHandler> propertyServiceAction = AttributeKey.valueOf(IServiceHandler.class.getSimpleName());
	
	protected List<IServiceHandler> serviceActionList;
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	    	super.channelInactive(ctx);
	    	IServiceHandler action = ctx.channel().attr(propertyServiceAction).get();
	    	if(action != null) 	action.onClose(ctx.channel());
	    	ctx.channel().attr(propertyServiceAction).set(null);
	    	ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
	    	IServiceHandler action = ctx.channel().attr(propertyServiceAction).get();
	    	if(action != null) action.onClose(ctx.channel());
	    	ctx.channel().attr(propertyServiceAction).set(null);
	    	ctx.close();
    }
    
    protected IServiceHandler findAction(String action) {
		if(this.serviceActionList==null)
			return null;
		for(IServiceHandler act : this.serviceActionList)
			if(act.isService(action))
				return act;
		return null;
	}

}
