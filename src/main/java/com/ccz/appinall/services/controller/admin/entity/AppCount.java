package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class AppCount extends AdminCommon {
	public EAdminAppStatus status;
	
	public AppCount() {}
	
	public AppCount(String data) {
		super(data);
		String[] sunit = data.split(ASS.UNIT, -1);
		status = EAdminAppStatus.getType(sunit[2]);	
	}
	public AppCount(JsonNode jObj) {
		super(jObj);
		status = EAdminAppStatus.getType(jObj.get("status").asText());
	}
	@Override
	public EAllCmd getCommand() {
		return EAllCmd.appcount;
	}
}
