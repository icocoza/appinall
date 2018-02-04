package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecPushFailMsg extends DbRecord {
	static final String TBL_NAME = "pushfail";
	
	public String msgid;
	public String devuuid, userid, epid;
	public String msg;
	public Timestamp regtime;
	
	public RecPushFailMsg(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (msgid VARCHAR(64) NOT NULL PRIMARY KEY,"
				+ "devuuid VARCHAR(128) NOT NULL, userid VARCHAR(64) NOT NULL, epid VARCHAR(256) NOT NULL, msg VARCHAR(512) NOT NULL, "
				+ "regtime DATETIME DEFAULT now(), INDEX idx_userid(userid)) ", RecPushFailMsg.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecPushFailMsg rec = (RecPushFailMsg)r;
		rec.msgid = rd.getString("msgid");
		rec.devuuid = rd.getString("devuuid");
		rec.userid = rd.getString("userid");
		rec.epid = rd.getString("epid");
		rec.msg = rd.getString("msg");
		rec.regtime = rd.getDate("regtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecPushFailMsg(poolName));
	}

	public DbRecord insert(String msgid, String devuuid, String userid, String epid, String msg) {
		this.devuuid = devuuid;
		this.userid = userid;
		this.epid = epid;
		
		String sql = String.format("INSERT INTO %s (msgid, devuuid, userid, epid, msg) VALUES('%s', '%s', '%s', '%s', '%s')", 
				RecPushFailMsg.TBL_NAME, msgid, devuuid, userid, epid, msg);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String msgid) {
		String sql = String.format("DELETE FROM %s WHERE msgid='%s'", RecPushFailMsg.TBL_NAME, msgid);
		return super.delete(sql);
	}

	public List<RecPushFailMsg> getMsgs(int offset, int count) {
		String sql = String.format("SELECT * FROM %s OFFSET %d, LIMIT %d", RecPushFailMsg.TBL_NAME, offset, count);
		return super.getList(sql).stream().map(e->(RecPushFailMsg)e).collect(Collectors.toList());
	}

}
