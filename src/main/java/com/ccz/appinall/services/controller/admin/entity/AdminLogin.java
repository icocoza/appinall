package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAllCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Setter
public class AdminLogin extends AdminCommon{
	public String email, password;
	
	@Getter
	private String remoteIp;
	
	public AdminLogin() {}
	
	public AdminLogin(String data) {
		String[] sunit = data.split(ASS.UNIT, -1);
		email = sunit[0];
		password = sunit[1];
	}
	public AdminLogin(JsonNode jObj) {
		email = jObj.get("email").asText();
		password = jObj.get("password").asText();
	}
	
	@Override
	public EAllCmd getCommand() {
		return EAllCmd.adminlogin;
	}
}
