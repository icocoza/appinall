package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EMessageCmd {
	none("none");//, msg("msg"), syncmsg("syncmsg"), rcvmsg("rcvmsg"), readmsg("readmsg"), delmsg("delmsg"),
//	online("online"), push("push");
	
	public final String value;
	
	private EMessageCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EMessageCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EMessageCmd cmd : EMessageCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EMessageCmd getType(String cmd) {
		EMessageCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
