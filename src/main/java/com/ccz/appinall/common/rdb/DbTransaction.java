package com.ccz.appinall.common.rdb;

import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.dbhelper.DbHelper;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.enums.EUserType;
import com.ccz.appinall.services.model.db.RecAddress;
import com.ccz.appinall.services.model.db.RecBoardScrap;
import com.ccz.appinall.services.model.db.RecDeliveryOrder;
import com.ccz.appinall.services.model.db.RecDeliveryPhoto;
import com.ccz.appinall.services.model.db.RecScrap;
import com.ccz.appinall.services.model.db.RecScrapBody;
import com.ccz.appinall.services.model.db.RecUser;
import com.ccz.appinall.services.model.db.RecUserAuth;
import com.ccz.appinall.services.model.db.RecUserBoardTableList;
import com.ccz.appinall.services.model.db.RecUserToken;
import com.ccz.appinall.services.model.db.RecUserVoter;

public class DbTransaction {
	public static DbTransaction s_pThis;
	public static DbTransaction getInst() { return s_pThis = (s_pThis == null ? new DbTransaction() : s_pThis); }
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

	public String queryInsertUser(String userid, String username, boolean anonymous) {
		return RecUser.qInsert(userid, username, anonymous); 
	}

	public String queryInsertUser(String userid, String username, boolean anonymous, String ostype, String osversion, String appversion) {
		return RecUser.qInsert(userid, username, anonymous, ostype, osversion, appversion); 
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
	
	public String queryInsertAddress(String buildid, String zip, String sido, String sigu, String eub, String roadname, String delivery, 
			 String buildname, String dongname, String liname, String hjdongname,
			 int buildno, int buildsubno, int jino, int jisubno, double lon, double lat) {
		return RecAddress.qInsertAddress(buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname, buildno, buildsubno, jino, jisubno, lon, lat);
	}
	
	public String queryUpdateUserLike(String userid, boolean like, boolean cancel) {
		return RecUser.qUpdateUserLike(userid, like, cancel);
	}
	
	public String queryInsertVoterUser(String deliverid, String orderid, String senderid, int point, boolean like, String comments) {
		return RecUserVoter.qInsert(deliverid, orderid, senderid, point, like, comments);
	}
	
	public String queryDeleteVoterUser(String deliverid, String orderid, String senderid) {
		return RecUserVoter.qDelete(deliverid, orderid, senderid);
	}
	
	public String queryInsertOrderFile(String fileid, String orderid, String userid, EUserType usertype) {
		return RecDeliveryPhoto.qInsert(fileid, orderid, userid, usertype);
	}
	
	public String queryUpdatePhotoUrl(String orderid, String photourl) {
		return RecDeliveryOrder.qUpdatePhotoUrl(orderid, photourl);
	}
	
	public String queryAddUserTable(String userid, String tableid, String title, int tablepos) {
		return RecUserBoardTableList.qInsertUserTable(userid, tableid, title, tablepos);
	}

	public String queryInsertScrap(String scrapid, String url, String title, String subtitle) {
		return RecScrap.qInsertScrap(scrapid, url, title, subtitle);
	}

	public String queryInsertScrapId(String boardid, String scrapid) {
		return RecBoardScrap.qInsertScrap(boardid, scrapid);
	}
	public String queryInsertScrapBody(String scrapid, String body) {
		return RecScrapBody.qInsertScrapBody(scrapid, body);
	}
	
	public boolean insertTransactionalScrap(String scode, String scrapid, String url, String title, String subtitle, String body) {
		List<String> queries = new ArrayList<>();
		queries.add(this.queryInsertScrap(scrapid, url, title, subtitle));
		queries.add(this.queryInsertScrapBody(scrapid, body));
		return DbTransaction.getInst().transactionQuery(scode, queries);
	}
}
