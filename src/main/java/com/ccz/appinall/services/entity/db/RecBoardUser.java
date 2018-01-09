package com.ccz.appinall.services.entity.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table(name="boarduser")
public class RecBoardUser extends DbRecord {
	public static final String TBL_NAME = "boarduser";
	
	public String boardid, userid;
	public String username;
	public String preference;
	public long visittime;
	
	public RecBoardUser(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (boardid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL,"
				+ "preference VARCHAR(16) NOT NULL, username VARCHAR(32), visittime DATETIME, PRIMARY KEY(boardid, userid, preference))", 
				RecBoardUser.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecBoardUser rec = (RecBoardUser)r;
		rec.boardid = rd.getString("boardid");
		rec.userid = rd.getString("userid");
		rec.username = rd.getString("username");
		rec.preference = rd.getString("preference");
		rec.visittime = rd.getLong("visittime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecBoardUser(poolName));
	}

	public boolean insert(String boardid, String userid, String username, String preference) {
		String sql = String.format("INSERT INTO %s (boardid, userid, username, preference) "
				 + "VALUES('%s', '%s', '%s', '%s')", RecBoardUser.TBL_NAME, boardid, userid, username, preference);
		return super.insert(sql);
	}
	
	public boolean delete(String boardid) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s'", RecBoardUser.TBL_NAME, boardid);
		return super.delete(sql);
	}
	
	public boolean delete(String boardid, String userid, String preference ) {
		String sql = String.format("DELETE FROM %s WHERE boardid='%s' AND userid='%s' AND preference='%s'", 
				RecBoardUser.TBL_NAME, boardid, userid, preference);
		return super.delete(sql);
	}

}

/*implements Serializable{
	private static final long serialVersionUID = -6570228089235049100L;

	@EmbeddedId
	PKBoardUser id;
	
	@Column(length = 32, nullable = false)
	private String username;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date visittime;
	
	public RecBoardUser()	{	}
	
	public RecBoardUser(String boardid, String userid, String username, String preference) {
		id = new PKBoardUser(boardid, userid, preference);
		this.username = username;
	}
}
*/