package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAddrCmd {
	none("none"), addr_search("addr_search"), order_add("order_add"), order_list("order_list"), order_detail("order_detail"),
	select_deliver("select_deliver"), sender_cancel_order("sender_cancel_order"), order_search("order_search"),
	
	select_order("select_order"), checkin_order("checkin_order"), moving_order("moving_order"), before_gotcha("before_gotcha"), gotcha_order("gotcha_order"),
	delivering_order("delivering_order"), before_arrival("before_arrival"), arrival_in_order("arrival_in_order"), complete_delivery("complete_delivery"),
	confirm_complete_delivery("confirm_complete_delivery"),
	watch_order("watch_order"), deliver_cancel_order("deliver_cancel_order"), deliver_plan("deliver_plan"), order_search_byroute("order_search_byroute"),
	; 
	
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
