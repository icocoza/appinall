package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table(name="epid")
public class RecPushToken extends DbRecord {
	static final String TBL_NAME = "epid";
	
	public String devuuid, userid, epid;
	public Timestamp regtime;
	
	public RecPushToken(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (devuuid VARCHAR(128) NOT NULL, userid VARCHAR(64) NOT NULL, "
				+ "epid VARCHAR(256) NOT NULL, regtime DATETIME DEFAULT  now(), PRIMARY KEY (devuuid), INDEX idx_userid(userid)) ", RecPushToken.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecPushToken rec = (RecPushToken)r;
		rec.devuuid = rd.getString("devuuid");
		rec.userid = rd.getString("userid");
		rec.epid = rd.getString("epid");
		rec.regtime = rd.getDate("regtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecPushToken(poolName));
	}

	public DbRecord insert(String devuuid, String userid, String scode, String epid) {
		this.devuuid = devuuid;
		this.userid = userid;
		this.epid = epid;
		
		String sql = String.format("INSERT INTO %s (devuuid, userid, epid) VALUES('%s', '%s', '%s')", RecPushToken.TBL_NAME, devuuid, userid, epid);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String devuuid) {
		String sql = String.format("DELETE FROM %s WHERE devuuid='%s'", RecPushToken.TBL_NAME, devuuid);
		return super.delete(sql);
	}

	public RecPushToken getEpid(String devuuid) {
		String sql = String.format("SELECT * FROM %s WHERE devuuid='%s'", RecPushToken.TBL_NAME, devuuid);
		return (RecPushToken) super.getOne(sql);
	}
	
	public boolean updateEpid(String devuuid, String epid) {
		String sql = String.format("UPDATE %s SET epid='%s' WHERE devuuid='%s'", RecPushToken.TBL_NAME, epid, devuuid);
		return super.update(sql);
	}

}
/* {
	@Id
	@Column(length = 128, nullable = false)
	private String devuuid;
	
	@Column(length = 256, nullable = false)
	private String epid;
	
	@Column(length = 12, nullable = false)
	private String scode;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date regtime;
	
	public RecEpid() {	}
	
	public RecEpid(String devuuid, String epid, String scode) {	
		this.devuuid = devuuid;
		this.epid = epid;
		this.scode = scode;
	}
	
	public void updateEpid(String epid) {
		this.epid = epid;
	}
}
*/