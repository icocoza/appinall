package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecUser extends DbRecord {
	static final String TBL_NAME = "user";
	
	public String userid;
	public String username;
	public boolean anonymous;
	public String ostype, osversion, appversion;
	public String appcode; //optional
	public Boolean enabledtoken;
	public Timestamp   jointime, leavetime, lasttime;
	public int likes, dislikes;

	public RecUser(String poolName) {
		super(poolName);
		
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "username VARCHAR(64) NOT NULL, anonymous BOOLEAN DEFAULT false, ostype VARCHAR(16), osversion VARCHAR(8), appversion VARCHAR(8), "
				+ "appcode VARCHAR(32), jointime DATETIME DEFAULT now(), leavetime DATETIME, lasttime DATETIME, likes INTEGER DEFAULT 0, dislikes INTEGER DEFAULT 0) ", RecUser.TBL_NAME);
		
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUser rec = (RecUser)r;
		rec.userid = rd.getString("userid");
		rec.username = rd.getString("username");
		rec.anonymous = rd.getBoolean("anonymous");
		rec.ostype = rd.getString("ostype");
		rec.osversion = rd.getString("osversion");
		rec.appversion = rd.getString("appversion");
		rec.appcode = rd.getString("appcode");
		rec.jointime = rd.getDate("jointime");
		rec.leavetime = rd.getDate("leavetime");
		rec.lasttime = rd.getDate("lasttime");
		rec.likes = rd.getInt("likes");
		rec.dislikes = rd.getInt("dislikes");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUser(super.poolName));
	}
	
	public DbRecord insert(String userid, String username, boolean isanonymous) {
		return super.insert(qInsert(userid, username, isanonymous)) ? this : DbRecord.Empty;
	}
	
	public DbRecord insert(String userid, String username, boolean isanonymous, String ostype, String osversion, String appversion) {
		return super.insert(qInsert(userid, username, isanonymous, ostype, osversion, appversion)) ? this : DbRecord.Empty;
	}
	
	static public String qInsert(String userid, String username, boolean isanonymous) {
		return String.format("INSERT INTO %s (userid, username, anonymous) VALUES('%s', '%s', %b)", RecUser.TBL_NAME, userid, username, isanonymous);
	}

	static public String qInsert(String userid, String username, boolean isanonymous, String ostype, String osversion, String appversion) {
		return String.format("INSERT INTO %s (userid, username, anonymous, ostype, osversion, appversion, jointime, leavetime, lasttime) "
								 + "VALUES('%s', '%s', %b, '%s', '%s', '%s', %d, 0, 0)", RecUser.TBL_NAME,
								 userid, username, isanonymous, ostype, osversion, appversion, System.currentTimeMillis());
	}

	public boolean updateUser(String userid, String ostype, String osversion, String appversion) {
		return super.update(qUpdateUser(userid, ostype, osversion, appversion));
	}
	
	static public String qUpdateUser(String userid, String ostype, String osversion, String appversion) {
		return String.format("UPDATE %s SET ostype='%s', osversion='%s', appversion='%s' WHERE userid='%s'", RecUser.TBL_NAME, ostype, osversion, appversion, userid);
	}

	public boolean delete(String userid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s'", RecUser.TBL_NAME, userid);
		return super.delete(sql);
	}

	public RecUser getUser(String userid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s'", RecUser.TBL_NAME, userid);
		return (RecUser) super.getOne(sql);
	}

	public boolean updateAppCode(String userid, String appcode) {
		String sql = String.format("UPDATE %s SET appcode='%s' WHERE userid='%s'", RecUser.TBL_NAME, appcode, userid);
		return super.update(sql);
	}

	public boolean updateLastVisit(String userid) {
		String sql = String.format("UPDATE %s SET lasttime=%d WHERE userid='%s'", RecUser.TBL_NAME, System.currentTimeMillis(), userid);
		return super.update(sql);
	}

	public boolean updateLeave(String userid) {
		String sql = String.format("UPDATE %s SET leavetime=%d WHERE userid='%s'", RecUser.TBL_NAME, System.currentTimeMillis(), userid);
		return super.update(sql);
	}

	public boolean updateUsername(String userid, String username) {
		String sql = String.format("UPDATE %s SET username='%s' WHERE userid='%s'", RecUser.TBL_NAME, username, userid);
		return super.update(sql);
	}
	
	public boolean changeAnonymousToNormal(String userid) {
		String sql = String.format("UPDATE %s SET anonymous=TRUE WHERE userid='%s'", RecUser.TBL_NAME, userid);
		return super.update(sql);
	}
	
	public boolean isSameAppCode(String appcode) {
		return this.appcode!=null && appcode.equals(appcode);
	}
	
	public boolean updateUserLike(String userid, boolean likes, boolean cancel) {
		return super.update(qUpdateUserLike(userid, likes, cancel));
	}
	
	static public String qUpdateUserLike(String userid, boolean likes, boolean cancel) {
		int value = cancel == false ? 1 : -1;
		return likes ? 
				String.format("UPDATE %s SET likes=likes+%d WHERE userid='%s'", RecUser.TBL_NAME, value, userid) :
				String.format("UPDATE %s SET dislikes=dislikes+%d WHERE userid='%s'", RecUser.TBL_NAME, value, userid);
	}
}
