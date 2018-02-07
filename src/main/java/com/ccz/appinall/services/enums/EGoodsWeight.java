package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EGoodsWeight {
	none("none"), under1kg("under1kg"), under5kg("under5kg"), under10kg("under10kg"), under20kg("under20kg"), under50kg("under50kg"), over50kg("over50kg"), all("all");
	
	public final String value;
	
	private EGoodsWeight(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EGoodsWeight> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EGoodsWeight cmd : EGoodsWeight.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EGoodsWeight getType(String cmd) {
		EGoodsWeight ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
