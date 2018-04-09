package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

public enum EFileCmd {
	none("none", false), fileinit("fileinit", true), filesstart("filestart", false), thumbnail("thumbnail", true), upload("upload", false);
	
	public final String value;
	@Getter private final boolean auth;
	
	private EFileCmd(String value, boolean auth) {
		this.value = value;
		this.auth = auth;
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
