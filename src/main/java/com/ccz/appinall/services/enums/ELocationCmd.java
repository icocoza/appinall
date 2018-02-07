package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ELocationCmd {

	none("none"), setlocation("setlocation"), getlocation("getlocation"), onlocation("onlocation");
	
	public final String value;
	
	private ELocationCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, ELocationCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(ELocationCmd cmd : ELocationCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public ELocationCmd getType(String cmd) {
		ELocationCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
