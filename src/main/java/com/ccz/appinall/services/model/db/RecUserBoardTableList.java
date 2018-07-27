package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.enums.EBoardService;
import com.ccz.appinall.services.enums.EUserAuthType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecUserBoardTableList extends DbRecord {
	
	static final String TBL_NAME = "usertablelist";
	@JsonIgnore	public String userid;
	@JsonIgnore	public String tableid;
	public String title;
	public int category;
	@JsonIgnore	public Timestamp createdAt;
	
	public RecUserBoardTableList(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "userid VARCHAR(64) NOT NULL, tableid VARCHAR(64) NOT NULL, title VARCHAR(64) NOT NULL, category INT, INDEX idx_userid(userid), INDEX idx_boardid(tableid))",  TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUserBoardTableList rec = (RecUserBoardTableList)r;
		rec.userid = rd.getString("userid");
		rec.tableid = rd.getString("tableid");
		rec.title = rd.getString("title");
		rec.createdAt = rd.getDate("createdAt");
		rec.category = rd.getInt("category");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserBoardTableList(super.poolName));
	}
	
	public DbRecord insertUserTable(String userid, String tableid, String title, int category) {
		return super.insert(qInsertUserTable(userid, tableid, title, category)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertUserTable(String userid, String tableid, String title, int category) {
		return String.format("INSERT INTO %s (userid, tableid, title, category) VALUES('%s', '%s', '%s', %d)", TBL_NAME, userid, tableid, title, category);
	}

	public List<RecUserBoardTableList> getUserTableList(String userid) {
		String sql = String.format("SELECT userid, tableid, title, category FROM %s WHERE userid='%s' ORDER BY category ASC" , TBL_NAME, userid);
		return super.getList(sql).stream().map(e->(RecUserBoardTableList)e).collect(Collectors.toList());
	}
	
}
