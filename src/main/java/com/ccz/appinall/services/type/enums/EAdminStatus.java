package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAdminStatus {
	none("none"), normal("normal"), pending("pending"), block("block"), leave("leave");
	
	public final String value;
	
	private EAdminStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	private static final Map<String, EAdminStatus> StrToCmdMap;
	
	static {
		StrToCmdMap = new ConcurrentHashMap<>();
		for(EAdminStatus cmd : EAdminStatus.values())
			StrToCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAdminStatus getType(String cmd) {
		try{
			EAdminStatus ecmd = StrToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
