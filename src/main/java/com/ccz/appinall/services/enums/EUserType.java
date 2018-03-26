package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EUserType {
	none("none"), sender("sender"), deliver("deliver"), receiver("receiver");
	
	public final String value;
	
	private EUserType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	private static final Map<String, EUserType> StrToCmdMap;
	
	static {
		StrToCmdMap = new ConcurrentHashMap<>();
		for(EUserType cmd : EUserType.values())
			StrToCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EUserType getType(String cmd) {
		try{
			EUserType ecmd = StrToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
