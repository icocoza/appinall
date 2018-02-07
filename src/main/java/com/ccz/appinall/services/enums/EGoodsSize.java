package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EGoodsSize {
	none("none"), envelop("envelop"), file("file"), smallbox("smallbox"), mediumbox("mediumbox"), largebox("largebox"), bigbox("bigbox"), cubic("cubic"), all("all");
	
	public final String value;
	
	private EGoodsSize(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EGoodsSize> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EGoodsSize cmd : EGoodsSize.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EGoodsSize getType(String cmd) {
		EGoodsSize ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
