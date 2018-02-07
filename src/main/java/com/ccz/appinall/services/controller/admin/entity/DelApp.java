package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class DelApp extends AdminCommon {
	public String scode, appid;
	
	public DelApp() {}
	
	public DelApp(String data) {
		super(data);
		String[] sunit = data.split(ASS.UNIT, -1);
		scode = sunit[2];
		appid = sunit[3];
	}
	public DelApp(JsonNode jObj) {
		super(jObj);
		scode = jObj.get("scode").asText();
		appid = jObj.get("appid").asText();
	}
	@Override
	public EAdminCmd getCommand() {
		return EAdminCmd.delapp;
	}
}
