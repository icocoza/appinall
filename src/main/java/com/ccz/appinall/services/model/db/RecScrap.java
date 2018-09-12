package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

public class RecScrap  extends DbRecord {
	@JsonIgnore static final String TBL_NAME = "scrap";
	
	@Getter private String scrapid;
	@JsonIgnore @Getter private String url;
	@Getter private String scraptitle, subtitle;
	@JsonIgnore @Getter private String scrapip;
	@JsonIgnore @Getter private String scrappath;
	@Getter private String scrapimg;
	
	public RecScrap(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}
	
	public RecScrap(DbReader rd) {
		super("");
		doLoad(rd, this);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "scrapid VARCHAR(64) NOT NULL PRIMARY KEY, url VARCHAR(256) NOT NULL, "
				+ "scraptitle VARCHAR(64) NOT NULL, subtitle VARCHAR(64) NOT NULL, "
				+ "scrapip VARCHAR(16), scrappath VARCHAR(32), createdAt DATETIME DEFAULT NOW(), "
				+ "INDEX idx_url(url) )", RecScrap.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecScrap rec = (RecScrap)r;
		rec.scrapid = rd.getString("scrapid");
		rec.url = rd.getString("url");
		rec.scraptitle = rd.getString("scraptitle");
		rec.subtitle = rd.getString("subtitle");
		rec.scrapip = rd.getString("scrapip");
		rec.scrappath = rd.getString("scrappath");
		rec.scrapimg = String.format("http://%s:8080/scrap?scrapid=%s", rec.scrapip, rec.scrapid);
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecScrap(poolName));
	}
	
	public boolean insertScrap(String scrapid, String url, String scraptitle, String subtitle) {
		String sql = qInsertScrap(scrapid, url, scraptitle, subtitle);
		return super.insert(sql);
	}

	public static String qInsertScrap(String scrapid, String url, String scraptitle, String subtitle) {
		return String.format("INSERT INTO %s (scrapid, url, scraptitle, subtitle) "
				+ "VALUES('%s', '%s', '%s', '%s')", RecScrap.TBL_NAME, 
				scrapid, url, scraptitle, subtitle);
	}
	
	public RecScrap getScrap(String scrapid) {
		String sql = String.format("SELECT * FROM %s WHERE scrapid='%s'", RecScrap.TBL_NAME, scrapid);
		return (RecScrap) super.getOne(sql);
	}
	
	public List<RecScrap> getScrapList(List<String> scrapids) {
		String inClause = scrapids.stream().map(x->"'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT * FROM %s WHERE scrapid IN (%s)", RecScrap.TBL_NAME, inClause);
		return super.getList(sql).stream().map(e->(RecScrap)e).collect(Collectors.toList());
	}

	public List<RecScrap> getScrapListByUrl(List<String> urls) {
		String inClause = urls.stream().map(x->"'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT * FROM %s WHERE url IN (%s)", RecScrap.TBL_NAME, inClause);
		return super.getList(sql).stream().map(e->(RecScrap)e).collect(Collectors.toList());
	}
	
	public boolean updateScrap(String scrapid, String scrapip, String scrappath) {
		String sql = String.format("UPDATE %s SET scrapip='%s', scrappath='%s' WHERE scrapid='%s'", RecScrap.TBL_NAME, scrapip, scrappath, scrapid);
		return super.update(sql);
	}

}
