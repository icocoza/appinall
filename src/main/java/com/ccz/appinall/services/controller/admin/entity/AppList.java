package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class AppList extends AdminCommon {
	public int offset, count;
	public EAdminAppStatus status;
	
	public AppList() {	}
	
	public AppList(String data) {
		super(data);
		String[] sunit = data.split(ASS.UNIT, -1);
		offset = Integer.parseInt(sunit[2]);
		count = Integer.parseInt(sunit[3]);
		status = EAdminAppStatus.getType(sunit[4]);
	}
	public AppList(JsonNode jObj) {
		super(jObj);
		offset = jObj.get("appid").asInt();
		count = jObj.get("version").asInt();
		status = EAdminAppStatus.getType(jObj.get("status").asText());
	}
	@Override
	public EAdminCmd getCommand() {
		return EAdminCmd.applist;
	}
}
