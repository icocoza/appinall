package com.ccz.appinall.services.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EAllCmd {
	none("none"), addr_search("addr_search"),
	
	adminregister("adminregister"), adminlogin("adminlogin"), adminlogout("adminlogout"),
	addapp("addapp"), delapp("delapp"), applist("applist"), modifyapp("modifyapp"), appcount("appcount"), stopapp("stopapp"), runapp("runapp"), readyapp("readyapp"),
	
	reg_idpw("reg_idpw"), reg_email("reg_email"), reg_phone("reg_phone"), login("login"), signin("signin"), 
	change_pw("change_pw"), reissue_email("reissue_email"), reissue_phone("reissue_phone"), verify_email("verify_email"), verify_sms("verify_sms"),
	anony_login("anony_login"), anony_login_gps("anony_login_gps"), anony_signin("anony_signin"), find_id("find_id"),
	
	addboard("addboard"), delboard("delboard"), 
	updatetitle("updatetitle"), updatecontent("updatecontent"), updatecategory("updatecategory"), updateboard("updateboard"), boardlist("boardlist"), getcontent("getcontent"), 
	like("like"), dislike("dislike"),
	addreply("addreply"), delreply("delreply"), replylist("replylist"),
	addvote("addvote"), selvote("selvote"), voteitemlist("voteitemlist"), voteupdate("voteupdate"), changeselection("changeselection"), 
	voteinfolist("voteinfolist"),
	
	chcreate("chcreate"), chexit("chexit"), chenter("chenter"), chinvite("chinvite"), 
	chmime("chmime"), chcount("chcount"), chlastmsg("chlastmsg"), chinfo("chinfo"),
	
	order_add("order_add"), order_list("order_list"), order_detail("order_detail"),
	select_deliver("select_deliver"), sender_cancel_order("sender_cancel_order"), order_search("order_search"),
	
	select_order("select_order"), checkin_order("checkin_order"), moving_order("moving_order"), before_gotcha("before_gotcha"), gotcha_order("gotcha_order"),
	delivering_order("delivering_order"), before_arrival("before_arrival"), arrival_in_order("arrival_in_order"), complete_delivery("complete_delivery"),
	confirm_complete_delivery("confirm_complete_delivery"),
	watch_order("watch_order"), deliver_cancel_order("deliver_cancel_order"), deliver_plan("deliver_plan"), order_search_byroute("order_search_byroute"),
	
	fileinit("fileinit"), filesstart("filestart"), thumbnail("thumbnail"), upload("upload"),
	
	addfriend("addfriend"), delfriend("delfriend"), 
	changefriendstatus("changefriendstatus"), friendids("friendids"), friendcnt("friendcnt"), friendinfos("friendinfos"), 
	appendme("appendme"), blockme("blockme"), appendmecnt("appendmecnt"), blockmecnt("blockmecnt"),
	
	geoloc("geoloc"), joinchannel("joinchannel"), leavechannel("leavechannel"),
	
	msg("msg"), syncmsg("syncmsg"), rcvmsg("rcvmsg"), readmsg("readmsg"), delmsg("delmsg"), online("online"), push("push"),
	
	all("all");
	
	public final String value;
	
	private EAllCmd(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EAllCmd> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EAllCmd cmd : EAllCmd.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EAllCmd getType(String cmd) {
		EAllCmd ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return none;
	}
}
