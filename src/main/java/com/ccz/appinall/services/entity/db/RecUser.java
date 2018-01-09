package com.ccz.appinall.services.entity.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table( name="user",
//	indexes = {	@Index(name = "idx_user_devuuid",  columnList="devuuid", unique = false)})
public class RecUser extends DbRecord {
	static final String TBL_NAME = "user";
	
	public String userid;
	public String devuuid;
	public String username, usertype;
	public String ostype, osversion, appversion;
	public String email, inappcode; //optional
	public long   jointime, leavetime, lasttime;

	public RecUser(String poolName) {
		super(poolName);
		
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL PRIMARY KEY, devuuid VARCHAR(128) NOT NULL, "
				+ "username VARCHAR(64) NOT NULL, usertype VARCHAR(4) DEFAULT 'u', ostype VARCHAR(16), osversion VARCHAR(8), appversion VARCHAR(8), email VARCHAR(64), "
				+ "inappcode VARCHAR(8), jointime LONG, leavetime LONG, lasttime LONG, INDEX (devuuid)) ", RecUser.TBL_NAME);
		
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUser rec = (RecUser)r;
		rec.userid = rd.getString("userid");
		rec.devuuid = rd.getString("devuuid");
		rec.username = rd.getString("username");
		rec.usertype = rd.getString("usertype");
		rec.ostype = rd.getString("ostype");
		rec.osversion = rd.getString("osversion");
		rec.appversion = rd.getString("appversion");
		rec.email = rd.getString("email");
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
	
	public DbRecord insert(String userid, String devuuid, String username, String usertype, String ostype, String osversion, String appversion, String email) {
		this.userid = userid;
		this.devuuid = devuuid;
		this.username = username;
		this.usertype = usertype;
		this.ostype = ostype;
		this.osversion = osversion;
		this.appversion = appversion;
		this.email = email;
		this.jointime = System.currentTimeMillis();
		String sql = String.format("INSERT INTO %s (userid, devuuid, username, usertype, ostype, osversion, appversion, email, jointime, leavetime, lasttime) "
								 + "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, 0, 0)", RecUser.TBL_NAME,
								 userid, devuuid, username, usertype, ostype, osversion, appversion, email, this.jointime);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String userid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s'", RecUser.TBL_NAME, userid);
		return super.delete(sql);
	}

	public RecUser getUser(String userid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s'", RecUser.TBL_NAME, userid);
		return (RecUser) super.getOne(sql);
	}

	public RecUser getUserByUuid(String devuuid) {
		String sql = String.format("SELECT * FROM %s WHERE devuuid='%s'", RecUser.TBL_NAME, devuuid);
		return (RecUser) super.getOne(sql);
	}

	public boolean updateAptCode(String userid, String inappcode) {
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

	public boolean updateEmail(String userid, String email) {
		String sql = String.format("UPDATE %s SET email='%s' WHERE userid='%s'", RecUser.TBL_NAME, email, userid);
		return super.update(sql);
	}
	
	public boolean isSameApt(String inappcode) {
		return this.inappcode!=null && inappcode.equals(inappcode);
	}
	
}
/* {
	@Id
	@Column(length = 64, nullable = false)
	public String userid;
	
	@Column(length = 128, nullable = false)
	public String devuuid;
	
	@Column(length = 32, nullable = false)
	public String username;
	
	@Column(length = 4, nullable = false)
	@ColumnDefault("u")
	public String usertype;
	
	@Column(length = 16)
	public String ostype;
	
	@Column(length = 8)
	public String osversion;
	
	@Column(length = 8)
	public String appversion;
	
	@Column(length = 64)
	public String email;
	
	@Column(length = 8)
	public String inappcode; //optional
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date jointime;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date leavetime;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date lasttime;
	
	public RecUser() {	}
	
	public RecUser(String userid, String devuuid, String username, String usertype, String ostype, String osversion, String appversion, String email) {
		this.userid = userid;
		this.devuuid = devuuid;
		this.username = username;
		this.usertype = usertype;
		this.ostype = ostype;
		this.osversion = osversion;
		this.appversion = appversion;
		this.email = email;
	}
}
*/