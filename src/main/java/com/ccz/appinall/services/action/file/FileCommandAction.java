package com.ccz.appinall.services.action.file;

import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.services.action.CommonAction;
import com.ccz.appinall.services.type.enums.EFileCmd;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class FileCommandAction extends CommonAction {

	public FileCommandAction(Object sessionKey) {
		super(sessionKey);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) { 	return false;	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {	return false;	}
	
	@Override
	public boolean processHttpMultipart(Channel ch, HttpMultipart multipart) {
		switch(EFileCmd.getType(multipart.getCommand())) {
			case thumbnail:
				break;
			case upload:
				break;
			default:
		}
		return false;
	}

}
