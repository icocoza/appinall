package com.ccz.appinall.services.action;

import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataCommon {
	@Getter
	private String scode, rcode, cmd;
	
	private String apptoken;
	
	@Getter
	private String tokenAppId, tokenScode;//by appToken
	
	public RecDataCommon(JsonNode jnode) {
		scode = jnode.get("scode").asText();
		rcode = jnode.get("rcode").asText();
		cmd   = jnode.get("cmd").asText();
		
		if(jnode.has("apptoken")) {
			apptoken = jnode.get("apptoken").asText();
			decodeToken();
		}
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

}
