package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EChannelCmd {
	none("none"), chcreate("chcreate"), chexit("chexit"), chenter("chenter"), chinvite("chinvite"), 
	chmime("chmime"), chcount("chcount"), chlastmsg("chlastmsg"), chinfo("chinfo");
	
	
	public final String value;
	
	private EChannelCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EChannelCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EChannelCmd cmd : EChannelCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EChannelCmd getType(String cmd) {
		EChannelCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
