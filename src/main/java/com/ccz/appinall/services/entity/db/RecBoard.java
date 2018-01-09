package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table( name="board",
//		indexes = {	@Index(name = "idx_itemtype",  columnList="itemtype", unique = false),
//					@Index(name = "idx_appcode",  columnList="appcode", unique = false),
//					@Index(name = "idx_createuserid",  columnList="createuserid", unique = false)})

public class RecBoard  extends DbRecord {	//this data move to NoSQL like MongoDb.
	static final String TBL_NAME = "board";
	
	public String boardid, itemtype, title, content;
	public boolean hasimage, hasfile;
	public String category, aptcode, createuserid, createusername;
	public Timestamp createtime;
	
	public RecBoard(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (boardid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "itemtype VARCHAR(12) NOT NULL, title VARCHAR(128) NOT NULL, content VARCHAR(128) NOT NULL, hasimage BOOLEAN, hasfile BOOLEAN, "
				+ "category VARCHAR(24), aptcode VARCHAR(8), createuserid VARCHAR(64), createusername VARCHAR(32), "
				+ "createtime DATETIME DEFAULT now(), INDEX(itemtype, aptcode, createuserid))",  RecBoard.TBL_NAME);
		return super.createTable(sql); 
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoard rec = (RecBoard)r;
		rec.boardid = rd.getString("boardid");
		rec.itemtype = rd.getString("itemtype");
		rec.title = rd.getString("title");
		rec.content = rd.getString("content");
		rec.hasimage = rd.getBoolean("hasimage");
		rec.hasfile = rd.getBoolean("hasfile");
		rec.category = rd.getString("category");
		rec.aptcode = rd.getString("aptcode");
		rec.createuserid = rd.getString("createuserid");
		rec.createusername = rd.getString("createusername");
		rec.createtime = rd.getDate("createtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoard(poolName));
	}

	public boolean insert(String boardid, String itemtype, String title, String content, boolean hasimage, boolean hasfile, 
			String category, String aptcode, String createuserid, String createusername) {
		String sql = String.format("INSERT INTO %s (boardid, itemtype, title, content, hasimage, hasfile, category, "
				+ "aptcode, createuserid, createusername) "
				+ "VALUES('%s','%s','%s','%s',%b, %b,'%s','%s','%s','%s')", RecBoard.TBL_NAME, 
				boardid, itemtype, title, content, hasimage, hasfile, category, aptcode, createuserid, createusername);
		return super.insert(sql);
	}
	
	public boolean delete(String userid, String boardid) {
		String sql = String.format("DELETE %s, %s, %s FROM %s "
								 + "JOIN %s ON %s.boardid=%s.boardid "
								 + "JOIN %s ON %s.boardid=%s.boardid "
								 + "WHERE %s.boardid='%s' AND %s.createuserid='%s'", 
				RecBoard.TBL_NAME, RecBoardCount.TBL_NAME, RecBoardContent.TBL_NAME, RecBoard.TBL_NAME, 
				RecBoardCount.TBL_NAME, RecBoard.TBL_NAME, RecBoardCount.TBL_NAME,
				RecBoardContent.TBL_NAME, RecBoard.TBL_NAME, RecBoardContent.TBL_NAME,
				RecBoard.TBL_NAME, boardid, RecBoard.TBL_NAME, userid);
		return super.delete(sql);
	}
	
	public boolean updateTitle(String boardid, String userid, String title) {
		String sql = String.format("UPDATE %s SET title='%s' WHERE createuserid='%s' AND boardid='%s'", RecBoard.TBL_NAME, title, userid, boardid);
		return super.update(sql);
	}
	
	public boolean updateContent(String boardid, String userid, String content, boolean hasimage, boolean hasfile) {
		String sql = String.format("UPDATE %s SET content='%s', hasimage=%b, hasfile=%b WHERE createuserid='%s' AND boardid='%s'", 
				RecBoard.TBL_NAME, content, hasimage, hasfile, userid, boardid);
		return super.update(sql);
	}

	public boolean updateCategory(String boardid, String userid, String category) {
		String sql = String.format("UPDATE %s SET category='%s' WHERE createuserid='%s' AND boardid='%s'", RecBoard.TBL_NAME, category, userid, boardid);
		return super.update(sql);
	}

	public List<RecBoard> getList(String category, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE category='%s' ORDER BY createtime DESC LIMIT %d, %d", 
				RecBoard.TBL_NAME, category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoard)e).collect(Collectors.toList());
	}

	public List<RecBoard> getList(String userid, String category, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE createuserid='%s' AND category='%s' ORDER BY createtime DESC LIMIT %d, %d", 
				RecBoard.TBL_NAME, userid, category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoard)e).collect(Collectors.toList());
	}
	
	public List<RecBoard> getTypedList(String itemtype, String category, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE itemtype='%s' AND category='%s' ORDER BY createtime DESC LIMIT %d, %d", 
				RecBoard.TBL_NAME, itemtype, category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoard)e).collect(Collectors.toList());
	}

	public List<RecBoard> getTypedList(String userid, String itemtype, String category, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE createuserid='%s' AND itemtype='%s' AND category='%s' ORDER BY createtime DESC LIMIT %d, %d", 
				RecBoard.TBL_NAME, userid, itemtype, category, offset, count);
		return super.getList(sql).stream().map(e->(RecBoard)e).collect(Collectors.toList());
	}
}
/*@Id
	{
	@Column(length = 64, nullable = false)
	private String boardid;
	
	@Column(length = 12, nullable = false)
	private String itemtype;
	
	@Column(length = 128, nullable = false)
	private String  title;
	
	@Column(length = 128, nullable = false)
	private String content;
	
	@Column
	private boolean hasimage = false;
	
	@Column
	private boolean hasfile = false;
	
	@Column(length = 12)
	private String category;
	
	@Column(length = 8)
	private String appcode;
	
	@Column(length = 64)
	private String createuserid;
	
	@Column(length = 32)
	private String createusername;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createtime;
	
	public RecBoard() {	}
	
	public RecBoard(String boardid, String itemtype, String title, String content, boolean hasimage, boolean hasfile, 
			String category, String appcode, String createuserid, String createusername) {
		this.boardid = boardid;
		this.itemtype = itemtype;
		this.title = title;
		this.content = content;
		this.hasimage = hasimage;
		this.hasfile = hasfile;
		this.category = category;
		this.appcode = appcode;
		this.createuserid = createuserid;
		this.createusername = createusername;
	}
	
	public void updateTitle(String title) {
		this.title = title;
	}
	
	public void updateContent(String content, boolean hasimage, boolean hasfile ) {
		this.content = content;
		this.hasimage = hasimage;
		this.hasfile = hasfile;
	}
	
	public void updateCategory(String category) {
		this.category = category;
	}
}
*/