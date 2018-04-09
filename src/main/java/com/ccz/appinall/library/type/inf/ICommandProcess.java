package com.ccz.appinall.library.type.inf;



import java.util.Map;

import com.ccz.appinall.library.datastore.HttpMultipart;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public interface ICommandProcess {
//	public boolean processPacketData(Channel ch, String[] data);
//	public boolean processJsonData(Channel ch, JsonNode jdata);
//	
//	default public boolean processHttpMultipart(Channel ch, HttpMultipart multipart) {
//		return false;
//	}
	Map<String, ICommandFunction> getCommandFunctions();
	void send(Channel ch, String data) ;
	
	public boolean processCommand(Channel ch, JsonNode jdata);
}
