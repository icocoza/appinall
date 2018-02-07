package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EGoodsType {
	none("none"), envelop("envelop"), electronic("file"), tools("smallbox"), bottle("mediumbox"), box("largebox"), unbox("bigbox"), all("all");
	
	public final String value;
	
	private EGoodsType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EGoodsType> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EGoodsType cmd : EGoodsType.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EGoodsType getType(String cmd) {
		EGoodsType ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
