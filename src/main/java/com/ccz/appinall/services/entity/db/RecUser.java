package com.ccz.appinall.services.entity.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecUser extends DbRecord {
	static final String TBL_NAME = "user";
	
	public String userid;
	public String username, usertype;
	public String ostype, osversion, appversion;
	public String inappcode; //optional
	public Boolean enabledtoken;
	public long   jointime, leavetime, lasttime;

	public RecUser(String poolName) {
		super(poolName);
		
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "username VARCHAR(64) NOT NULL, usertype VARCHAR(4) DEFAULT 'u', ostype VARCHAR(16), osversion VARCHAR(8), appversion VARCHAR(8), "
				+ "inappcode VARCHAR(8), jointime LONG, leavetime LONG, lasttime LONG) ", RecUser.TBL_NAME);
		
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUser rec = (RecUser)r;
		rec.userid = rd.getString("userid");
		rec.username = rd.getString("username");
		rec.usertype = rd.getString("usertype");
		rec.ostype = rd.getString("ostype");
		rec.osversion = rd.getString("osversion");
		rec.appversion = rd.getString("appversion");
		rec.inappcode = rd.getString("inappcode");
		rec.jointime = rd.getLong("jointime");
		rec.leavetime = rd.getLong("leavetime");
		rec.lasttime = rd.getLong("lasttime");
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
	
	public DbRecord insert(String userid, String username, String usertype, String ostype, String osversion, String appversion) {
		return super.insert(qInsert(userid, username, usertype, ostype, osversion, appversion)) ? this : DbRecord.Empty;
	}

	static public String qInsert(String userid, String username, String usertype, String ostype, String osversion, String appversion) {
		return String.format("INSERT INTO %s (userid, username, usertype, ostype, osversion, appversion, jointime, leavetime, lasttime) "
								 + "VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d, 0, 0)", RecUser.TBL_NAME,
								 userid, username, usertype, ostype, osversion, appversion, System.currentTimeMillis());
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

	public boolean updateAppCode(String userid, String inappcode) {
		String sql = String.format("UPDATE %s SET inappcode='%s' WHERE userid='%s'", RecUser.TBL_NAME, inappcode, userid);
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
	
	public boolean isSameApt(String inappcode) {
		return this.inappcode!=null && inappcode.equals(inappcode);
	}
	
}
