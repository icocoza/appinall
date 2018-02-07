package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EUserAuthType;

public class RecUserToken extends DbRecord {
	static final String TBL_NAME = "usertoken";
	
	public String userid, uuid;
	public String tokenid, token;
	public Timestamp createtime, expiretime;
	public boolean enabled;
	
	public RecUserToken(String poolName) {
		super(poolName);
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL, uuid VARCHAR(128), "
				+ "tokenid VARCHAR(64) NOT NULL, token VARCHAR(128) NOT NULL, "
				+ "createtime DATETIME DEFAULT now(), expiretime DATETIME, enabled BOOLEAN DEFAULT false, PRIMARY KEY(userid, tokenid), INDEX idx_uuid(uuid)) ", RecUserToken.TBL_NAME);
		
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUserToken rec = (RecUserToken)r;
		rec.userid = rd.getString("userid");
		rec.uuid = rd.getString("uuid");
		rec.tokenid = rd.getString("tokenid");
		rec.token = rd.getString("token");
		rec.createtime = rd.getDate("createtime");
		rec.expiretime = rd.getDate("expiretime");
		rec.enabled = rd.getBoolean("enabled");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserToken(super.poolName));
	}

	public RecUserToken getToken(String tokenid) {
		String sql = String.format("SELECT * FROM %s WHERE tokenid='%s'", RecUserToken.TBL_NAME, tokenid);
		return (RecUserToken) super.getOne(sql);
	}

	public RecUserToken getToken(String userid, String tokenid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' AND tokenid='%s'", RecUserToken.TBL_NAME, userid, tokenid);
		return (RecUserToken) super.getOne(sql);
	}

	public boolean insertToken(String userid, String uuid, String tokenid, String token) {
		return super.insert(qInsertToken(userid, uuid, tokenid, token));
	}

	static public String qInsertToken(String userid, String uuid, String tokenid, String token) {
		return String.format("INSERT INTO %s (userid, uuid, tokenid, token) VALUES('%s', '%s', '%s', '%s')", RecUserToken.TBL_NAME, userid, uuid, tokenid, token);
	}
	
	public boolean updateToken(String userid, String tokenid, String token) {
		String sql = String.format("UPDATE %s SET token='%s' WHERE userid='%s' AND tokenid='%s'", RecUserToken.TBL_NAME, token, userid, tokenid);
		return super.update(sql);
	}

	public boolean enableToken(String userid, String tokenid, boolean enabled) {
		String sql = String.format("UPDATE %s SET enabled=%b WHERE userid='%s' AND tokenid='%s'", RecUserToken.TBL_NAME, enabled, userid, tokenid);
		return super.update(sql);
	}
	
	static public String qDeleteTokenByUuid(String userid, String uuid) {
		return String.format("DELETE FROM %s WHERE userid='%s' AND uuid='%s'", RecUserToken.TBL_NAME, userid, uuid);
	}

	public boolean delete(String userid, String tokenid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s' AND token='%s'", RecUserToken.TBL_NAME, userid, tokenid);
		return super.delete(sql);
	}
	
	public boolean delete(String userid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s'", RecUserToken.TBL_NAME, userid);
		return super.delete(sql);
	}
}
