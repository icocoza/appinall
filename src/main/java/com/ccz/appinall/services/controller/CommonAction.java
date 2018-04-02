package com.ccz.appinall.services.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.ICommandProcess;
import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.FileSession;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Setter;

@Component
public abstract class CommonAction implements ICommandProcess {
	
//	@Setter protected AttributeKey<AuthSession> 			attrAuthSessionKey;
//	@Setter protected AttributeKey<FileSession> 			attrFileSessionKey;
//	
//	@SuppressWarnings("unchecked")
//	public CommonAction(Object attrAuthSessionKey) {
//		this.attrAuthSessionKey = (AttributeKey<AuthSession>) attrAuthSessionKey;
//	}
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public void send(Channel ch, String data) {
		IWriteProtocol wp = ch.attr(chAttributeKey.getWriteKey()).get();
		wp.write(ch, data);
		
	}

}