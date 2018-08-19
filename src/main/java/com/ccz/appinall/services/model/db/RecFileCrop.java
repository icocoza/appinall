package com.ccz.appinall.services.model.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecFileCrop  extends DbRecord{
	static final String TBL_NAME = "filecrop";

	public String boardid;
	public String serverip;
	public String subpath;
	public String filename;

	public RecFileCrop(String poolName) {
		super(poolName);
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (boardid VARCHAR(64) NOT NULL PRIMARY KEY, serverip VARCHAR(16) NOT NULL, "
				+ "subpath VARCHAR(64) NOT NULL, filename VARCHAR(64) NOT NULL)", RecFileCrop.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecFileCrop rec = (RecFileCrop)r;
		rec.boardid = rd.getString("boardid");
		rec.filename = rd.getString("filename");
		rec.serverip = rd.getString("serverip");
		rec.subpath = rd.getString("subpath");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecFileCrop(poolName));
	}
	
	public DbRecord insertCropFile(String boardid, String serverip, String subpath, String filename) {
		this.boardid = boardid;
		this.serverip = serverip;
		this.subpath = subpath;
		this.filename = filename;
		String sql = String.format("INSERT INTO %s (boardid, serverip, subpath, filename) VALUES('%s', '%s', '%s', '%s')", 
									RecFileCrop.TBL_NAME, boardid, serverip, subpath, filename);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public RecFileCrop getFile(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s'", RecFileCrop.TBL_NAME, boardid);
		return (RecFileCrop) super.getOne(sql);
	}
}
