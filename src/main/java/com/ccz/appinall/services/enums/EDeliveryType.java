package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EDeliveryType {
	none("none"), foot("foot"), transportation("transportation"), car("car"), bike("bike"), truck("truck");
	
	public final String value;
	
	private EDeliveryType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EDeliveryType> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EDeliveryType cmd : EDeliveryType.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EDeliveryType getType(String cmd) {
		EDeliveryType ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
