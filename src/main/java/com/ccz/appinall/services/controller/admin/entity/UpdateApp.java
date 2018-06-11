package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class UpdateApp extends ModifyApp {
	public EAdminAppStatus status;
	
	public UpdateApp() {}
	
	public UpdateApp(JsonNode jObj) {
		super(jObj);
		status = EAdminAppStatus.none;
	}
	
	public UpdateApp(String data, EAdminAppStatus status) {
		super(data);
		this.status = status;
	}
	public UpdateApp(JsonNode jObj, EAdminAppStatus status) {
		super(jObj);
		this.status = status;
	}
	
	@Override
	public EAllCmd getCommand() {
		if(EAdminAppStatus.ready == status)
			return EAllCmd.readyapp;
		else if(EAdminAppStatus.run == status)
			return EAllCmd.runapp;
		else if(EAdminAppStatus.stop == status)
			return EAllCmd.stopapp;
		return EAllCmd.none;
	}
}