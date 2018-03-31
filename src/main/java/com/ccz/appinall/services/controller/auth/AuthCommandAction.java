package com.ccz.appinall.services.controller.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.common.rdb.DbTransaction;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.RecDataAuth.*;
import com.ccz.appinall.services.enums.EAuthCmd;
import com.ccz.appinall.services.enums.EAuthError;
import com.ccz.appinall.services.enums.EUserAuthType;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.services.service.SessionService;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
//@Component
public class AuthCommandAction extends CommonAction {

	@Autowired
	SessionService sessionService;
	
	public AuthCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@SuppressWarnings("unused")
	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return false;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		ResponseData<EAuthError> res = new ResponseData<EAuthError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		switch(EAuthCmd.getType(res.getCommand())) {
		case reg_idpw:
			res = this.doRegister(res, new RecDataAuth().new DataRegIdPw(jdata));
			break;
		case reg_email:
			res = this.doRegister(res, new RecDataAuth().new DataRegEmail(jdata));
			break;
		case reg_phone:
			res = this.doRegister(res, new RecDataAuth().new DataRegPhone(jdata));
			break;
		case login:
			res = this.doLogin(res, new RecDataAuth().new DataLogin(jdata)); //가입 후 한번은 로그인 해야 함
			break;
		case signin:
			res = this.doSignin(ch, res, new RecDataAuth().new DataSignIn(jdata)); //한번 로그인 한 이후에는 토큰으로 로그인 하면 됨 
			break;
		case change_pw:
			res = this.doUpdatePW(res, new RecDataAuth().new DataUpdateIdUser(jdata));
			break;
		case reissue_email:	//send email
			res = this.doUpdateEmail(res, new RecDataAuth().new DataUpdateEmailUser(jdata));
			break;
		case reissue_phone:	//send sms and update sms code
			res = this.doUpdatePhoneNo(res, new RecDataAuth().new DataUpdatePhoneUser(jdata));
			break;
		case verify_email: //from HTTP GET Request
			break;
		case verify_sms:
			res = this.doVerifyPhoneNo(res, new RecDataAuth().new DataVerifyPhoneUser(jdata));
			break;
		default:
			return false;
		}
		if(res != null) {
			send(ch, res.toString());
			log.info(res.toString());
		}
		return true;
	}

	/** modified...check RecUser schema 
	 * @param ch 
	 * @param userData, registration string data 
	 * 		  [app token][ssh1 mac or uuid][user name][user type][os type][os version][app version][email]
	 * 		  user type => 'u'
	 * 		  inAppCode => to distinguish app service types for service operator
	 * @return AES256([userid][uuid])
	 * 
	 * 1. check application id
	 * 2. find authtype, return error if exist
	 * 3. register userAuth by authtype
	 * 4. register user 
	 * 5. return the Base62 token which encrypted AES256
	 */
	private ResponseData<EAuthError> doRegister(ResponseData<EAuthError> res, DataRegUser data) {
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		if(data instanceof DataRegIdPw)
			return doRegIdPw(res, (DataRegIdPw) data);
		else if(data instanceof DataRegEmail)
			return doRegEmail(res, (DataRegEmail) data);
		else if(data instanceof DataRegPhone)
			return doRegPhone(res, (DataRegPhone) data);
		return res.setError(EAuthError.unknown_datatype);
	}
	
	private ResponseData<EAuthError> doRegIdPw(ResponseData<EAuthError> res, DataRegIdPw data) {
		if(data.getUid().length()<8)
			return res.setError(EAuthError.userid_more_than_8);
		if(StrUtil.isAlphaNumeric(data.getUid())==false)
			return res.setError(EAuthError.userid_alphabet_and_digit);
		if(data.getPw().length()<6)
			return res.setError(EAuthError.pass_more_than_6);
		
		if( DbAppManager.getInst().findUid(data.getTokenScode(), data.getUid()) == true )
			return res.setError(EAuthError.already_exist_userid);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertUID(userid, data.getUid(), data.getPw());
		return doRegisterUser(res, userid, data.getUid(), authQuery, data);
	}
	
	private ResponseData<EAuthError> doRegEmail(ResponseData<EAuthError> res, DataRegEmail data) {
		if(StrUtil.isEmail(data.getEmail()) == false)
			return res.setError(EAuthError.invalid_email_format);
		
		if( DbAppManager.getInst().findEmail(data.getTokenScode(), data.getEmail()) == true )
			return res.setError(EAuthError.already_exist_email);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertEmail(userid, data.getEmail());
		return doRegisterUser(res, userid, data.getEmail(), authQuery, data);
	}
	
	private ResponseData<EAuthError> doRegPhone(ResponseData<EAuthError> res, DataRegPhone data) {
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAuthError.invalid_phoneno_format);
		
		if( DbAppManager.getInst().findPhoneno(data.getTokenScode(), data.getPhoneno()) == true )
			return res.setError(EAuthError.already_exist_phoneno);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertEmail(userid, data.getPhoneno());
		return doRegisterUser(res, userid, data.getPhoneno(), authQuery, data);
	}
	
	private ResponseData<EAuthError> doRegisterUser(ResponseData<EAuthError> res, String userid, String regId, String authQuery, DataRegUser data) {
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(regId+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		
		List<String> queries = new ArrayList<>();
		queries.add(authQuery);
		queries.add(DbTransaction.getInst().queryInsertUser(userid, data.getUsername(), data.getUsertype(), data.getOstype(), data.getOsversion(), data.getAppversion()));
		queries.add(DbTransaction.getInst().queryInsertToken(userid, data.getUuid(), tokenid, token, false));
		if(DbTransaction.getInst().transactionQuery(data.getTokenScode(), queries)==false)
			return res.setError(EAuthError.failed_register);
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAuthError.ok);
	}
	
	private ResponseData<EAuthError> doLogin(ResponseData<EAuthError> res, DataLogin data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		if(data.isValidUserToken() == true)
			return doLoginByToken(res, data);
		return doLoginByIdPw(res, data);
	}
	
	private ResponseData<EAuthError> doLoginByToken(ResponseData<EAuthError> res, DataLogin data) {	//already signed user who has token
		if(data.isValidUuid()==false)
			return res.setError(EAuthError.invalid_user_token);
		
		RecUserToken token = DbAppManager.getInst().getUserTokenByTokenId(data.getTokenScode(), data.getTokenid());
		if(token==DbRecord.Empty)
			return res.setError(EAuthError.invalid_user_tokenid);
		if(data.getTokenUuid().equals(token.uuid)==false)
			return res.setError(EAuthError.invalid_or_expired_token);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuth(data.getTokenScode(), token.userid);
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.unauthorized_userid);
		if(auth.isSameUid(data.getUid())==false || auth.isSamePw(data.getPw())==false)
			return res.setError(EAuthError.invalid_user);
		
		if(DbAppManager.getInst().enableToken(data.getTokenScode(), token.userid, token.tokenid, true) == false)
			return res.setError(EAuthError.unknown_error);
		
		return res.setError(EAuthError.ok);
	}
	
	private ResponseData<EAuthError> doLoginByIdPw(ResponseData<EAuthError> res, DataLogin data) {		//new login using id and pw
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByUid(data.getScode(), data.getUid());
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.not_exist_user);
		if(auth.isSamePw(data.getPw())==false)
			return res.setError(EAuthError.invalid_user);
		RecUserToken recToken = DbAppManager.getInst().getUserTokenByUserId(data.getScode(), auth.getUserid());
		if(recToken==DbRecord.Empty)
			return res.setError(EAuthError.unauthorized_userid);
		
		if(recToken.uuid.equals(data.getUuid()) == true) {
			String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+auth.getAuthtype());
			DbAppManager.getInst().updateToken(data.getScode(), auth.getUserid(), recToken.tokenid, token, true);
			return res.setParam("tid", recToken.tokenid).setParam("token", token).setError(EAuthError.ok);
		}
		
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+EUserAuthType.uid.getValue());
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryDeleteTokenByUuid(auth.getUserid(), recToken.uuid));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, true));
		if(DbTransaction.getInst().transactionQuery(data.getTokenScode(), queries)==false)
			return res.setError(EAuthError.failed_update_token);
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAuthError.ok);
	}
	/** 
	 * login data
	 * @param ch
	 * @param res
	 * @param userData, [token][appcode]
	 * 			token => AES256([userid][uuid][authtype])
	 * @return the lasttime which is last joined time
	 */
	private ResponseData<EAuthError> doSignin(Channel ch, ResponseData<EAuthError> res, DataSignIn data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		if(data.isValidUserToken()==false || data.isValidUuid()==false)
			return res.setError(EAuthError.invalid_user_token);
		
		RecUserToken token = DbAppManager.getInst().getUserTokenByTokenId(data.getTokenScode(), data.getTokenid());
		if(token==DbRecord.Empty)
			return res.setError(EAuthError.invalid_user_tokenid);
		if(data.getTokenUuid().equals(token.uuid)==false)
			return res.setError(EAuthError.invalid_or_expired_token);
		if(token.enabled == false)
			return res.setError(EAuthError.unauthorized_token);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuth(data.getTokenScode(), token.userid);
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.unauthorized_userid);
		
		RecUser user = DbAppManager.getInst().getUser(data.getTokenScode(), token.userid);
		if(DbRecord.Empty == user)
			return res.setError(EAuthError.not_exist_userinfo);
		if(user.isSameApt(data.getTokenScode()) == false)
			DbAppManager.getInst().updateAppCode(data.getTokenScode(), token.userid, data.getTokenScode());	//update apt code
		DbAppManager.getInst().addEpid(data.getScode(), token.userid, data.getUuid(), data.getEpid());	//register epid
		user.inappcode = data.getTokenAppId();
		
		AuthSession session = new AuthSession(ch, 1).putSession(user, data.getTokenScode());	//consider the sessionid to find instance when close
		SessionManager.getInst().put(session);
		ch.attr(super.attrAuthSessionKey).set(session);
		
		sessionService.addUserSession(token.userid, StrUtil.getHostIp());	//save to redis
		
		return res.setError(EAuthError.ok).setParam(""+user.lasttime);
	}

	private ResponseData<EAuthError> doUpdatePW(ResponseData<EAuthError> res, DataUpdateIdUser data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAuthError.invalid_uuid);
		if(data.getNewpw().length()<6)
			return res.setError(EAuthError.pass_more_than_6);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByUid(data.getScode(), data.getUid());
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.not_exist_user);
		if(auth.isSamePw(data.getPw())==false)
			return res.setError(EAuthError.mismatch_pw);
		
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryDeleteTokenByUuid(auth.getUserid(), data.getUuid()));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		queries.add(DbTransaction.getInst().queryUpdateUser(auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion()));
		queries.add(DbTransaction.getInst().queryUpdatePw(data.getUid(), data.getNewpw()));
		if(DbTransaction.getInst().transactionQuery(data.getTokenScode(), queries)==false)
			return res.setError(EAuthError.failed_change_pw);
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAuthError.ok);
	}
	
	private ResponseData<EAuthError> doUpdateEmail(ResponseData<EAuthError> res, DataUpdateEmailUser data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAuthError.invalid_uuid);
		if(StrUtil.isEmail(data.getEmail()) == false)
			return res.setError(EAuthError.invalid_email_format);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByEmail(data.getScode(), data.getEmail());
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.not_exist_user);
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getEmail()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		String emailcode = StrUtil.getSha1Uuid("ec");
		
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryUpdateEmailCode(data.getEmail(), emailcode));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		queries.add(DbTransaction.getInst().queryUpdateUser(auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion()));
		if(DbTransaction.getInst().transactionQuery(data.getTokenScode(), queries)==false)
			return res.setError(EAuthError.failed_email_verify);
		
		//[TODO] Send Email
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAuthError.ok);
	}

	private ResponseData<EAuthError> doUpdatePhoneNo(ResponseData<EAuthError> res, DataUpdatePhoneUser data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAuthError.invalid_uuid);
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAuthError.invalid_phoneno_format);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByPhone(data.getScode(), data.getPhoneno());
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.not_exist_user);
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getPhoneno()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		String smscode = "" + (new Random().nextInt(9999-1000)+1000);
		
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryUpdateSMSCode(data.getPhoneno(), smscode));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		queries.add(DbTransaction.getInst().queryUpdateUser(auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion()));
		if(DbTransaction.getInst().transactionQuery(data.getTokenScode(), queries)==false)
			return res.setError(EAuthError.failed_phone_Verify);
		
		//[TODO] Send SMS
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAuthError.ok);
	}
	
	private ResponseData<EAuthError> doVerifyPhoneNo(ResponseData<EAuthError> res, DataVerifyPhoneUser data) {
		if(data.isValidAppToken()==false)
			return res.setError(EAuthError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAuthError.invalid_uuid);
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAuthError.invalid_phoneno_format);
		if(data.getSmscode()==null || data.getSmscode().length() != 4)
			return res.setError(EAuthError.smscode_size_4);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAuthError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByPhone(data.getScode(), data.getPhoneno());
		if(DbRecord.Empty == auth)
			return res.setError(EAuthError.not_exist_user);
		if(auth.isSameSmsCode(data.getSmscode())==false)
			return res.setError(EAuthError.mismatch_smscode);
		
		if(DbAppManager.getInst().enableToken(data.getTokenScode(), auth.getUserid(), data.getTokenid(), true) == false)
			return res.setError(EAuthError.unknown_error);
		
		return res.setError(EAuthError.ok);
	}
}
