package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table( name="voteinfo")
public class RecVoteInfo extends DbRecord {
	static final String TBL_NAME = "voteinfo";
	
	public String boardid, userid;
	public long expiretime;
	public boolean isclosed;
	
	public RecVoteInfo(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "boardid VARCHAR(64) NOT NULL PRIMARY KEY, userid VARCHAR(64) NOT NULL,"
				+ "expiretime LONG NOT NULL, isclosed BOOLEAN DEFAULT false) ", RecVoteInfo.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecVoteInfo rec = (RecVoteInfo)r;
		rec.boardid = rd.getString("boardid");
		rec.userid = rd.getString("userid");
		rec.expiretime = rd.getLong("expiretime");
		rec.isclosed = rd.getBoolean("isclosed");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecVoteInfo(poolName));
	}

	public boolean insert(String boardid, String userid, long expiretime) {
		String sql = String.format("INSERT INTO %s (boardid, userid, expiretime) "
				+ "VALUES('%s', '%s', %d)", RecVoteInfo.TBL_NAME,  
				boardid, userid, expiretime);
		return super.insert(sql);
	}
	
	public boolean updateExpireTime(String boardid, String userid, long expiretime) {
		String sql = String.format("UPDATE %s SET expiretime=%d WHERE boardid='%s' AND userid='%s'", RecVoteInfo.TBL_NAME, expiretime, boardid, userid);
		return super.update(sql);
	}

	public boolean updateClose(String boardid, String userid, boolean isclosed) {
		String sql = String.format("UPDATE %s SET isclosed=%b WHERE boardid='%s' AND userid='%s'", RecVoteInfo.TBL_NAME, isclosed, boardid, userid);
		return super.update(sql);
	}

	public boolean delete(String boardid, String userid) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s' AND userid='%s'", RecVoteInfo.TBL_NAME, boardid, userid);
		return super.delete(sql);
	}
	
	public RecVoteInfo getVoteInfo(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s'", RecVoteInfo.TBL_NAME, boardid);
		return (RecVoteInfo) super.getOne(sql);
	}
	
	public List<RecVoteInfo> getVoteInfoList(List<String> boardids) {
		String commaboardids = boardids.stream().map(e->"'"+e+"'").collect(Collectors.joining(","));
		String sql = String.format("SELECT * FROM %s WHERE boardid IN (%s)", RecVoteInfo.TBL_NAME, commaboardids);
		return super.getList(sql).stream().map(e->(RecVoteInfo)e).collect(Collectors.toList());
	}

}
/* {
	@Id
	@Column(length = 64, nullable = false)
	public String boardid;
	
	@Column(length = 64, nullable = false)
	public String userid;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date expiretime;
	
	@Column
	public Boolean isclosed = false;
	
	public RecVoteInfo() { 	}
	
	public RecVoteInfo(String boardid, String userid, Date expiretime, boolean isclosed) { 	
		this.boardid = boardid;
		this.userid = userid;
		this.expiretime = expiretime;
		this.isclosed = isclosed;
	}
}
*/