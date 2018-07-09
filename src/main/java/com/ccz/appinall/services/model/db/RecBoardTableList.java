package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EBoardService;

public class RecBoardTableList extends DbRecord {
	static final String TBL_NAME = "boardtablelist";
	
	public String tableid, title;
	public String boardtype;
	public EBoardService servicetype;
	public String sido, sigu, dong;
	public Timestamp createdAt;
 
	public RecBoardTableList(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (tableid VARCHAR(64) NOT NULL PRIMARY KEY, title VARCHAR(64) NOT NULL, "
				+ "boardtype VARCHAR(24), servicetype VARCHAR(24), sido VARCHAR(32), sigu VARCHAR(32), dong VARCHAR(32), createdAt DATETIME DEFAULT now(), "
				+ "INDEX idx_title(title), INDEX idx_sido(sido), INDEX idx_sigu(sigu), INDEX idx_dong(dong))",  TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardTableList rec = (RecBoardTableList)r;
		rec.tableid = rd.getString("tableid");
		rec.title = rd.getString("title");
		rec.boardtype = rd.getString("boardtype");
		rec.servicetype = EBoardService.getType(rd.getString("servicetype"));
		rec.sido = rd.getString("sido");
		rec.sigu = rd.getString("sigu");
		rec.dong = rd.getString("dong");
		rec.createdAt = rd.getDate("createdAt");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardTableList(super.poolName));
	}
	
	public DbRecord insertTable(String tableid, String title, String boardtype, String servicetype, String sido, String sigu, String dong) {
		this.tableid = tableid;
		this.title = title;
		this.boardtype = boardtype;
		this.serviceCode = servicetype;
		this.sido = sido;
		this.sigu = sigu;
		this.dong = dong;
		return super.insert(qInsertTable(tableid, title, boardtype, servicetype, sido, sigu, dong)) ? this : DbRecord.Empty;
	}
	
	static public String qInsertTable(String tableid, String title, String boardtype, String servicetype, String sido, String sigu, String dong) {
		return String.format("INSERT INTO %s (tableid, title, boardtype, servicetype, sido, sigu, dong) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
									TBL_NAME, tableid, title, boardtype, servicetype, sido, sigu, dong);
	}

	public RecBoardTableList getTableByTitle(String title, String sido, String sigu, String dong) {
		String sql = String.format("SELECT tableid, title, boardtype, servicetype, sigu, dong FROM %s WHERE title='%s' AND  sido='%s' AND sigu='%s' AND dong='%s'" , TBL_NAME, title, sido, sigu, dong);
		return (RecBoardTableList) super.getOne(sql);
	}
}
