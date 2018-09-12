package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table(name="boardcontent")
public class RecBoardContent extends DbRecord {
	static final String TBL_NAME = "boardcontent";
	public static final int MAX_CONTENT_SIZE = 2048;
	public String boardid, content;
	
	public RecBoardContent(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "boardid VARCHAR(64) NOT NULL PRIMARY KEY, content VARCHAR(%d) NOT NULL)", RecBoardContent.TBL_NAME, RecBoardContent.MAX_CONTENT_SIZE);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardContent rec = (RecBoardContent)r;
		rec.boardid = rd.getString("boardid");
		rec.content = rd.getString("content");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardContent(poolName));
	}
	
	public boolean insert(String boardid, String content) {
		String sql = String.format("INSERT INTO %s (boardid, content) VALUES('%s', '%s')", RecBoardContent.TBL_NAME, boardid, content);
		return super.insert(sql);
	}
	
	public boolean delete(String boardid) {
		return false; //delete from TblBoard using join
	}
	
	public boolean updateContent(String boardid, String content) {
		String sql = String.format("UPDATE %s SET content='%s' WHERE boardid='%s'", RecBoardContent.TBL_NAME, content, boardid);
		return super.update(sql);
	}
	
	public String getContent(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s'", RecBoardContent.TBL_NAME, boardid);
		RecBoardContent content = (RecBoardContent) super.getOne(sql);
		return content != null? content.content : null;
	}

}
