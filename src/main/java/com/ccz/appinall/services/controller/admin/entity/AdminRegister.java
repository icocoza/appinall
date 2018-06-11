package com.ccz.appinall.services.controller.admin.entity;

import java.util.Date;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EAdminStatus;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EUserRole;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Setter;

@Setter
public class AdminRegister extends AdminCommon {
	public String passwd;
	public EAdminStatus adminstatus = EAdminStatus.normal;
	public EUserRole userrole = EUserRole.adminuser;
	public String username;
	public String nationality;
	public Date birthday;
	public int sex;	
	public AdminRegister() {}
	public AdminRegister(String data) {
		String[] sunit = data.split(ASS.UNIT, -1);
		email = sunit[0];
		passwd = sunit[1];
		username = sunit[3];
		nationality = sunit[4];
		//birthday = sunit[5];
		sex = Integer.parseInt(sunit[6]);
	}
	public AdminRegister(JsonNode jObj) {
		email = jObj.get("email").asText();
		passwd = jObj.get("password").asText();
		username = jObj.get("username").asText();			
		nationality = jObj.get("nationality").asText();
		//birthday = jObj.get("birthday");
		sex = jObj.get("sex").asInt();
	}
	@Override
	public EAllCmd getCommand() {
		return EAllCmd.adminregister;
	}
}
