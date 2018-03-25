package com.ccz.appinall.services.controller.auth;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.services.controller.RecDataCommon;
import com.ccz.appinall.services.enums.EUserAuthType;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataAuth {
	
	@Getter
	public class DataRegUser extends RecDataCommon {
		private String uuid, username, usertype, ostype, osversion, appversion, inappcode;
		protected EUserAuthType authtype = EUserAuthType.none;
		
		public DataRegUser(JsonNode jnode) {
			super(jnode);
			this.uuid = jnode.get("uuid").asText();
			this.username = jnode.get("username").asText();
			this.usertype = jnode.get("usertype").asText();
			this.ostype = jnode.get("ostype").asText();
			this.osversion = jnode.get("osversion").asText();
			this.appversion = jnode.get("appversion").asText();
			this.inappcode = jnode.get("inappcode").asText();
		}
	}
	
	@Getter
	public class DataRegIdPw extends DataRegUser {
		public String uid, pw;	//for user authentication
		
		public DataRegIdPw(JsonNode jnode) {
			super(jnode);
			this.authtype = EUserAuthType.uid;
			this.uid = jnode.get("uid").asText();
			this.pw = jnode.get("pw").asText();
		}
	}

	@Getter
	public class DataRegEmail extends DataRegUser {
		public String email;	//for user authentication
		
		public DataRegEmail(JsonNode jnode) {
			super(jnode);
			this.authtype = EUserAuthType.email;
			this.email = jnode.get("email").asText();
		}
	}

	@Getter
	public class DataRegPhone extends DataRegUser {
		public String phoneno;	//for user authentication
		
		public DataRegPhone(JsonNode jnode) {
			super(jnode);
			this.authtype = EUserAuthType.phone;
			this.phoneno = jnode.get("phoneno").asText();
		}
	}
	
	public class DataSignIn extends RecDataCommon {
		
		private String regToken;
		
		@Getter
		private String tokenid, uuid;
		@Getter
		private String tokenUserid, tokenUuid;//by regToken
		@Getter
		private  EUserAuthType tokenAuthType = EUserAuthType.none;//by regToken
		
		public DataSignIn(JsonNode jnode) {
			super(jnode);
			if(jnode.has("regToken"))
				this.regToken = jnode.get("regToken").asText();
			if(jnode.has("regtoken"))
				this.regToken = jnode.get("regtoken").asText();
			if(jnode.has("tokenid"))
				this.tokenid = jnode.get("tokenid").asText();
			if(jnode.has("tid"))
				this.tokenid = jnode.get("tid").asText();

			if(jnode.has("uuid"))
				this.uuid = jnode.get("uuid").asText();
			
			decodeRegToken();
		}
		
		private void decodeRegToken() {
			if(this.regToken==null)
				return;
			String dec = Crypto.AES256Cipher.getInst().dec(regToken);
			String[] chunk = dec.split(ASS.UNIT);
			tokenUserid = chunk[0];
			tokenUuid = chunk[1];
			tokenAuthType = EUserAuthType.getType(chunk[2]);
		}
		
		public boolean isValidUserToken() {
			if(tokenUserid==null || tokenUserid.length()<1 || tokenUuid == null || tokenUuid.length()<1 || tokenAuthType == null)
				return false;
			return true;
		}
		
		public boolean isValidUuid() {
			return this.uuid.equals(this.tokenUuid);
		}
	}
	
	public class DataLogin extends DataSignIn {
		@Getter
		private String uid, pw;
		
		public DataLogin(JsonNode jnode) {
			super(jnode);
			this.uid = jnode.get("uid").asText();
			this.pw = jnode.get("pw").asText();
		}
		
		public boolean isValidIdPw() {
			if(uid==null || uid.length()<8 || pw==null || pw.length()<6 )
				return false;
			return true;
		}
		
	}
	
	@Getter
	private class DataUpdateUser extends RecDataCommon {
		protected String uuid, ostype, osversion, appversion, inappcode;
		protected EUserAuthType authtype = EUserAuthType.none;
		
		public DataUpdateUser(JsonNode jnode) {
			super(jnode);
			this.uuid = jnode.get("uuid").asText();
			this.ostype = jnode.get("ostype").asText();
			this.osversion = jnode.get("osversion").asText();
			this.appversion = jnode.get("appversion").asText();
			this.inappcode = jnode.get("inappcode").asText();
		}
	}
	
	public class DataUpdateIdUser extends DataRegIdPw {
		@Getter
		private String newpw;
		
		public DataUpdateIdUser(JsonNode jnode) {
			super(jnode);
			newpw = jnode.get("newpw").asText();
		}
		
	}
	
	public class DataUpdateEmailUser extends DataRegEmail {
		public DataUpdateEmailUser(JsonNode jnode) {
			super(jnode);
		}
	}
	
	public class DataUpdatePhoneUser extends DataRegPhone {
		public DataUpdatePhoneUser(JsonNode jnode) {
			super(jnode);
		}
	}

	public class DataVerifyPhoneUser extends DataRegPhone {
		@Getter
		private String smscode, tokenid;
		
		public DataVerifyPhoneUser(JsonNode jnode) {
			super(jnode);
			smscode = jnode.get("smscode").asText();
			tokenid = jnode.get("tokenid").asText();
		}
	}

}
