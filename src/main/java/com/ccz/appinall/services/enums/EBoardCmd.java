package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EBoardCmd {
	none("none"), addboard("addboard"), delboard("delboard"), 
	updatetitle("updatetitle"), updatecontent("updatecontent"), updatecategory("updatecategory"), boardlist("boardlist"), boardcontent("boardcontent"), 
	like("like"), dislike("dislike"),
	addreply("addreply"), delreply("delreply"), replylist("replylist"),
	vote("vote"), selvote("selvote"), voteitemlist("voteitemlist"), voteupdate("voteupdate"), voteitem("voteitem"), changeselection("changeselection"), 
	voteinfolist("voteinfolist");
	
	public final String value;
	
	private EBoardCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EBoardCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EBoardCmd cmd : EBoardCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EBoardCmd getType(String cmd) {
		EBoardCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}