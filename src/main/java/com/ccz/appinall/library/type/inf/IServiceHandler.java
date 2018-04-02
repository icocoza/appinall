package com.ccz.appinall.library.type.inf;

import io.netty.channel.Channel;

public interface IServiceHandler {
	IServiceHandler init();
    void 	send(Channel ch, String data);
    boolean isService(String serviceType);
	boolean process(Channel ch, IDataAccess da);
	
    void 	onClose(Channel ch);
    
}
