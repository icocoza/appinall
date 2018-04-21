package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Data;

//@Entity
//@Data
//@Table( name="vote",
//	indexes = {	@Index(name = "idx_vote_boardid",  columnList="boardid", unique = false)})
public class RecVote extends DbRecord {
	static final String TBL_NAME = "vote";
	
	public String vitemid, boardid; //boardid could have many vitemid(vote item id)
	public int selectcount;
	public String votetext;//, voteurl;
	
	public RecVote(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "vitemid VARCHAR(64) NOT NULL PRIMARY KEY, boardid VARCHAR(64) NOT NULL, "
				+ "selectcount INT DEFAULT 0, votetext VARCHAR(128), voteurl VARCHAR(256), INDEX (boardid)) ", RecVote.TBL_NAME);
		
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecVote rec = (RecVote)r;
		rec.vitemid = rd.getString("vitemid");
		rec.boardid = rd.getString("boardid");
		rec.selectcount = rd.getInt("selectcount");
		rec.votetext = rd.getString("votetext");
		//rec.voteurl = rd.getString("voteurl");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecVote(poolName));
	}

	public boolean insert(String boardid, String vitemid, String votetext) {
		String sql = String.format("INSERT INTO %s (vitemid, boardid, votetext) VALUES('%s','%s','%s')", RecVote.TBL_NAME, 
				vitemid, boardid, votetext);
		return super.insert(sql);
	}
	
	public boolean incVote(String boardid, String vitemid) {
		String sql = String.format("UPDATE %s SET selectcount=selectcount+1 WHERE vitemid='%s' AND boardid='%s'", 
				RecVote.TBL_NAME, vitemid, boardid);
		return super.update(sql);
	}
	
	public boolean decVote(String boardid, String vitemid) {
		String sql = String.format("UPDATE %s SET selectcount=selectcount-1 WHERE vitemid='%s' AND boardid='%s'", 
				RecVote.TBL_NAME, vitemid, boardid);
		return super.update(sql);
	}
	
	public boolean updateVoteText(String boardid, String vitemid, String votetext) {
		String sql = String.format("UPDATE %s SET votetext='%s' WHERE boardid ='%s' AND vitemid='%s'", 
				RecVote.TBL_NAME, votetext, boardid, vitemid);
		return super.update(sql);
	}
	
//	public boolean updateVoteUrl(String boardid, String vitemid, String voteurl) {
//		String sql = String.format("UPDATE %s SET voteurl='%s' WHERE vitemid='%s' AND boardid='%s'", 
//				RecVote.TBL_NAME, voteurl, vitemid, boardid);
//		return super.update(sql);
//	}
	
	public boolean delete(String boardid) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s'", RecVote.TBL_NAME, boardid);
		return super.delete(sql);
	}
	public boolean delete(String boardid, String vitemid) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s' AND vitemid='%s'", RecVote.TBL_NAME, boardid, vitemid);
		return super.delete(sql);
	}
	
	public List<RecVote> getVoteItemList(String boardid) {
		String sql = String.format("SELECT * FROM %s WHERE boardid='%s'", RecVote.TBL_NAME, boardid);
		return super.getList(sql).stream().map(e->(RecVote)e).collect(Collectors.toList());
	}

	
}
/* {
	@Id
	@Column(length = 64, nullable = false)
	public String vitemid;
	
	@Column(length = 64, nullable = false)
	public String boardid; //boardid could have many vitemid(vote item id)
	
	@Column
	public Integer selectcount;
	
	@Column(length = 128)
	public String votetext;
	
	@Column(length = 256)
	public String voteurl;
	
	public RecVote() {	}
	
	public RecVote(String boardid, String vitemid, String votetext, String voteurl) {	
		this.boardid = boardid;
		this.vitemid = vitemid;
		this.votetext = votetext;
		this.voteurl = voteurl;
	}
}
*/