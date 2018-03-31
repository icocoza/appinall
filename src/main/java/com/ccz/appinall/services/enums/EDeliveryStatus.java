package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EDeliveryStatus {
	none("none"), apply("apply"), ready("ready"), 
	assign("assign"), start("start"), gotcha5min("gotcha5min"), 
	gotcha("gotcha"), delivering("delivering"), 
	before_delivered("before_delivered"), deliver_arrived("deliver_arrived"), delivered("delivered"), 
	confirm("confirm"), finish("finish"), 
	cancel_bysender("cancel_bysender"), cancel_bydeliver("cancel_bydeliver"),sendback("sendback");
	
	public final String value;
	
	private EDeliveryStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EDeliveryStatus> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EDeliveryStatus cmd : EDeliveryStatus.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EDeliveryStatus getType(String cmd) {
		EDeliveryStatus ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
