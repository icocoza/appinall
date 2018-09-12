package com.ccz.appinall.services.model.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

public class RecScrapBody  extends DbRecord {
	@JsonIgnore static final String TBL_NAME = "scrapbody";
	
	@Getter private String scrapid;
	@Getter private String body;
	
	public RecScrapBody(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}
	
	public RecScrapBody(DbReader rd) {
		super("");
		doLoad(rd, this);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (scrapid VARCHAR(64) NOT NULL PRIMARY KEY, body VARCHAR(512)) ", RecScrapBody.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecScrapBody rec = (RecScrapBody)r;
		rec.scrapid = rd.getString("scrapid");
		rec.body = rd.getString("body");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecScrapBody(poolName));
	}
	
	public boolean insertScrapBody(String scrapid,  String body) {
		String sql = qInsertScrapBody(scrapid, body);
		return super.insert(sql);
	}

	public static String qInsertScrapBody(String scrapid, String body) {
		return String.format("INSERT INTO %s (scrapid, body) "
				+ "VALUES('%s', '%s')", RecScrapBody.TBL_NAME, scrapid, body);
	}
	
	public RecScrapBody getScrapBody(String scrapid) {
		String sql = String.format("SELECT * FROM %s WHERE scrapid='%s'", RecScrapBody.TBL_NAME, scrapid);
		return (RecScrapBody) super.getOne(sql);
	}
		
	public boolean updateScrapBody(String scrapid, String body) {
		String sql = String.format("UPDATE %s SET body='%s' WHERE scrapid='%s'", RecScrapBody.TBL_NAME, body, scrapid);
		return super.update(sql);
	}

}
