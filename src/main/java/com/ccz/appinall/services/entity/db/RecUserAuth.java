package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.type.enums.EUserAuthType;

import lombok.Getter;

public class RecUserAuth  extends DbRecord {
	static final String TBL_NAME = "userauth";
	@Getter
	String userid;
	String uid, email, phoneno;
	String pw, emailcode, smscode;
	public Timestamp regdate, leavedate;
	EUserAuthType authtype;
	
	public RecUserAuth(String poolName) {
		super(poolName);
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL PRIMARY KEY"
				+ "uid VARCHAR(64), email VARCHAR(64) DEFAULT '', phoneno VARCHAR(64) DEFAULT '', "
				+ "pw VARCHAR(64) DEFAULT '', emailcode VARCHAR(32) DEFAULT '', smscode VARCHAR(6) DEFAULT '', "
				+ "regdate DATETIME DEFAULT now(), leavedate DATETIME, authtype VARCHAR(12) NOT NULL,"
				+ "idx_uid(uid), INDEX idx_email(email), INDEX idx_phoneno(phoneno)) ", RecUserAuth.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUserAuth rec = (RecUserAuth)r;
		rec.userid = rd.getString("userid");
		rec.uid = rd.getString("uid");
		rec.email = rd.getString("email");
		rec.phoneno = rd.getString("phoneno");
		rec.pw = rd.getString("pw");
		rec.emailcode = rd.getString("emailcode"); 
		rec.smscode = rd.getString("smscode");
		rec.regdate = rd.getDate("regdate");
		rec.leavedate = rd.getDate("leavedate");
		rec.authtype = EUserAuthType.getType(rd.getString("authtype"));
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserAuth(super.poolName));
	}

	public DbRecord insertUID(String userid, String uid, String pw) {
		return super.insert(qInsertUID(userid, uid, pw)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertUID(String userid, String uid, String pw) {
		pw = StrUtil.getSha256(pw);
		return String.format("INSERT INTO %s (userid, uid, pw, authtype) VALUES('%s', '%s', '%s', '%s')", 
									RecUserAuth.TBL_NAME, userid, uid, pw, EUserAuthType.uid);
	}
	public DbRecord insertEmail(String userid, String email) {
		return super.insert(qInsertEmail(userid, email)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertEmail(String userid, String email) {
		return String.format("INSERT INTO %s (userid, email, authtype) VALUES('%s', '%s', '%s')", 
									RecUserAuth.TBL_NAME, userid, email, EUserAuthType.email);
	}

	public DbRecord insertPhoneNo(String userid, String phoneno) {
		return super.insert(qInsertPhoneNo(userid, phoneno)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertPhoneNo(String userid, String phoneno) {
		return String.format("INSERT INTO %s (userid, phoneno, authtype) VALUES('%s', '%s', '%s')", 
				RecUserAuth.TBL_NAME, userid, phoneno, EUserAuthType.phone);
	}
	
	public RecUserAuth getUser(String userid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s'", RecUserAuth.TBL_NAME, userid);
		return (RecUserAuth) super.getOne(sql);
	}

	public RecUserAuth getUserByUid(String uid) {
		String sql = String.format("SELECT * FROM %s WHERE uid='%s'", RecUserAuth.TBL_NAME, uid);
		return (RecUserAuth) super.getOne(sql);
	}

	public RecUserAuth getUserByEmail(String email) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s'", RecUserAuth.TBL_NAME, email);
		return (RecUserAuth) super.getOne(sql);
	}

	public RecUserAuth getUserByPhone(String phoneno) {
		String sql = String.format("SELECT * FROM %s WHERE phoneno='%s'", RecUserAuth.TBL_NAME, phoneno);
		return (RecUserAuth) super.getOne(sql);
	}

	public EUserAuthType findUserAuth(String uid, String email, String phoneno) {
		String sql = String.format("SELECT * FROM %s WHERE uid='%s' OR email='%s' OR phoneno='%s'", RecUserAuth.TBL_NAME, uid, email, phoneno);
		RecUserAuth auth = (RecUserAuth) super.getOne(sql);
		if(DbRecord.Empty == auth)
			return EUserAuthType.none;
		return auth.authtype;
	}
	
	public boolean findUid(String uid) {
		String sql = String.format("SELECT * FROM %s WHERE uid='%s'", RecUserAuth.TBL_NAME, uid);
		RecUserAuth user = (RecUserAuth) super.getOne(sql);
		if(DbRecord.Empty == user)
			return false;
		return true;
	}
	public boolean findEmail(String email) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s'", RecUserAuth.TBL_NAME, email);
		RecUserAuth user = (RecUserAuth) super.getOne(sql);
		if(DbRecord.Empty == user)
			return false;
		return true;
	}
	public boolean findPhoneno(String phoneno) {
		String sql = String.format("SELECT * FROM %s WHERE phoneno='%s'", RecUserAuth.TBL_NAME, phoneno);
		RecUserAuth user = (RecUserAuth) super.getOne(sql);
		if(DbRecord.Empty == user)
			return false;
		return true;
	}
	
	public boolean updatePw(String uid, String pw) {
		return super.update(qUpdatePw(uid, pw));
	}

	static public String qUpdatePw(String uid, String pw) {
		pw = StrUtil.getSha256(pw);
		return String.format("UPDATE %s SET pw='%s' WHERE uid='%s'", RecUserAuth.TBL_NAME, pw, uid);
	}

	public boolean updateEmailCode(String email, String emailcode) {
		return super.update(qUpdateEmailCode(email, emailcode));
	}
	
	static public String qUpdateEmailCode(String email, String emailcode) {
		return String.format("UPDATE %s SET emailcode='%s' WHERE email='%s'", RecUserAuth.TBL_NAME, emailcode, email);
	}
	
	public boolean updateSMSCode(String phoneno, String smscode) {
		return super.update(qUpdateSMSCode(phoneno, smscode));
	}

	static public String qUpdateSMSCode(String phoneno, String smscode) {
		return String.format("UPDATE %s SET smscode='%s' WHERE phoneno='%s'", RecUserAuth.TBL_NAME, smscode, phoneno);
	}

	public boolean updateUserQuit(String userid) {
		String sql = String.format("UPDATE %s SET authtype='%s' WHERE userid='%s'", RecUserAuth.TBL_NAME, EUserAuthType.quit, userid);
		return super.update(sql);
	}
	
	public boolean deleteUserId(String userid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s'", RecUserAuth.TBL_NAME, userid);
		return super.delete(sql);
	}

	public boolean isSameAuthId(EUserAuthType authType, String uid ) {
		if(EUserAuthType.uid == authType && this.uid != null && this.uid.equals(uid))
			return true;
		if(EUserAuthType.email == authType && this.email != null && this.email.equals(uid))
			return true;
		if(EUserAuthType.phone == authType && this.phoneno != null && this.phoneno.equals(uid))
			return true;
		return false;
	}
	
	public boolean isSameUid(String uid) {
		return this.uid!=null && this.uid.equals(uid);
	}
	
	public boolean isSamePw(String pw) {
		pw = StrUtil.getSha256(pw);
		return this.pw != null && this.pw.equals(pw);
	}
	
	public boolean isSameSmsCode(String smscode) {
		return this.smscode!=null && this.smscode.equals(smscode);
	}
}
