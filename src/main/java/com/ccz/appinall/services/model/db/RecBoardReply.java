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
//@Table( name="boardreply",
//		indexes = {	@Index(name = "idx_boardid",  columnList="boardid", unique = false)})
public class RecBoardReply extends DbRecord {
	public static final String TBL_NAME = "boardreply";
	
	public long replyid, parentid;
	@JsonIgnore public String boardid;
	public String userid, username;
	public short depth;
	public String msg;
	public Timestamp replytime;
	
	public RecBoardReply(String poolName) {
		super(poolName);
	}

	public RecBoardReply(String poolName, String replyid) {
		super(poolName);
		this.replyid = Long.parseLong(replyid);
	}
	
	public RecBoardReply(String poolName, long replyid) {
		super(poolName);
		this.replyid = replyid;
	}
	
	public String getReplyId() {
		return replyid+"";
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (replyid INT NOT NULL AUTO_INCREMENT, parentid INT DEFAULT 0, "
				+ "boardid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL, username VARCHAR(32) NOT NULL, "
				+ "depth SMALLINT NOT NULL, msg VARCHAR(512) NOT NULL, replytime DATETIME DEFAULT now(), PRIMARY KEY(replyid), INDEX(boardid))", 
				RecBoardReply.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardReply rec = (RecBoardReply)r;
		rec.replyid = rd.getLong("replyid");
		rec.parentid = rd.getLong("parentid");
		rec.boardid = rd.getString("boardid");
		rec.userid = rd.getString("userid");
		rec.username = rd.getString("username");
		rec.depth = rd.getShort("depth");
		rec.msg = rd.getString("msg");
		rec.replytime = rd.getDate("replytime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardReply(poolName));
	}

	public int insert(String boardid, String parentid, String userid, String username, short depth, String msg) {
		String sql = String.format("INSERT INTO %s (parentid, boardid, userid, username, depth, msg) "
				 + "VALUES(%d, '%s', '%s', '%s', %d, '%s')", RecBoardReply.TBL_NAME,   
				 Long.parseLong(parentid), boardid, userid, username, depth, msg);
		return super.insertAndGetKey(sql);
	}
	
	public boolean delete() {
		String sql = String.format("DELETE FROM %s WHERE replyid=%d", RecBoardReply.TBL_NAME, replyid);
		return super.delete(sql);
	}

	public boolean deleteIfNoChild(String boardid, String userid) {
		String sql = String.format("SELECT replyid FROM %s WHERE parentid=%d", RecBoardReply.TBL_NAME, replyid);
		if(super.exist(sql)==false) {	//delete a record if not exist child, else update messge(means delete only message)
			sql = String.format("DELETE FROM %s WHERE boardid='%s' AND replyid='%s' AND userid='%s'", RecBoardReply.TBL_NAME, boardid, replyid, userid);
			return super.delete(sql);
		}
		return updateMsg(userid, "-- deleted by user --");
	}

	public boolean updateMsg(String userid, String msg) {
		String sql = String.format("UPDATE %s SET msg='%s' WHERE replyid=%d AND userid='%s'", RecBoardReply.TBL_NAME, msg, replyid, userid);
		return super.update(sql);
	}
	
	public List<RecBoardReply> getList(String boardid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s' ORDER BY replytime LIMIT %d, %d", 
				RecBoardReply.TBL_NAME, boardid, offset, count);
		return super.getList(sql).stream().map(e->(RecBoardReply)e).collect(Collectors.toList());
	}
	
}
/* {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long replyid;
	
	@Column
	private Long parentid = 0L;
	
	@Column(length = 64, nullable = false)
	private String boardid;
	
	@Column(length = 64, nullable = false)
	private String userid;
	
	@Column(length = 32, nullable = false)
	private String  username;
	
	@Column(nullable = false)
	private short depth;
	
	@Column(length = 512, nullable = false)
	private String msg;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date replytime;
	
	public RecBoardReply() {		}
	
	public RecBoardReply(String boardid, Long parentid, String userid, String username, short depth, String msg) {
		this.parentid = parentid;
		this.boardid =boardid;
		this.userid =userid;
		this.username =username;
		this.depth = depth;
		this.msg =msg;
	}
	
	public void updateMsg(String msg) {
		this.msg = msg;
	}
}
*/