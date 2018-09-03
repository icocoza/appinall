package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Getter;

public class RecBoardScrap extends DbRecord {

	static final String TBL_NAME = "boardscrap";
	
	@Getter private String boardid, scrapid;
	
	public RecBoardScrap(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "scrapid VARCHAR(64) NOT NULL PRIMARY KEY, boardid VARCHAR(64) NOT NULL, "
				+ "createdAt DATETIME DEFAULT NOW(), INDEX idx_boardid(boardid))", RecBoardScrap.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardScrap rec = (RecBoardScrap)r;
		rec.boardid = rd.getString("boardid");
		rec.scrapid = rd.getString("scrapid");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardScrap(poolName));
	}
	
	public boolean insertScrap(String boardid, String scrapid) {
		String sql = qInsertScrap(boardid, scrapid);
		return super.insert(sql);
	}

	public static String qInsertScrap(String boardid, String scrapid) {
		return String.format("INSERT INTO %s (boardid, scrapid) VALUES('%s', '%s')", 
				RecBoardScrap.TBL_NAME, boardid,	scrapid);
	}
	
	public RecBoardScrap getScrapId(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s'", RecBoardScrap.TBL_NAME, boardid);
		return (RecBoardScrap) super.getOne(sql);
	}
	
	public List<RecBoardScrap> getScrapIdList(String boardid) {
		String sql = String.format("SELECT * FROM %S WHERE boardid='%s'", RecBoardScrap.TBL_NAME, boardid);
		return super.getList(sql).stream().map(e->(RecBoardScrap)e).collect(Collectors.toList());
	}

}
