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
//@Table( name="images")
public class RecImage extends DbRecord{
	static final String TBL_NAME = "images";
	
	public String imgid, imgname, thumbname;
	public long width, height, thumbwidth, thumbheight;
	public Timestamp regtime;
	
	public RecImage(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (imgid VARCHAR(64) NOT NULL PRIMARY KEY,"
				+ "imgname VARCHAR(256) NOT NULL, thumbname VARCHAR(256) NOT NULL, "
				+ "width LONG DEFAULT 0, height LONG DEFAULT 0, thumbwidth LONG DEFAULT 0, thumbheight LONG DEFAULT 0,"
				+ "regtime DATETIME DEFAULT  now())", RecImage.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecImage rec = (RecImage)r;
		rec.imgid = rd.getString("imgid");
		rec.imgname = rd.getString("imgname");
		rec.thumbname = rd.getString("thumbname");
		rec.width = rd.getLong("width");
		rec.height = rd.getLong("height");
		rec.thumbwidth = rd.getLong("thumbwidth");
		rec.thumbheight = rd.getLong("thumbheight");
		rec.regtime = rd.getDate("regtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecImage(poolName));
	}

	public DbRecord insert(String imgid, String imgname, String thumbname, long width, long height, long thumbwidth, long thumbheight) {
		this.imgid = imgid;
		this.imgname = imgname;
		this.thumbname = thumbname;
		this.width = width;
		this.height = height;
		this.thumbwidth = thumbwidth;
		this.thumbheight = thumbheight;
		String sql = String.format("INSERT INTO %s (imgid, imgname, thumbname, width, height, thumbwidth, thumbheight) "
								+  "VALUES('%s', '%s', '%s', %d, %d, %d, %d)", RecImage.TBL_NAME, 
								imgid, imgname, thumbname, width, height, thumbwidth, thumbheight);
		return super.insert(sql) ? this : DbRecord.Empty;
	}
	
	public boolean delete(String imgid) {
		String sql = String.format("DELETE FROM %s WHERE imgid='%s'", RecImage.TBL_NAME, imgid);
		return super.delete(sql);
	}

	public RecImage getImage(String imgid) {
		String sql = String.format("SELECT * FROM %s WHERE imgid='%s'", RecImage.TBL_NAME, imgid);
		return (RecImage) super.getOne(sql);
	}
	
}
/*{
	@Id
	@Column(length = 64, nullable = false)
	private String imgid;
	
	@Column(length = 256, nullable = false)
	private String imgurl;
	
	@Column(length = 256, nullable = false)
	private String thumburl;
	
	@Column
	private Long width = 0L;
	
	@Column
	private Long height = 0L;
	
	@Column
	private Long thumbwidth = 0L;
	
	@Column
	private Long thumbheight = 0L;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date regtime;
	
	public RecImage()	{	}
	
	public RecImage(String imgid, String imgurl, String thumburl, long width, long height, long thumbwidth, long thumbheight)	{	
		this.imgid = imgid;
		this.imgurl = imgurl;
		this.thumburl = thumburl;
		this.width = width;
		this.height = height;
		this.thumbwidth = thumbwidth;
		this.thumbheight = thumbheight;
	}
}
*/