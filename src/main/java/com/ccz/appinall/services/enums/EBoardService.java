package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EBoardService {
	none("none"), no01("no01"), no02("no02"), no03("no03"), no04("no04"),normal("normal");
	
	
	public final String value;
	
	private EBoardService(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EBoardService> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EBoardService cmd : EBoardService.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EBoardService getType(String cmd) {
		EBoardService ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
