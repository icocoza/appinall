package com.ccz.appinall.services.model.db;

import java.io.Serializable;
import java.util.Date;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table( name="voteuser")
public class RecVoteUser extends DbRecord {
	static final String TBL_NAME = "voteuser";
	
	public String boardid, userid; //multi key
	public String vitemid;	//selected vote id
	public long selecttime;
	
	public RecVoteUser(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "boardid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL, vitemid VARCHAR(64) NOT NULL,"
				+ "selecttime LONG NOT NULL, PRIMARY KEY (boardid, userid)) ", RecVoteUser.TBL_NAME);
		
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecVoteUser rec = (RecVoteUser)r;
		
		rec.boardid = rd.getString("boardid");
		rec.userid = rd.getString("userid");
		rec.vitemid = rd.getString("replyid");
		rec.selecttime = rd.getLong("selecttime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecVoteUser(poolName));
	}
	
	public boolean insert(String userid, String boardid, String vitemid) {
		String sql = String.format("INSERT INTO %s (boardid, userid, vitemid, selecttime) "
				+ "VALUES('%s','%s','%s',%d)", RecVoteUser.TBL_NAME,  
				boardid, userid, vitemid, System.currentTimeMillis());
		return super.insert(sql);
	}
	
	public boolean updateSelectItem(String userid, String boardid, String vitemid) {
		String sql = String.format("UPDATE %s SET vitemid='%s', selecttime=%d WHERE userid='%s' AND boardid='%s'", RecVoteUser.TBL_NAME, 
				vitemid, System.currentTimeMillis(), userid, boardid);
		return super.update(sql);
	}
	
	public boolean delete(String userid, String boardid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s' AND boardid='%s'", RecVoteUser.TBL_NAME, userid, boardid);
		return super.delete(sql);
	}
	
	public RecVoteUser getVoteUser(String userid, String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' AND boardid='%s'", RecVoteUser.TBL_NAME, userid, boardid);
		return (RecVoteUser) super.getOne(sql);
	}
}
/* implements Serializable{
	private static final long serialVersionUID = 8933480768244542941L;

	@EmbeddedId
	PKVoteUser id;
	
	@Column(length = 64, nullable = false)
	public String vitemid;	//selected vote id
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date selecttime;
	
	public RecVoteUser() {	}
	
	public RecVoteUser(String userid, String boardid, String vitemid) {	
		id = new PKVoteUser(boardid, userid);
		this.vitemid = vitemid;
	}
}
*/