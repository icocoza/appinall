package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAddrCmd {
	none("none"), search("search"), orderrequest("orderrequest"), orderlist("orderlist"), orderdetail("orderdetail"),
	deliverselect("deliverselect"), ordersearch("ordersearch"), orderselect("orderselect"),
	watchorder("watchorder"), ordercancel("ordercancel"), ordercomplete("ordercomplete"), deliverplan("deliverplan"); 
	
	public final String value;
	
	private EAddrCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EAddrCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EAddrCmd cmd : EAddrCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAddrCmd getType(String cmd) {
		EAddrCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
