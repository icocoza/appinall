package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EUserRole {
	none("none"), user("user"), adminuser("adminuser"), adminmaster("adminmaster"), anonymous("anonymous");
	
	public final String value;
	
	private EUserRole(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	private static final Map<String, EUserRole> StrToCmdMap;
	
	static {
		StrToCmdMap = new ConcurrentHashMap<>();
		for(EUserRole cmd : EUserRole.values())
			StrToCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EUserRole getType(String cmd) {
		try{
			EUserRole ecmd = StrToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
