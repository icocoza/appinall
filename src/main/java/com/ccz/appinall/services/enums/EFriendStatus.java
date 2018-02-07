package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EFriendStatus {
	none("none"), friend("friend"), block("block"), black("black"), all("all");
	
	public final String value;
	
	private EFriendStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static final Map<String, EFriendStatus> StringToCmdMap;
	
	static {
		StringToCmdMap = new ConcurrentHashMap<>();
		for(EFriendStatus cmd : EFriendStatus.values())
			StringToCmdMap.put(cmd.getValue(), cmd);
	}

	static public EFriendStatus getType(String cmd) {
		try{
			EFriendStatus ecmd = StringToCmdMap.get(cmd);
			if(ecmd != null)
				return ecmd;
			return none;
		}catch(Exception e) {
			return none;
		}
	}
}
