package com.ccz.appinall.services.action.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.type.enums.EAdminCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

public abstract class AdminCommon{
	@Setter @Getter
	protected String email;
	@Setter @Getter
	private String token;
	
	public AdminCommon() {}
	
	public AdminCommon(String data) {
		String[] sunit = data.split(ASS.UNIT, -1); 
		email = sunit[0];
		token = sunit[1];
	}
	
	public AdminCommon(JsonNode jObj) {
		email = jObj.get("email").asText();
		token = jObj.get("token").asText();
	}
	
	public abstract EAdminCmd getCommand();
}
