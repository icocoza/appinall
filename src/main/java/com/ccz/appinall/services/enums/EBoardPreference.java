package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EBoardPreference {
	none("none"), like("like"), dislike("dislike"), happy("happy"), smile("smile"), sad("sad");
	
	
	public final String value;
	
	private EBoardPreference(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EBoardPreference> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EBoardPreference cmd : EBoardPreference.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EBoardPreference getType(String cmd) {
		EBoardPreference ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
