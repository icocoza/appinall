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
public class RecEpid extends DbRecord {
	static final String TBL_NAME = "epid";
	
	public String devuuid, epid;
	public Timestamp regtime;
	
	public RecEpid(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (devuuid VARCHAR(128) NOT NULL,"
				+ "epid VARCHAR(256) NOT NULL, regtime DATETIME DEFAULT  now(), PRIMARY KEY (devuuid)) ", RecEpid.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecEpid rec = (RecEpid)r;
		rec.devuuid = rd.getString("devuuid");
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
		return doLoad(rd, new RecEpid(poolName));
	}

	public DbRecord insert(String devuuid, String scode, String epid) {
		this.devuuid = devuuid;
		this.epid = epid;
		
		String sql = String.format("INSERT INTO %s (devuuid, epid) VALUES('%s', '%s')", RecEpid.TBL_NAME, devuuid, epid);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String devuuid) {
		String sql = String.format("DELETE FROM %s WHERE devuuid='%s'", RecEpid.TBL_NAME, devuuid);
		return super.delete(sql);
	}

	public RecEpid getEpid(String devuuid) {
		String sql = String.format("SELECT * FROM %s WHERE devuuid='%s'", RecEpid.TBL_NAME, devuuid);
		return (RecEpid) super.getOne(sql);
	}
	
	public boolean updateEpid(String devuuid, String epid) {
		String sql = String.format("UPDATE %s SET epid='%s' WHERE devuuid='%s'", RecEpid.TBL_NAME, epid, devuuid);
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