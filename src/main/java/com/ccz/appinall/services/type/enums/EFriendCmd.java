package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EFriendCmd {
	none("none"), addfriend("addfriend"), delfriend("delfriend"), 
	changefriendstatus("changefriendstatus"), friendids("friendids"), friendcnt("friendcnt"), friendinfos("friendinfos"), 
	appendme("appendme"), blockme("blockme"), appendmecnt("appendmecnt"), blockmecnt("blockmecnt");
	
	
	public final String value;
	
	private EFriendCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EFriendCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EFriendCmd cmd : EFriendCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EFriendCmd getType(String cmd) {
		EFriendCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
