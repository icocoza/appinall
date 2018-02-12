package com.ccz.appinall.common.rdb;

import java.util.List;

import com.ccz.appinall.library.dbhelper.DbHelper;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.model.db.RecUser;
import com.ccz.appinall.services.model.db.RecUserAuth;
import com.ccz.appinall.services.model.db.RecUserToken;

public class DbTransaction {
	public static DbTransaction s_pThis;
	
	public static DbTransaction getInst() {
		return s_pThis = (s_pThis == null ? new DbTransaction() : s_pThis);
	}
	public static void freeInst() {		s_pThis = null; 	}
	
	public boolean transactionQuery(String poolName, List<String> queries) {
		return DbHelper.multiQuery(poolName, queries.toArray(new String[queries.size()]));
	}
	
	public String queryInsertUID(String userid, String uid, String pw) {
		return RecUserAuth.qInsertUID(userid, uid, pw);
	}
	
	public String queryInsertEmail(String userid, String email) {
		return RecUserAuth.qInsertEmail(userid, email);
	}
	
	public String queryInsertPhoneNo(String userid, String phoneno) {
		return RecUserAuth.qInsertPhoneNo(userid, phoneno);
	}

	public String queryUpdateEmailCode(String email, String emailcode) {
		return RecUserAuth.qUpdateEmailCode(email, emailcode);
	}
	
	public String queryUpdateSMSCode(String phoneno, String smscode) {
		return RecUserAuth.qUpdateSMSCode(phoneno, smscode);
	}

	public String queryInsertToken(String userid, String uuid, String tokenid, String token, boolean enabled) {
		return RecUserToken.qInsertToken(userid, uuid, tokenid, token, enabled);
	}
	
	public String queryInsertUser(String userid, String username, String usertype, String ostype, String osversion, String appversion) {
		return RecUser.qInsert(userid, username, usertype, ostype, osversion, appversion); 
	}
	
	public String queryUpdateUser(String userid, String ostype, String osversion, String appversion) {
		return RecUser.qUpdateUser(userid, ostype, osversion, appversion);
	}
	
	public String queryDeleteTokenByUuid(String userid, String uuid) {
		return RecUserToken.qDeleteTokenByUuid(userid, uuid);
	}
	
	public String queryUpdatePw(String uid, String pw) {
		return RecUserAuth.qUpdatePw(uid, pw);
	}
	
}
