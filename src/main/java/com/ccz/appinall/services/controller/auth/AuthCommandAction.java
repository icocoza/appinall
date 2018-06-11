package com.ccz.appinall.services.controller.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.common.rdb.DbTransaction;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.library.util.ShortUUID;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.RecDataAuth.*;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.enums.EUserAuthType;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.services.service.SessionService;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class AuthCommandAction extends CommonAction {

	@Autowired
	SessionService sessionService;	//to save redis
	@Autowired
	SessionManager sessionManager;	//to save local memory
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public AuthCommandAction() {
		super.setCommandFunction(EAllCmd.find_id, doFindId);
		super.setCommandFunction(EAllCmd.reg_idpw, doRegIdPw);
		super.setCommandFunction(EAllCmd.reg_email, doRegEmail);
		super.setCommandFunction(EAllCmd.reg_phone, doRegPhone);
		super.setCommandFunction(EAllCmd.login, doLogin);
		super.setCommandFunction(EAllCmd.anony_login, doAnonyLogin);
		super.setCommandFunction(EAllCmd.anony_login_gps, doAnonyLoginGps);
		super.setCommandFunction(EAllCmd.signin, doSignin);
		super.setCommandFunction(EAllCmd.anony_signin, doSignin);
		super.setCommandFunction(EAllCmd.change_pw, doUpdatePW);
		super.setCommandFunction(EAllCmd.reissue_email, doUpdateEmail);
		super.setCommandFunction(EAllCmd.reissue_phone, doUpdatePhoneNo);
		//super.setCommandFunction(EAllCmd.verify_email, null);
		super.setCommandFunction(EAllCmd.verify_sms, doVerifyPhoneNo);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EAllError> res = new ResponseData<EAllError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		//AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EAllError>) cmdFunc.doAction(ch, res, jdata);
			send(ch, res.toJsonString());
			return true;
		}
		return false;
	}
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doFindId = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataUserId data = new RecDataAuth().new DataUserId(jnode);
		if(data.getUid().length()<8)
			return res.setError(EAllError.userid_more_than_8);
		if(StrUtil.isAlphaNumeric(data.getUid())==false)
			return res.setError(EAllError.userid_alphabet_and_digit);
		if( DbAppManager.getInst().findUid(data.getScode(), data.getUid()) == true )
			return res.setError(EAllError.already_exist_userid);
		return res.setError(EAllError.ok);
	};

	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doRegIdPw = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataRegIdPw data = new RecDataAuth().new DataRegIdPw(jnode);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		if(data.getUid().length()<8)
			return res.setError(EAllError.userid_more_than_8);
		if(StrUtil.isAlphaNumeric(data.getUid())==false)
			return res.setError(EAllError.userid_alphabet_and_digit);
		if(data.getPw().length()<6)
			return res.setError(EAllError.pass_more_than_6);
		
		if( DbAppManager.getInst().findUid(data.getScode(), data.getUid()) == true )
			return res.setError(EAllError.already_exist_userid);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertUID(userid, data.getUid(), data.getPw());
		return doRegisterUser(res, userid, data.getUid(), authQuery, data, false);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doRegEmail = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataRegEmail data = new RecDataAuth().new DataRegEmail(jnode);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		if(StrUtil.isEmail(data.getEmail()) == false)
			return res.setError(EAllError.invalid_email_format);
		
		if( DbAppManager.getInst().findEmail(data.getScode(), data.getEmail()) == true )
			return res.setError(EAllError.already_exist_email);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertEmail(userid, data.getEmail());
		return doRegisterUser(res, userid, data.getEmail(), authQuery, data, false);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doRegPhone = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataRegPhone data = new RecDataAuth().new DataRegPhone(jnode);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAllError.invalid_phoneno_format);
		
		if( DbAppManager.getInst().findPhoneno(data.getScode(), data.getPhoneno()) == true )
			return res.setError(EAllError.already_exist_phoneno);
		
		String userid = KeyGen.makeKeyWithSeq("us");
		String authQuery = DbTransaction.getInst().queryInsertEmail(userid, data.getPhoneno());
		return doRegisterUser(res, userid, data.getPhoneno(), authQuery, data, false);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doLogin = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataLogin data = new RecDataAuth().new DataLogin(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		if(data.isValidUserToken() == true)
			return doLoginByToken(res, data);
		return doLoginByIdPw(res, data);
	};
	
	/** 
	 * login data
	 * @param ch
	 * @param res
	 * @param userData, [token][appcode]
	 * 			token => AES256([userid][uuid][authtype])
	 * @return the lasttime which is last joined time
	 */
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doSignin = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataSignIn data = new RecDataAuth().new DataSignIn(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		
		if(data.isValidUserToken()==false || data.isValidUuid()==false)
			return res.setError(EAllError.invalid_user_token);
		
		RecUserToken token = DbAppManager.getInst().getUserTokenByTokenId(data.getScode(), data.getTokenid());
		if(token==DbRecord.Empty)
			return res.setError(EAllError.invalid_user_tokenid);
		if(data.getTokenUuid().equals(token.uuid)==false)
			return res.setError(EAllError.invalid_or_expired_token);
		if(token.enabled == false)
			return res.setError(EAllError.unauthorized_token);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuth(data.getScode(), token.userid);
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.unauthorized_userid);
		
		RecUser user = DbAppManager.getInst().getUser(data.getScode(), token.userid);
		if(DbRecord.Empty == user)
			return res.setError(EAllError.not_exist_userinfo);
		//if(user.isSameAppCode(data.getScode()) == false)
		//	DbAppManager.getInst().updateAppCode(data.getScode(), token.userid, data.getScode());	//update apt code
		//user.inappcode = data.getTokenAppId();
		
		AuthSession session = new AuthSession(ch, 1).putSession(user, data.getScode());	//consider the sessionid to find instance when close
		session.setSessionData(sessionService.addUserSession(token.userid, StrUtil.getHostIp()));	//save to redis
		ch.attr(chAttributeKey.getAuthSessionKey()).set(session);
		sessionManager.put(session);
		
		return res.setError(EAllError.ok).setParam(""+user.lasttime);
	};

	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doUpdatePW = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataUpdateIdUser data = new RecDataAuth().new DataUpdateIdUser(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAllError.invalid_uuid);
		if(data.getNewpw().length()<6)
			return res.setError(EAllError.pass_more_than_6);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByUid(data.getScode(), data.getUid());
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		if(auth.isSamePw(data.getPw())==false)
			return res.setError(EAllError.mismatch_pw);
		
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryDeleteTokenByUuid(auth.getUserid(), data.getUuid()));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		queries.add(DbTransaction.getInst().queryUpdatePw(data.getUid(), data.getNewpw()));
		if(DbTransaction.getInst().transactionQuery(data.getScode(), queries)==false)
			return res.setError(EAllError.failed_change_pw);
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAllError.ok);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doUpdateEmail = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataUpdateEmailUser data = new RecDataAuth().new DataUpdateEmailUser(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAllError.invalid_uuid);
		if(StrUtil.isEmail(data.getEmail()) == false)
			return res.setError(EAllError.invalid_email_format);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByEmail(data.getScode(), data.getEmail());
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getEmail()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		String emailcode = StrUtil.getSha1Uuid("ec");
		
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryUpdateEmailCode(data.getEmail(), emailcode));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		if(DbTransaction.getInst().transactionQuery(data.getScode(), queries)==false)
			return res.setError(EAllError.failed_email_verify);
		
		//[TODO] Send Email
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAllError.ok);
	};

	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doUpdatePhoneNo = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataUpdatePhoneUser data = new RecDataAuth().new DataUpdatePhoneUser(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAllError.invalid_uuid);
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAllError.invalid_phoneno_format);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByPhone(data.getScode(), data.getPhoneno());
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getPhoneno()+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		String smscode = "" + (new Random().nextInt(9999-1000)+1000);
		
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryUpdateSMSCode(data.getPhoneno(), smscode));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, false));
		if(DbTransaction.getInst().transactionQuery(data.getScode(), queries)==false)
			return res.setError(EAllError.failed_phone_Verify);
		
		//[TODO] Send SMS
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAllError.ok);
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doVerifyPhoneNo = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataVerifyPhoneUser data = new RecDataAuth().new DataVerifyPhoneUser(jnode);
		if(data.isValidAppToken()==false)
			return res.setError(EAllError.invalid_app_token);
		if(data.getUuid() == null || data.getUuid().length() < 1)
			return res.setError(EAllError.invalid_uuid);
		if(StrUtil.isPhone(data.getPhoneno()) == false)
			return res.setError(EAllError.invalid_phoneno_format);
		if(data.getSmscode()==null || data.getSmscode().length() != 4)
			return res.setError(EAllError.smscode_size_4);
		RecAdminApp app = DbAppManager.getInst().getApp(data.getTokenAppId());	
		if(app == DbRecord.Empty)
			return res.setError(EAllError.wrong_appid);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByPhone(data.getScode(), data.getPhoneno());
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		if(auth.isSameSmsCode(data.getSmscode())==false)
			return res.setError(EAllError.mismatch_smscode);
		
		if(DbAppManager.getInst().enableToken(data.getScode(), auth.getUserid(), data.getTokenid(), true) == false)
			return res.setError(EAllError.unknown_error);
		
		return res.setError(EAllError.ok);
	};
	
	/**
	 * 
	 * @param res
	 * @param data
	 * 		scode, rcode, cmd
	 * 		regtoken
	 * 		uid, inappcode, uuid
	 * 		epid, ostype, osversion, appversion
	 * @return
	 */
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doAnonyLogin = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataAnonyLogin data = new RecDataAuth().new DataAnonyLogin(jnode);
		return doCommonAnonyLogin(res, data);
	};
	
	private ResponseData<EAllError> doCommonAnonyLogin(ResponseData<EAllError> res, DataAnonyLogin data) {
		if( DbAppManager.getInst().findUid(data.getScode(), data.getUid()) == true )
			return res.setError(EAllError.already_exist_userid);

		String userid = KeyGen.makeKeyWithSeq("anonyus");
		String authQuery = DbTransaction.getInst().queryInsertUID(userid, data.getUid(), data.getUid()); //anonymous user는 uid(client defined)와 inappcode를 pw로 사용함(즉, passcode 없음) 
		DataRegUser regUser = new RecDataAuth().new DataRegUser(data.getUuid(), ShortUUID.next(), data);
		
		if(doRegisterUser(res, userid, data.getUid(), authQuery, regUser, true).getError() != EAllError.ok)
			return res;

		RecUserAuth auth = DbAppManager.getInst().getUserAuth(data.getScode(), userid);
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		RecUserToken recToken = DbAppManager.getInst().getUserTokenByUserId(data.getScode(), auth.getUserid());
		if(recToken==DbRecord.Empty)
			return res.setError(EAllError.unauthorized_userid);
		
		DbAppManager.getInst().addEpid(data.getScode(), auth.getUserid(), data.getUuid(), data.getEpid());	//register epid
		DbAppManager.getInst().updateUserInfo(data.getScode(), auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion());
		
		res.setUserid(userid);
		return res.setError(EAllError.ok);
	}
	
	//[TODO] Need to check the gps..
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doAnonyLoginGps = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataAnonyLoginGps data = new RecDataAuth().new DataAnonyLoginGps(jnode);
		res = doCommonAnonyLogin(res, data);
		if(res.getError() != EAllError.ok)
			return res;
		RecAddress addr = DbAppManager.getInst().getAddress(data.getScode(), data.getBuildid());
		if(addr == null)
			return res.setError(EAllError.not_exist_building);
		DbAppManager.getInst().updateAppCode(data.getScode(), res.getUserid(), data.getBuildid());
		
		return res.setError(EAllError.ok);
	};
	
	private ResponseData<EAllError> doRegisterUser(ResponseData<EAllError> res, String userid, String regId, String authQuery, DataRegUser data, boolean enableToken) {
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(regId+ASS.UNIT+data.getUuid()+ASS.UNIT+data.getAuthtype());
		
		List<String> queries = new ArrayList<>();
		queries.add(authQuery);
		queries.add(DbTransaction.getInst().queryInsertUser(userid, data.getUsername(), data.isAnonymous()));
		queries.add(DbTransaction.getInst().queryInsertToken(userid, data.getUuid(), tokenid, token, enableToken));
		if(DbTransaction.getInst().transactionQuery(data.getScode(), queries)==false)
			return res.setError(EAllError.failed_register);
		
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAllError.ok);
	}
	
	private ResponseData<EAllError> doLoginByToken(ResponseData<EAllError> res, DataLogin data) {
		if(data.isValidUuid()==false)
			return res.setError(EAllError.invalid_user_token);
		
		RecUserToken token = DbAppManager.getInst().getUserTokenByTokenId(data.getScode(), data.getTokenid());
		if(token==DbRecord.Empty)
			return res.setError(EAllError.invalid_user_tokenid);
		if(data.getTokenUuid().equals(token.uuid)==false)
			return res.setError(EAllError.invalid_or_expired_token);
		
		RecUserAuth auth = DbAppManager.getInst().getUserAuth(data.getScode(), token.userid);
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.unauthorized_userid);
		if(auth.isSameUid(data.getUid())==false || auth.isSamePw(data.getPw())==false)
			return res.setError(EAllError.invalid_user);
		
		if(DbAppManager.getInst().enableToken(data.getScode(), token.userid, token.tokenid, true) == false)
			return res.setError(EAllError.unknown_error);
		DbAppManager.getInst().updateUserInfo(data.getScode(), auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion());
		return res.setError(EAllError.ok);
	}
	
	private ResponseData<EAllError> doLoginByIdPw(ResponseData<EAllError> res, DataLogin data) {
		RecUserAuth auth = DbAppManager.getInst().getUserAuthByUid(data.getScode(), data.getUid());
		if(DbRecord.Empty == auth)
			return res.setError(EAllError.not_exist_user);
		if(auth.isSamePw(data.getPw())==false)
			return res.setError(EAllError.invalid_user);
		RecUserToken recToken = DbAppManager.getInst().getUserTokenByUserId(data.getScode(), auth.getUserid());
		if(recToken==DbRecord.Empty)
			return res.setError(EAllError.unauthorized_userid);
		
		if(recToken.uuid.equals(data.getUuid()) == true) {
			String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+auth.getAuthtype());
			DbAppManager.getInst().updateToken(data.getScode(), auth.getUserid(), recToken.tokenid, token, true);
			return res.setParam("tid", recToken.tokenid).setParam("token", token).setError(EAllError.ok);
		}
		
		String tokenid = StrUtil.getSha1Uuid("tid");
		String token = Crypto.AES256Cipher.getInst().enc(data.getUid()+ASS.UNIT+data.getUuid()+ASS.UNIT+EUserAuthType.uid.getValue());
		List<String> queries = new ArrayList<>();
		queries.add(DbTransaction.getInst().queryDeleteTokenByUuid(auth.getUserid(), recToken.uuid));
		queries.add(DbTransaction.getInst().queryInsertToken(auth.getUserid(), data.getUuid(), tokenid, token, true));
		if(DbTransaction.getInst().transactionQuery(data.getScode(), queries)==false)
			return res.setError(EAllError.failed_update_token);

		DbAppManager.getInst().addEpid(data.getScode(), auth.getUserid(), data.getUuid(), data.getEpid());	//register epid
		DbAppManager.getInst().updateUserInfo(data.getScode(), auth.getUserid(), data.getOstype(), data.getOsversion(), data.getAppversion());
		return res.setParam("tid", tokenid).setParam("token", token).setError(EAllError.ok);
	}
}
