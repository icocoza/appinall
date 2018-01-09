package com.ccz.appinall.services.action.auth;

import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.action.CommonAction;
import com.ccz.appinall.services.action.auth.RecDataAuth.DataLogin;
import com.ccz.appinall.services.action.auth.RecDataAuth.DataRegister;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecAdminApp;
import com.ccz.appinall.services.entity.db.RecUser;
import com.ccz.appinall.services.type.enums.EAuthCmd;
import com.ccz.appinall.services.type.enums.EAuthError;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

//@Component
public class AuthCommandAction extends CommonAction {

	public AuthCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		ResponseData<EAuthError> res = new ResponseData<EAuthError>(data[0], data[1], data[2]);
		
		String userData = data[3];
		switch(EAuthCmd.getType(res.getCommand())) {
		case register:
			res = this.doRegister(ch, res, new RecDataAuth().new DataRegister(userData)); //O
			break;
		case login:
			res = this.doLogin(ch, res, new RecDataAuth().new DataLogin(userData)); //O
			break;
		default:
			return false;
		}
		if(res != null)
			send(ch, res.toString());
		return true;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		ResponseData<EAuthError> res = new ResponseData<EAuthError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		switch(EAuthCmd.getType(res.getCommand())) {
		case register:
			res = this.doRegister(ch, res, new RecDataAuth().new DataRegister(jdata)); //O
			break;
		case login:
			res = this.doLogin(ch, res, new RecDataAuth().new DataLogin(jdata)); //O
			break;
		default:
			return false;
		}
		if(res != null)
			send(ch, res.toString());
		return true;
	}

	/** 
	 * @param ch 
	 * @param userData, registration string data 
	 * 		  [app token][ssh1 mac or uuid][user name][user type][os type][os version][app version][email]
	 * 		  user type => 'u'
	 * 		  inAppCode => to distinguish app service types for service operator
	 * @return AES256([userid][uuid])
	 */
	private ResponseData<EAuthError> doRegister(Channel ch, ResponseData<EAuthError> res, DataRegister data) {
		RecUser user = DbAppManager.getInst().getUserByUuid(data.tokenScode, data.uuid);
		RecAdminApp app = DbAppManager.getInst().getApp(data.tokenAppId);	//if app is null
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.eWrongAppId);
		//if(data.tokenScode.equals(super.serviceCode)==false)
		//	return res.setError(EAuthError.eWrongServiceCode);
		if(user == DbRecord.Empty) {
			String userid = StrUtil.getSha1Uuid("us");
			user = DbAppManager.getInst().addUser(data.tokenScode, userid, data.uuid, data.username, data.usertype, data.ostype, 
												  data.osversion, data.appversion, data.email);
		}
		if(user==DbRecord.Empty)
			return res.setError(EAuthError.eRegisterFailed);

		String enc = Crypto.AES256Cipher.getInst().enc(user.userid+ASS.UNIT+user.devuuid+ASS.UNIT+user.jointime);
		return res.setError(EAuthError.eOK).setParam(enc);
	}

	/** 
	 * login data
	 * @param ch
	 * @param res
	 * @param userData, [token][appcode]
	 * 			token => AES256([userid][uuid])
	 * @return the lasttime which is last joined time
	 */
	private ResponseData<EAuthError> doLogin(Channel ch, ResponseData<EAuthError> res, DataLogin data) {
		RecUser user = DbAppManager.getInst().getUserByUuid(data.tokenScode, data.tokenUuid);
		if(user==DbRecord.Empty)
			return res.setError(EAuthError.eNotExistUser);
		else if(user.userid.equals(data.tokenUserid)==false)
			return res.setError(EAuthError.eMismatchToken);
		if(user.isSameApt(data.inappcode) == false)
			if(DbAppManager.getInst().updateAptCode(data.tokenScode, data.tokenUserid, data.inappcode) == false)	//update apt code
				return res.setError(EAuthError.eWrongServiceCode);
		
		user.inappcode = data.inappcode;
		AuthSession session = new AuthSession(ch, 1).putSession(user, data.tokenScode);
		SessionManager.getInst().put(session);
		ch.attr(sessionKey).set(session);
		
		return res.setError(EAuthError.eOK).setParam(""+user.lasttime);
	}


}
