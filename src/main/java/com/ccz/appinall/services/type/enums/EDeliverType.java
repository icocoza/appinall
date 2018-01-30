package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EDeliverType {
	none("none"), personal("personal"), selfowner("selfowner"), business("business");
	
	public final String value;
	
	private EDeliverType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EDeliverType> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EDeliverType cmd : EDeliverType.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EDeliverType getType(String cmd) {
		EDeliverType ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
