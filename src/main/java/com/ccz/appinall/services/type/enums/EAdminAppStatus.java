package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAdminAppStatus {
	none("none"), ready("ready"), run("run"), stop("stop"), delete("delete"), all("all");
	
	public final String value;
	
	private EAdminAppStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static final Map<String, EAdminAppStatus> StringToCmdMap;
	
	static {
		StringToCmdMap = new ConcurrentHashMap<>();
		for(EAdminAppStatus cmd : EAdminAppStatus.values())
			StringToCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAdminAppStatus getType(int cmd) {
		EAdminAppStatus ecmd = StringToCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}

	static public EAdminAppStatus getType(String cmd) {
		try{
			EAdminAppStatus ecmd = StringToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
