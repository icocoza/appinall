package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAuthCmd {
	none("none"), reg_idpw("reg_idpw"), reg_email("reg_email"), reg_phone("reg_phone"), login("login"), signin("signin"), 
	change_pw("change_pw"), reissue_email("reissue_email"), reissue_phone("reissue_phone"), verify_email("verify_email"), verify_sms("verify_sms");
	
	public final String value;
	
	private EAuthCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EAuthCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EAuthCmd cmd : EAuthCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAuthCmd getType(String cmd) {
		EAuthCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
