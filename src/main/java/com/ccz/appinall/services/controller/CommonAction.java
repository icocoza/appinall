package com.ccz.appinall.services.controller;

import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.ICommandProcess;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.FileSession;

import io.netty.util.AttributeKey;
import lombok.Setter;

public abstract class CommonAction implements ICommandProcess {
	
	@Setter protected AttributeKey<AuthSession> 			attrAuthSessionKey;
	@Setter protected AttributeKey<FileSession> 			attrFileSessionKey;
	
	@SuppressWarnings("unchecked")
	public CommonAction(Object attrAuthSessionKey) {
		this.attrAuthSessionKey = (AttributeKey<AuthSession>) attrAuthSessionKey;
	}
}