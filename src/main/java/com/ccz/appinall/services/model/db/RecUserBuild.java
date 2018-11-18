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

public class RecUserBuild extends DbRecord {
	
	static final String TBL_NAME = "userbuilding";
	public String userid;
	public String buildid;
	public double lon, lat;
	public Timestamp createdAt;
	
	public RecUserBuild(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "buildid VARCHAR(64) NOT NULL, lon DOUBLE DEFAULT 0, lat DOUBLE DEFAULT 0, createdAt DATETIME DEFAULT now())",  TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUserBuild rec = (RecUserBuild)r;
		rec.userid = rd.getString("userid");
		rec.buildid = rd.getString("buildid");
		rec.lon = rd.getDouble("lon");
		rec.lat = rd.getDouble("lat");
		rec.createdAt = rd.getDate("createdAt");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserBuild(super.poolName));
	}
	
	public DbRecord insertUserBuilding(String userid, String buildid, double lon, double lat) {
		return super.insert(qInsertUserBuilding(userid, buildid, lon, lat)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertUserBuilding(String userid, String buildid, double lon, double lat) {
		return String.format("INSERT INTO %s (userid, buildid, lon, lat) VALUES('%s', '%s', %f, %f)", TBL_NAME, userid, buildid, lon, lat);
	}

	public RecUserBuild getUserBuilding(String userid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s'" , TBL_NAME, userid);
		return (RecUserBuild) super.getOne(sql);
	}

}
