package com.ccz.appinall.library.type.inf;



import java.util.Map;

import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public interface ICommandProcess {
	Map<EAllCmd, ICommandFunction> getCommandFunctions();
	void send(Channel ch, String data) ;
}
