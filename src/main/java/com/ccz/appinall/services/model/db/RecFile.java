package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.Date;
import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

//@Entity
//@Data
//@Table( name="images")
public class RecFile extends DbRecord{
	static final String TBL_NAME = "upfile";
	
	public String fileid, userid, filename, thumbname;
	public String filetype, fileserver; //file server ip
	public int width, height, thumbwidth, thumbheight;
	public long filesize;
	public boolean enabled;
	public Timestamp regtime;
	
	public RecFile(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (fileid VARCHAR(64) NOT NULL PRIMARY KEY, userid VARCHAR(64) NOT NULL, "
				+ "filename VARCHAR(64) NOT NULL, thumbname VARCHAR(64), filetype VARCHAR(16), fileserver VARCHAR(64), "
				+ "width INTEGER DEFAULT 0, height INTEGER DEFAULT 0, thumbwidth INTEGER DEFAULT 0, thumbheight INTEGER DEFAULT 0, "
				+ "filesize LONG, enabled BOOLEAN DEFAULT false, "
				+ "regtime DATETIME DEFAULT now())", RecFile.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecFile rec = (RecFile)r;
		rec.fileid = rd.getString("fileid");
		rec.userid = rd.getString("userid");
		rec.filename = rd.getString("filename");
		rec.thumbname = rd.getString("thumbname");
		rec.filetype = rd.getString("filetype");
		rec.fileserver = rd.getString("fileserver");
		rec.width = rd.getInt("width");
		rec.height = rd.getInt("height");
		rec.thumbwidth = rd.getInt("thumbwidth");
		rec.thumbheight = rd.getInt("thumbheight");
		rec.filesize = rd.getLong("filesize");
		rec.enabled = rd.getBoolean("enabled");
		rec.regtime = rd.getDate("regtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecFile(poolName));
	}

	public DbRecord insertFileInit(String fileid, String userid, String filename, String filetype, long filesize) {
		this.fileid = fileid;
		this.userid = userid;
		this.filename = filename;
		this.filetype = filetype;
		this.filesize = filesize;
		String sql = String.format("INSERT INTO %s (fileid, userid, filename, filetype, filesize) VALUES('%s', '%s', '%s', '%s', %d)", 
									RecFile.TBL_NAME, fileid, userid, filename, filetype, filesize);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public DbRecord updateFileInfo(String fileid, int width, int height, long filesize, String fileserver) {
		this.fileid = fileid;
		this.width = width;
		this.height = height;
		this.filesize = filesize;
		this.fileserver = fileserver;
		String sql = String.format("UPDATE %s SET width=%d, height=%d, filesize=%d, fileserver='%s', enabled=true WHERE fileid='%s')",
								RecFile.TBL_NAME, width, height, filesize, fileserver, fileid);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean updateFileEnabled(String fileid, boolean enabled) {
		String sql = String.format("UPDATE %s SET enabled=%b WHERE fileid='%s'", RecFile.TBL_NAME, enabled, fileid);
		return super.update(sql);
	}

	public boolean updateThumbnail(String fileid, String thumbname, int thumbwidth, int thumbheight) {
		String sql = String.format("UPDATE %s SET thumbname='%s, thumbwidth=%d, thumbheight=%d WHERE fileid='%s'", 
					RecFile.TBL_NAME, thumbname, thumbwidth, thumbheight, fileid);
		return super.update(sql);
	}

	public boolean delete(String fileid) {
		String sql = String.format("DELETE FROM %s WHERE fileid='%s'", RecFile.TBL_NAME, fileid);
		return super.delete(sql);
	}

	public RecFile getFile(String fileid) {
		String sql = String.format("SELECT * FROM %s WHERE fileid='%s'", RecFile.TBL_NAME, fileid);
		return (RecFile) super.getOne(sql);
	}
	
}
