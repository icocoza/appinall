package com.ccz.appinall.services.action.admin.entity;

import com.ccz.appinall.services.type.enums.EAdminCmd;
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
	public EAdminCmd getCommand() {
		return EAdminCmd.adminlogout;
	}
}
