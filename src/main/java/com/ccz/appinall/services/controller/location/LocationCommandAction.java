package com.ccz.appinall.services.controller.location;

import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.enums.EAddrCmd;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.ELocationCmd;
import com.ccz.appinall.services.enums.ELocationError;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class LocationCommandAction extends CommonAction {

	public LocationCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return false;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		ResponseData<ELocationError> res = new ResponseData<>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		
		switch(ELocationCmd.getType(res.getCommand())) {
			case setlocation:
			case getlocation:
			case onlocation:
			break;
			default:
				break;
		}
		return false;
	}

}
