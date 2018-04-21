package com.ccz.appinall.services.model.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EBoardPreference;

//@Entity
//@Data
//@Table(name="boarduser")
public class RecBoardUser extends DbRecord {
	public static final String TBL_NAME = "boarduser";
	
	public String boardid, userid;
	public String username;
	public EBoardPreference preference;
	public long visittime;
	
	public RecBoardUser(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (boardid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL,"
				+ "preference VARCHAR(16) NOT NULL, username VARCHAR(32), visittime DATETIME, PRIMARY KEY(boardid, userid, preference))", 
				RecBoardUser.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardUser rec = (RecBoardUser)r;
		rec.boardid = rd.getString("boardid");
		rec.userid = rd.getString("userid");
		rec.username = rd.getString("username");
		rec.preference = EBoardPreference.getType(rd.getString("preference"));
		rec.visittime = rd.getLong("visittime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardUser(poolName));
	}

	public boolean insert(String boardid, String userid, String username, EBoardPreference preference) {
		String sql = String.format("INSERT INTO %s (boardid, userid, username, preference) "
				 + "VALUES('%s', '%s', '%s', '%s')", RecBoardUser.TBL_NAME, boardid, userid, username, preference);
		return super.insert(sql);
	}
	
	public boolean delete(String boardid) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s'", RecBoardUser.TBL_NAME, boardid);
		return super.delete(sql);
	}
	
	public boolean delete(String boardid, String userid, EBoardPreference preference ) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s' AND userid='%s' AND preference='%s'", 
				RecBoardUser.TBL_NAME, boardid, userid, preference);
		return super.delete(sql);
	}

}
