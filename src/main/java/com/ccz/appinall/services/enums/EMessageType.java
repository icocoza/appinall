package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EMessageType {
	none("none"), chat("chat"), online("online"), push("push");
	
	public final String value;
	
	private EMessageType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static final Map<String, EMessageType> StrToCmdMap;
	
	static {
		StrToCmdMap = new ConcurrentHashMap<>();
		for(EMessageType cmd : EMessageType.values())
			StrToCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EMessageType getType(String cmd) {
		try{
			EMessageType ecmd = StrToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
