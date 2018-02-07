package com.ccz.appinall.library.type.inf;


import com.ccz.appinall.common.config.DefaultPropertyKey;
import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public interface ICommandProcess {
	public boolean processPacketData(Channel ch, String[] data);
	public boolean processJsonData(Channel ch, JsonNode jdata);
	
	default public boolean processHttpMultipart(Channel ch, HttpMultipart multipart) {
		return false;
	}
	
	default public void send(Channel ch, String data) {
		IWriteProtocol wp = ch.attr(DefaultPropertyKey.writePropertyKey).get();
		wp.write(ch, data);
	}
	
}
