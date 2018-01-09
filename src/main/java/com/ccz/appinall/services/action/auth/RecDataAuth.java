package com.ccz.appinall.services.action.auth;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.services.action.RecDataCommon;
import com.fasterxml.jackson.databind.JsonNode;

public class RecDataAuth {
	public class DataRegister extends RecDataCommon {
		public String apptoken, uuid, username, usertype, ostype, osversion, appversion, email="", inappcode="";
		
		public String tokenAppId, tokenScode;
		
		public DataRegister(String data) {
			String[] sarray = data.split(ASS.UNIT, -1); 
			this.apptoken = sarray[0];
			this.uuid = sarray[1];
			this.username = sarray[2];
			this.usertype = sarray[3];
			this.ostype = sarray[4];
			this.osversion = sarray[5];
			this.appversion = sarray[6];
			if(sarray.length>7)
				this.email = sarray[7];
			if(sarray.length>8)
				this.inappcode = sarray[8];
			decodeToken();
		}
		
		public DataRegister(JsonNode jObj) {
			this.apptoken = jObj.get("apptoken").asText();
			this.uuid = jObj.get("uuid").asText();
			this.username = jObj.get("username").asText();
			this.usertype = jObj.get("usertype").asText();
			this.ostype = jObj.get("ostype").asText();
			this.osversion = jObj.get("osversion").asText();
			this.appversion = jObj.get("appversion").asText();
			this.email = jObj.get("email").asText();
			this.inappcode = jObj.get("inappcode").asText();
			decodeToken();
		}
		
		private void decodeToken() {
			String dec = Crypto.AES256Cipher.getInst().dec(apptoken);
			String[] chunk = dec.split(ASS.CHUNK);
			tokenAppId = chunk[0];
			tokenScode = chunk[1];
		}
	}
	
	public class DataLogin extends RecDataCommon {
		public String appToken, regToken, inappcode;
		
		public String tokenUserid, tokenUuid, tokenJoinTime;		
		public String tokenAppId, tokenScode;
		public DataLogin(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			this.appToken = sarray[0];
			this.regToken = sarray[1];
			this.inappcode = sarray[2];
			decodeAppToken();
			decodeRegToken();
		}
		public DataLogin(JsonNode jObj) {
			this.appToken = jObj.get("appToken").asText();
			this.regToken = jObj.get("regToken").asText();
			this.inappcode = jObj.get("inappcode").asText();
			decodeAppToken();
			decodeRegToken();
		}
		
		private void decodeAppToken() {
			String dec = Crypto.AES256Cipher.getInst().dec(appToken);
			String[] chunk = dec.split(ASS.CHUNK);
			tokenAppId = chunk[0];
			tokenScode = chunk[1];
		}
		
		private void decodeRegToken() {
			String dec = Crypto.AES256Cipher.getInst().dec(regToken);
			String[] chunk = dec.split(ASS.UNIT);
			tokenUserid = chunk[0];
			tokenUuid = chunk[1];
			tokenJoinTime = chunk[2];
		}

	}
}
