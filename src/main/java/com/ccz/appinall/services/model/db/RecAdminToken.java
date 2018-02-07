package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table( name="admintoken")
public class RecAdminToken extends DbRecord {
	static final String TBL_NAME = "admintoken";
	public String email, token;
	public Timestamp issuedate, lasttime;
	public String remoteip, reserved;

	public RecAdminToken(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (email VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "token VARCHAR(256) NOT NULL, issuedate DATETIME DEFAULT now(), lasttime DATETIME DEFAULT now(), "
				+ "remoteip VARCHAR(32) DEFAULT '', reserved VARCHAR(64))", RecAdminToken.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecAdminToken rec = (RecAdminToken)r;
		rec.email = rd.getString("email");
		rec.token = rd.getString("token");
		rec.issuedate = rd.getDate("issuedate");
		rec.lasttime = rd.getDate("lasttime");
		rec.remoteip = rd.getString("remoteip");
		rec.reserved = rd.getString("reserved");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecAdminToken(poolName));
	}
	
	public boolean upsert(String email, String token, String remoteip) {
		if(getToken(email)==null)
			return insert(email, token, remoteip);
		else
			return update(email, token, remoteip);
	}
	
	private boolean insert(String email, String token, String remoteip) {
		this.email = email;
		this.token = token;
		this.remoteip = remoteip;
		String sql = String.format("INSERT INTO %s (email, token, remoteip) "
								 + "VALUES('%s', '%s', '%s')", RecAdminToken.TBL_NAME, email, token, remoteip);
		return super.insert(sql);
	}
	private boolean update(String email, String token, String remoteip) {
		String sql = String.format("UPDATE %s SET token='%s', remoteip='%s', issuedate=now(), lasttime=now() WHERE email='%s'", 
									RecAdminToken.TBL_NAME, token, remoteip, email);
		return super.update(sql);
	}
	
	public boolean update(String email, String token) {
		String sql = String.format("UPDATE %s SET token='%s', issuedate=now(), lasttime=now() WHERE email='%s'", 
									RecAdminToken.TBL_NAME, token, email);
		return super.update(sql);
	}
	
	public boolean delete(String email) {
		String sql = String.format("DELETE FROM %s WHERE email='%s'", RecAdminToken.TBL_NAME, email);
		return super.delete(sql);
	}
	
	public RecAdminToken getToken(String email) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s'", RecAdminToken.TBL_NAME, email);
		return (RecAdminToken) super.getOne(sql);
	}
	
	public boolean updateLasttime(String email) {
		String sql = String.format("UPDATE %s SET lasttime=now() WHERE email='%s'", RecAdminToken.TBL_NAME, email);
		return super.update(sql);
	}

}