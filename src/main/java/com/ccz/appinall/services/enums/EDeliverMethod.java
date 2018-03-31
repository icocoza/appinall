package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EDeliverMethod {
	none("none"), foot("foot"), bus("bus"), subway("subway"), transportation("transportation"), car("car"), 
	bike("bike"), truck("truck"), taxi("taxi");
	
	public final String value;
	
	private EDeliverMethod(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EDeliverMethod> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EDeliverMethod cmd : EDeliverMethod.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EDeliverMethod getType(String cmd) {
		EDeliverMethod ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
