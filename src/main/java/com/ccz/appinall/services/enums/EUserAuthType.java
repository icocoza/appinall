package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EUserAuthType {
	none("none"), uid("uid"), email("email"), phone("phone"), uid_email("id_email"), uid_phone("id_phone"), all("all"), quit("quit");
	
	public final String value;
	
	private EUserAuthType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EUserAuthType> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EUserAuthType cmd : EUserAuthType.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EUserAuthType getType(String cmd) {
		EUserAuthType ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
