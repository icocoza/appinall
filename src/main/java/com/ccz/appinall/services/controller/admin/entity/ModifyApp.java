package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class ModifyApp extends AddApp {

	public String appid;
	
	public ModifyApp() {}	
	
	public ModifyApp(String data) {
		super(data, 1);
		String[] sunit = data.split(ASS.UNIT, -1);
		appid = sunit[2];
	}
	public ModifyApp(JsonNode jObj) {
		super(jObj);
		appid = jObj.get("appid").asText();
	}
	
	@Override
	public EAllCmd getCommand() {
		return EAllCmd.modifyapp;
	}

}
