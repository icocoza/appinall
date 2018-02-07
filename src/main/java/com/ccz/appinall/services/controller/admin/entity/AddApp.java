package com.ccz.appinall.services.controller.admin.entity;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.controller.admin.entity.AdminLogout;
import com.ccz.appinall.services.enums.EAdminAppStatus;
import com.ccz.appinall.services.enums.EAdminCmd;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class AddApp extends AdminCommon {
	public String scode, title, version;
	public String updateforce;
	public String storeurl, description;
	public EAdminAppStatus status;
	public String fcmid, fcmkey;
	
	public AddApp() {
	}
	
	public AddApp(String data) {
		super(data);
		parse(data, 0);
	}
	public AddApp(String data, int index) {
		super(data);
		parse(data, index);
	}
	
	public AddApp(JsonNode jObj) {
		super(jObj);
		scode = jObj.get("scode").asText();
		title = jObj.get("title").asText();
		version = jObj.get("version").asText();
		updateforce = jObj.get("updateforce").asText();//.asBoolean();
		storeurl = jObj.get("storeurl").asText();
		description = jObj.get("description").asText();
		status = EAdminAppStatus.getType(jObj.get("status").asText());
		fcmid = jObj.get("fcmid").asText();
		fcmkey = jObj.get("fcmkey").asText();
	}
	
	protected void parse(String data, int index) {
		String[] sunit = data.split(ASS.UNIT, -1);
		scode = sunit[2+index];
		title = sunit[3+index];
		version = sunit[4+index];
		updateforce = sunit[5+index];//Boolean.parseBoolean(sunit[5]);
		storeurl = sunit[6+index];
		description = sunit[7+index];
		status = EAdminAppStatus.getType(sunit[8+index]);
		fcmid = sunit[9+index];
		fcmkey = sunit[10+index];
	}

	public boolean isUpdateNow() {
		return Boolean.parseBoolean(updateforce);
	}
	@Override
	public EAdminCmd getCommand() {
		return EAdminCmd.addapp;
	}
}
