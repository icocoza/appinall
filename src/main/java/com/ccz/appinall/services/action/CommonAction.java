package com.ccz.appinall.services.action;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.library.type.inf.ICommandProcess;

import io.netty.util.AttributeKey;

public abstract class CommonAction implements ICommandProcess {
	
	@SuppressWarnings("rawtypes")
	protected AttributeKey<SessionItem> sessionKey;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CommonAction(Object sessionKey) {
		this.sessionKey = (AttributeKey<SessionItem>) sessionKey;
	}

}