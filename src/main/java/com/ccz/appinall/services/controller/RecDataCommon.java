package com.ccz.appinall.services.controller;

import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataCommon {
	
	@Getter private String scode, rcode, cmd;
	
	private String apptoken;
	
	@Getter private String tokenAppId;
	private String tokenScode;//by appToken
	
	//@Getter private String userid;
	
	public RecDataCommon(JsonNode jnode) {
		if(jnode==null)
			return;
		scode = jnode.get("scode").asText();
		rcode = jnode.get("rcode").asText();
		cmd   = jnode.get("cmd").asText();
		
		if(jnode.has("apptoken")) {
			apptoken = jnode.get("apptoken").asText();
			decodeToken();
		}
		//if(jnode.has("userid"))
		//	this.userid = jnode.get("userid").asText();
	}
	
	public RecDataCommon(RecDataCommon clone) {
		this.scode = clone.scode;
		this.rcode = clone.rcode;
		this.cmd = clone.cmd;
	}
	
	private void decodeToken() {
		String dec = Crypto.AES256Cipher.getInst().dec(apptoken);
		String[] chunk = dec.split(ASS.CHUNK);
		tokenAppId = chunk[0];
		tokenScode = chunk[1];
	}
	
	public boolean isValidAppToken() {
		if(tokenAppId==null || tokenAppId.length()<1 || tokenScode == null || tokenScode.length()<1)
			return false;
		return true;
	}
	
	public void copy(RecDataCommon data) {
		this.scode = data.scode;
		this.rcode = data.rcode;
		this.cmd = data.cmd;
		this.apptoken = data.apptoken;
		if(this.apptoken != null)
			decodeToken();
	}

}
