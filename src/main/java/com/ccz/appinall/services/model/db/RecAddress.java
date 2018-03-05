package com.ccz.appinall.services.model.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecAddress extends DbRecord {
	static final String TBL_NAME = "zipaddress";
	
	public String buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname;
	public int buildno, buildsubno, jino, jisubno;
	public double lon, lat;
	
	public RecAddress(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "buildid VARCHAR(32) NOT NULL PRIMARY KEY, zip VARCHAR(6) NOT NULL, sido VARCHAR(16) NOT NULL, sigu VARCHAR(16) NOT NULL, "
				+ "eub VARCHAR(16) NOT NULL, roadname VARCHAR(32) NOT NULL, delivery VARCHAR(32) NOT NULL, "
				+ "buildname VARCHAR(32) NOT NULL, dongname VARCHAR(12) NOT NULL, liname VARCHAR(8) NOT NULL, hjdongname VARCHAR(16) NOT NULL, "
				+ "buildno INTEGER DEFAULT 0, buildsubno INTEGER DEFAULT 0, jino INTEGER DEFAULT 0, jisubno INTEGER DEFAULT 0,"
				+ "lon DOUBLE, lat DOUBLE)", RecAddress.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecAddress rec = (RecAddress)r;
		rec.buildid = rd.getString("buildid");
		rec.zip = rd.getString("zip");
		rec.sido = rd.getString("sido");
		rec.sigu = rd.getString("sigu");
		rec.eub = rd.getString("eub");
		rec.roadname = rd.getString("roadname");
		rec.delivery = rd.getString("delivery");
		rec.buildname = rd.getString("buildname");
		rec.dongname = rd.getString("dongname");
		rec.liname = rd.getString("liname");
		rec.hjdongname = rd.getString("hjdongname");
		rec.buildno = rd.getInt("buildno");
		rec.buildsubno = rd.getInt("buildsubno");
		rec.jino = rd.getInt("jino");
		rec.jisubno = rd.getInt("jisubno");
		rec.lon = rd.getDouble("lon");
		rec.lat = rd.getDouble("lat");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecAddress(poolName));
	}

	public boolean insertAddress(String buildid, String zip, String sido, String sigu, String eub, String roadname, String delivery, 
						 String buildname, String dongname, String liname, String hjdongname,
						 int buildno, int buildsubno, int jino, int jisubno, double lon, double lat) {
		String sql = qInsertAddress(buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname, buildno, buildsubno, jino, jisubno, lon, lat);
		return super.insert(sql);
	}

	static public String qInsertAddress(String buildid, String zip, String sido, String sigu, String eub, String roadname, String delivery, 
			 String buildname, String dongname, String liname, String hjdongname,
			 int buildno, int buildsubno, int jino, int jisubno, double lon, double lat) {
		return String.format("INSERT INTO %s (buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname, "
			+ "buildno, buildsubno, jino, jisubno, lon, lat) "
			+ "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',%d,%d,%d,%d,%f,%f)", RecAddress.TBL_NAME, 
			buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname,
			buildno, buildsubno, jino, jisubno, lon, lat);
	}

	public RecAddress getAddress(String buildid) {
		String sql = String.format("select * from %s where buildid='%s'", RecAddress.TBL_NAME, buildid);
		return (RecAddress) super.getOne(sql);
	}
	
}
