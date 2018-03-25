package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecUserVoter extends DbRecord {
	static final String TBL_NAME = "uservoter";
	
	public String userid, voterid, voteitem;
	public int point;
	public boolean like;
	public String comments;
	public Timestamp votetime;
	
	public RecUserVoter(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "userid VARCHAR(64) NOT NULL, voterid VARCHAR(64) NOT NULL, voteitem VARCHAR(64) NOT NULL, "
				+ "point INTEGER DEFAULT 0, comments VARCHAR(256), like BOOLEAN DEFAULT true,"
				+ "votetime DATETIME DEFAULT NOW(), PRIMARY KEY (userid, voterid, voteitem), "
				+ "INDEX idx_voteitem(voteitem), INDEX idx_userid(userid))", RecUserVoter.TBL_NAME);
		
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecUserVoter rec = (RecUserVoter)r;
		
		rec.userid = rd.getString("userid");		
		rec.voterid = rd.getString("voterid");
		rec.voteitem = rd.getString("voteitem");
		rec.point = rd.getInt("point");
		rec.like = rd.getBoolean("like");
		rec.comments = rd.getString("comments");
		rec.votetime = rd.getDate("votetime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecUserVoter(poolName));
	}
	
	public boolean insert(String userid, String voterid, String voteitem, int point, boolean like, String comments) {
		return super.insert(qInsert(userid, voterid, voteitem, point, like, comments));
	}
	
	static public String qInsert(String userid, String voterid, String voteitem, int point, boolean like, String comments) {
		return String.format("INSERT INTO %s (userid, voterid, voteitem, point, like, comments) "
				+ "VALUES('%s','%s','%s',%d,%b,'%s')", RecUserVoter.TBL_NAME,  
				userid, voterid, voteitem, point, like, comments);
	}
	
	public boolean delete(String userid, String voterid, String voteitem) {
		return super.delete(qDelete(userid, voterid, voteitem));
	}
	
	static public String qDelete(String userid, String voterid, String voteitem) {
		return String.format("DELETE FROM %s WHERE userid='%s' AND voterid='%s' AND voteitem='%s'", RecUserVoter.TBL_NAME, userid, voterid, voteitem);
	}
	
	public List<RecUserVoter> getVoterUsers(String userid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' LIMIT %d, %d", RecUserVoter.TBL_NAME, userid, offset, count);
		return super.getList(sql).stream().map(e->(RecUserVoter)e).collect(Collectors.toList());
	}

}
