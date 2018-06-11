package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class AdminLogout extends AdminCommon{
	
	public AdminLogout(String data) {
		super(data);
	}
	
	public AdminLogout(JsonNode jObj) {
		super(jObj);
	}
	
	@Override
	public EAllCmd getCommand() {
		return EAllCmd.adminlogout;
	}
}
