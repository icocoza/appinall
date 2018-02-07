package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EFileCmd {
	none("none"), thumbnail("thumbnail"), upload("upload");
	
	public final String value;
	
	private EFileCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EFileCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EFileCmd cmd : EFileCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EFileCmd getType(String cmd) {
		EFileCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
