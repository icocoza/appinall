package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EBoardItemType {
	none("none"), text("text"), image("image"), audio("audio"), video("video"), 
	link("link"), richtext("richtext");
	
	
	public final String value;
	
	private EBoardItemType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EBoardItemType> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EBoardItemType cmd : EBoardItemType.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EBoardItemType getType(String cmd) {
		EBoardItemType ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
