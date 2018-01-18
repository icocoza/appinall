package com.ccz.appinall.services.action.address;

import com.ccz.appinall.services.action.CommonAction;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class AddressCommandAction extends CommonAction {

	public AddressCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return false;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		return false;
	}

}
