package com.ccz.appinall.services.controller;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.library.type.inf.ICommandProcess;

import io.netty.util.AttributeKey;
import lombok.Setter;

public abstract class CommonAction implements ICommandProcess {
	
	@SuppressWarnings("rawtypes")
	protected AttributeKey<SessionItem> sessionKey;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CommonAction(Object sessionKey) {
		if(sessionKey == null)
			return;
		this.sessionKey = (AttributeKey<SessionItem>) sessionKey;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setSessionKey(Object sessionKey) {
		this.sessionKey = (AttributeKey<SessionItem>) sessionKey;
	}
}