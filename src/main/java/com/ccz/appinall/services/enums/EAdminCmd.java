package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAdminCmd {
	none("none"), adminregister("adminregister"), adminlogin("adminlogin"), adminlogout("adminlogout"),
	addapp("addapp"), delapp("delapp"), applist("applist"), modifyapp("modifyapp"), appcount("appcount"), stopapp("stopapp"), runapp("runapp"), readyapp("readyapp");
	
	public final String value;
	
	private EAdminCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EAdminCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EAdminCmd cmd : EAdminCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAdminCmd getType(String cmd) {
		EAdminCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
