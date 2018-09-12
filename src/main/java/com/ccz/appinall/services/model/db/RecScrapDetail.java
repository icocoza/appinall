package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

public class RecScrapDetail  extends DbRecord {
	
	@Getter private String scrapid;
	@Getter private String url;
	@Getter private String scraptitle, subtitle;
	@JsonIgnore @Getter private String scrapip;
	@JsonIgnore @Getter private String scrappath;
	@Getter private String scrapimg;
	@Getter private String body;
	
	public RecScrapDetail(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}
	
	public RecScrapDetail(DbReader rd) {
		super("");
		doLoad(rd, this);
	}

	@Override
	public boolean createTable() {
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecScrapDetail rec = (RecScrapDetail)r;
		rec.scrapid = rd.getString("scrapid");
		rec.url = rd.getString("url");
		rec.scraptitle = rd.getString("scraptitle");
		rec.subtitle = rd.getString("subtitle");
		rec.scrapip = rd.getString("scrapip");
		rec.scrappath = rd.getString("scrappath");
		rec.scrapimg = String.format("http://%s:8080/scrap?scrapid=%s", rec.scrapip, rec.scrapid);
		rec.body = rd.getString("body");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecScrapDetail(poolName));
	}
	
	public List<RecScrapDetail> getScrapDetailList(String boardid) {
		String sql = String.format("SELECT * FROM scrap "
				+ "LEFT JOIN scrapbody ON scrap.scrapid = scrapbody.scrapid "
				+ "LEFT JOIN boardscrap ON scrap.scrapid = boardscrap.scrapid "
				+ "WHERE boardscrap.boardid='%s'", boardid);
		return super.getList(sql).stream().map(e->(RecScrapDetail)e).collect(Collectors.toList());
	}

}
