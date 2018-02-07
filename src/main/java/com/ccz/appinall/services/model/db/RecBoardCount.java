package com.ccz.appinall.services.model.db;

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
//@Table(name="boardcount")
public class RecBoardCount extends DbRecord{
	public static final String TBL_NAME = "boardcount";
	
	public String boardid;
	public int likes, dislikes, visit, reply;
	public Timestamp lastmodify;
	
	public RecBoardCount(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (boardid VARCHAR(64) NOT NULL PRIMARY KEY,"
				+ "likes INT DEFAULT 0, dislikes INT DEFAULT 0, visit INT DEFAULT 0, reply INT DEFAULT 0, lastmodify DATETIME DEFAULT now(), INDEX(likes, visit))", 
				RecBoardCount.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardCount rec = (RecBoardCount)r;
		rec.boardid = rd.getString("boardid");
		rec.likes = rd.getInt("likes");
		rec.dislikes = rd.getInt("dislikes");
		rec.visit = rd.getInt("visit");
		rec.reply = rd.getInt("reply");
		rec.lastmodify = rd.getDate("lastmodify");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardCount(poolName));
	}

	public boolean insert(String boardid) {
		String sql = String.format("INSERT INTO %s (boardid) "
				 + "VALUES('%s')", RecBoardCount.TBL_NAME, boardid);
		return super.insert(sql);
	}
	
	public boolean delete() {
		return false; //delete from TblBoard using join
	}
	
	public boolean incLike(String boardid, boolean bInc) {
		String sql = String.format("UPDATE %s SET likes=likes+%d WHERE boardid='%s'", RecBoardCount.TBL_NAME, bInc? 1:-1, boardid);
		return super.update(sql);
	}
	
	public boolean incDislike(String boardid, boolean bInc) {
		String sql = String.format("UPDATE %s SET dislikes=dislikes+%d WHERE boardid='%s'", RecBoardCount.TBL_NAME, bInc? 1:-1, boardid);
		return super.update(sql);
	}
	
	public boolean incVisit(String boardid) {
		String sql = String.format("UPDATE %s SET visit=visit+1 WHERE boardid='%s'", RecBoardCount.TBL_NAME, boardid);
		return super.update(sql);
	}
	
	public boolean incReply(String boardid) {
		String sql = String.format("UPDATE %s SET reply=reply+1 WHERE boardid='%s'", RecBoardCount.TBL_NAME, boardid);
		return super.update(sql);
	}
	
}

/*{
	@Id
	@Column(length = 64, nullable = false)
	public String boardid;
	
	@Column
	public int likes = 0;
	
	@Column
	public int dislikes = 0;
	
	@Column
	public int visit = 0;
	
	@Column
	public int  reply = 0;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true)
	public Date lastmodify;
	
	public RecBoardCount() {	}
	
	public RecBoardCount(String boardid) {
		this.boardid = boardid;
	}
}
*/