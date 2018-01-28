package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table(name="webscrab")
public class RecWebScrab extends DbRecord {
	static final String TBL_NAME = "webscrab";
	
	public String webid, url, title, scrabpath;
	public int width, height, count;
	public Timestamp regtime;
	
	public RecWebScrab(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (webid VARCHAR(64) NOT NULL PRIMARY KEY,"
				+ "url VARCHAR(256) NOT NULL, title VARCHAR(128) DEFAULT '', scrabpath VARCHAR(128) NOT NULL, "
				+ "width INTEGER DEFAULT 0, height INTEGER DEFAULT 0, count INTEGER DEFAULT 0, "
				+ "regtime DATETIME DEFAULT  now())", RecWebScrab.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecWebScrab rec = (RecWebScrab)r;
		rec.webid = rd.getString("webid");
		rec.url = rd.getString("url");
		rec.title = rd.getString("title");
		rec.scrabpath = rd.getString("scrabpath");
		rec.width = rd.getInt("width");
		rec.height = rd.getInt("height");
		rec.count = rd.getInt("count");
		rec.regtime = rd.getDate("regtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecWebScrab(poolName));
	}

	public DbRecord insert(String webid, String url, String title, String scrabpath, int width, int height) {
		this.webid = webid;
		this.url = url;
		this.title = title;
		this.scrabpath = scrabpath;
		this.width = width;
		this.height = height;
		String sql = String.format("INSERT INTO %s (webid, url, title, scrabpath, width, height) "
								+  "VALUES('%s', '%s', '%s', '%s', %d, %d)", RecWebScrab.TBL_NAME, 
								webid, url, title, scrabpath, width, height);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String imgid) {
		String sql = String.format("DELETE FROM %s WHERE webid='%s'", RecWebScrab.TBL_NAME, webid);
		return super.delete(sql);
	}

	public RecWebScrab getScrab(String imgid) {
		String sql = String.format("SELECT * FROM %s WHERE webid='%s'", RecWebScrab.TBL_NAME, webid);
		return (RecWebScrab) super.getOne(sql);
	}
	
	public boolean inc(String imgid) {
		String sql = String.format("UPDATE %s SET count=count+1 WHERE imgid='%s'", RecWebScrab.TBL_NAME, imgid);
		return super.update(sql);
	}

}
/* {
	@Id
	@Column(length = 64, nullable = false)
	public String webid;
	
	@Column(length = 256, nullable = false)
	public String url;
	
	@Column(length = 64, nullable = false)
	public String title;
	
	@Column(length = 128, nullable = false)
	public String scrabpath;
	
	@Column
	public Integer width;
	
	@Column
	public Integer height;
	
	@Column
	public Integer count;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date regtime;
	
	public RecWebScrab()	{	}
	
	public RecWebScrab(String webid, String url, String title, String scrabpath, int width, int height)	{	
		this.webid = webid;
		this.url = url;
		this.title = title;
		this.scrabpath = scrabpath;
		this.width = width;
		this.height = height;
	}
}
*/