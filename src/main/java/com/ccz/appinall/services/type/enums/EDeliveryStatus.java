package com.ccz.appinall.services.type.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EDeliveryStatus {
	none("none"), ready("ready"), 
	assign("assign"), start("start"), 
	gotcha("gotcha"), delivering("delivering"), 
	delivered("delivered"), 
	confirm("confirm"), finish("finish"), 
	cancel("cancel"), sendback("sendback");
	
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
