package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

//@Entity
//@Data
//@Table( name="images")
public class RecFile extends DbRecord{
	static final String TBL_NAME = "upfile";
	
	public String fileid;
	@JsonIgnore public String userid;
	@JsonIgnore public String filename, thumbname;
	@JsonIgnore public String boardid;
	public String filetype, fileserver; //file server ip
	@JsonIgnore public int width, height, thumbwidth, thumbheight;
	public long filesize;
	@JsonIgnore public boolean uploaded, enabled, deleted;
	public String comment;
	@JsonIgnore public Timestamp regtime, deletedAt;
	
	public RecFile(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (fileid VARCHAR(64) NOT NULL PRIMARY KEY, userid VARCHAR(64) NOT NULL, "
				+ "filename VARCHAR(64) NOT NULL, thumbname VARCHAR(64), boardid VARCHAR(64), filetype VARCHAR(16), fileserver VARCHAR(64), "
				+ "width INTEGER DEFAULT 0, height INTEGER DEFAULT 0, thumbwidth INTEGER DEFAULT 0, thumbheight INTEGER DEFAULT 0, "
				+ "filesize LONG, uploaded BOOLEAN DEFAULT false, enabled BOOLEAN DEFAULT false, comment VARCHAR(256), "
				+ "regtime DATETIME DEFAULT now(), deleted BOOLEAN DEFAULT false, deletedAt DATETIME, INDEX idx_boardid(boardid))", RecFile.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecFile rec = (RecFile)r;
		rec.fileid = rd.getString("fileid");
		rec.userid = rd.getString("userid");
		rec.filename = rd.getString("filename");
		rec.thumbname = rd.getString("thumbname");
		rec.boardid = rd.getString("boardid");
		rec.filetype = rd.getString("filetype");
		rec.fileserver = rd.getString("fileserver");
		rec.width = rd.getInt("width");
		rec.height = rd.getInt("height");
		rec.thumbwidth = rd.getInt("thumbwidth");
		rec.thumbheight = rd.getInt("thumbheight");
		rec.filesize = rd.getLong("filesize");
		rec.uploaded = rd.getBoolean("uploaded");
		rec.enabled = rd.getBoolean("enabled");
		rec.deleted = rd.getBoolean("deleted");
		rec.comment = rd.getString("comment");
		rec.regtime = rd.getDate("regtime");
		rec.deletedAt = rd.getDate("deletedAt");
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

	public DbRecord insertFileInit(String fileid, String userid, String fileserver, String filename, String filetype, long filesize, String comment) {
		this.fileid = fileid;
		this.userid = userid;
		this.fileserver = fileserver;
		this.filename = filename;
		this.filetype = filetype;
		this.filesize = filesize;
		this.comment = comment;
		String sql = String.format("INSERT INTO %s (fileid, userid, fileserver, filename, filetype, filesize, comment) VALUES('%s', '%s', '%s', '%s', '%s', %d, '%s')", 
									RecFile.TBL_NAME, fileid, userid, fileserver, filename, filetype, filesize, comment);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public DbRecord updateFileInfo(String fileid, int width, int height, long filesize) {
		this.fileid = fileid;
		this.width = width;
		this.height = height;
		this.filesize = filesize;
		
		String sql = String.format("UPDATE %s SET width=%d, height=%d, filesize=%d, uploaded=true WHERE fileid='%s'",
								RecFile.TBL_NAME, width, height, filesize, fileid);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean updateFileEnabled(String fileid, String boardid, boolean enabled) {
		String sql = String.format("UPDATE %s SET boardid='%s', enabled=%b, deleted=false, deletedAt=null WHERE fileid='%s' AND uploaded=true", RecFile.TBL_NAME, boardid, enabled, fileid);
		return super.update(sql);
	}
	public boolean updateFilesEnabled(List<String> fileids, String boardid, boolean enabled) {
		String filestr = fileids.stream().map(x -> "'"+x+"'").collect(Collectors.joining(","));
		String sql = String.format("UPDATE %s SET boardid='%s', enabled=%b, deleted=false, deletedAt=null WHERE fileid IN(%s) AND uploaded=true", RecFile.TBL_NAME, boardid, enabled, filestr);
		return super.update(sql);
	}

	public boolean updateThumbnail(String fileid, String thumbname, int thumbwidth, int thumbheight) {
		String sql = String.format("UPDATE %s SET thumbname='%s', thumbwidth=%d, thumbheight=%d WHERE fileid='%s'", 
					RecFile.TBL_NAME, thumbname, thumbwidth, thumbheight, fileid);
		return super.update(sql);
	}

	public boolean updateDeleteFile(String boardid) {
		String sql = String.format("UPDATE %s SET deleted=true, deletedAt=NOW() WHERE boardid='%s'", RecFile.TBL_NAME, boardid);
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
	
	public List<RecFile> getFileList(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s' AND deleted=false", RecFile.TBL_NAME, boardid);
		return super.getList(sql).stream().map(e -> (RecFile)e).collect(Collectors.toList());
	}
}
