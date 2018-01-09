package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EFriendStatus;

//@Entity
//@Data
//@Table(name="friend")
public class RecFriend  extends DbRecord {
	static final String TBL_NAME = "friend";
	
	public String userid, friendid;
	public EFriendStatus friendstatus;
	public String friendname, friendtype;
	public Timestamp addtime;
	
	public RecFriend(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() { 
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL, friendid VARCHAR(64) NOT NULL,"
								 + "friendstatus INTEGER DEFAULT 0, friendname VARCHAR(32), friendtype CHAR(1), addtime DATETIME DEFAULT now(), "
								 + "PRIMARY KEY(userid, friendid), INDEX(userid, friendid))", RecFriend.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecFriend rec = (RecFriend)r;
		rec.userid = rd.getString("boardid");
		rec.friendid = rd.getString("friendid");
		rec.friendstatus = EFriendStatus.getType(rd.getString("friendstatus"));
		rec.friendname = rd.getString("friendname");
		rec.friendtype = rd.getString("friendtype");
		rec.addtime = rd.getDate("addtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecFriend(poolName));
	}

	public boolean insert(String userid, String friendid, String friendname, String friendtype) {	//default normal = 0
		String sql = String.format("INSERT INTO %s (userid, friendid, friendname, friendtype) "
				 + "VALUES('%s', '%s', '%s', '%s')", RecFriend.TBL_NAME, userid, friendid, friendname, friendtype);
		return super.insert(sql);
	}

	public boolean delete(String userid, String friendid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s' AND friendid='%s'", RecFriend.TBL_NAME, userid, friendid);
		return super.delete(sql);
	}
	
	public boolean updateFriendStatus(String userid, String friendid, EFriendStatus friendstatus) {
		String sql = String.format("UPDATE %s SET friendstatus=%d WHERE userid='%s' AND friendid='%s'", RecFriend.TBL_NAME, friendstatus.getValue(), userid, friendid);
		return super.update(sql);
	}
	
	public List<RecFriend> getList(String userid, EFriendStatus friendstatus, int offset, int count) {
		if(EFriendStatus.all == friendstatus)
			return getListAll(userid, offset, count);
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' AND friendstatus=%d LIMIT %d, %d", 
				RecFriend.TBL_NAME, userid, friendstatus.getValue(), offset, count);
		return super.getList(sql).stream().map(e->(RecFriend)e).collect(Collectors.toList());
	}

	private List<RecFriend> getListAll(String userid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' LIMIT %d, %d", 
				RecFriend.TBL_NAME, userid, offset, count);
		return super.getList(sql).stream().map(e->(RecFriend)e).collect(Collectors.toList());
	}
	
	public List<RecFriend> getList(String userid, List<String> friendids) {
		String idstrlist = friendids.stream().map(e->"'"+e+"'").collect(Collectors.joining(","));
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' AND friendid IN (%s)", RecFriend.TBL_NAME, userid, idstrlist);
		return super.getList(sql).stream().map(e->(RecFriend)e).collect(Collectors.toList());
	}
	
	public int getCount(String userid, EFriendStatus friendstatus) {
		if(EFriendStatus.all == friendstatus)
			return super.count(String.format("SELECT COUNT(*) FROM %s WHERE userid='%s'", RecFriend.TBL_NAME, userid));
		return super.count(String.format("SELECT COUNT(*) FROM %s WHERE userid='%s' AND friendstatus=%d", RecFriend.TBL_NAME, userid, friendstatus.getValue())); 
	}
	
	public List<RecFriendInfo> getFriendMeList(String userid, EFriendStatus friendstatus, int offset, int count)  {
		return new RecFriendInfo(poolName).getFriendMeList(userid, friendstatus, offset, count);
	}

	public int getFriendMeCount(String userid, EFriendStatus friendstatus) {
		if(EFriendStatus.all == friendstatus)
			return super.count(String.format("SELECT COUNT(*) FROM %s WHERE friendid='%s'", RecFriend.TBL_NAME, userid));
		return super.count(String.format("SELECT COUNT(*) FROM %s WHERE friendid='%s' AND friendstatus=%d", RecFriend.TBL_NAME, userid, friendstatus.getValue())); 
	}
	
	public class RecFriendInfo extends DbRecord {
	 	public String userid;
		public String username, usertype;
		public String email; //optional

		public RecFriendInfo(String poolName) {
			super(poolName);
		}

		@Override
		public boolean createTable() { 	return false; 	}

		@Override
		protected DbRecord doLoad(DbReader rd, DbRecord r) {
			RecFriendInfo rec = (RecFriendInfo)r;
			rec.userid = rd.getString("userid");
			rec.username = rd.getString("username");
			rec.usertype = rd.getString("usertype");
			rec.email = rd.getString("email");
			return rec;
		}

		@Override
		protected DbRecord onLoadOne(DbReader rd) {
			return doLoad(rd, this);
		}

		@Override
		protected DbRecord onLoadList(DbReader rd) {
			return doLoad(rd, new RecFriendInfo(poolName));
		}
		
		private List<RecFriendInfo> getFriendMeList(String userid, EFriendStatus friendstatus, int offset, int count) {
			String sql = String.format("SELECT userid, username, usertype, email FROM %s WHERE userid IN (SELECT userid FROM %s WHERE friendid='%s' LIMIT %d, %d)", 
					RecUser.TBL_NAME, RecFriend.TBL_NAME, userid, offset, count);
			if(EFriendStatus.all != friendstatus)
				sql = String.format("SELECT userid, username, usertype, email FROM %s WHERE userid IN (SELECT userid FROM %s WHERE friendid='%s' AND friendstatus=%d) LIMIT %d, %d", 
						RecUser.TBL_NAME, RecFriend.TBL_NAME, userid, friendstatus.getValue(), offset, count);
			return super.getList(sql).stream().map(e->(RecFriendInfo)e).collect(Collectors.toList());
		}
		
	}

}
/* implements Serializable{
	private static final long serialVersionUID = 7533520219624510214L;
	
	@EmbeddedId
	private PKFriend id;
	
	@Column(length = 8)
    @Enumerated(EnumType.STRING)
	private EFriendStatus friendstatus = EFriendStatus.friend;
	
	@Column(length = 32)
	private String friendname;
	
	@Column(length = 8)
	private String friendtype;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	public RecFriend() {		}
	
	public RecFriend(String userid, String friendid, String friendname, String friendtype) {		
		id = new PKFriend(userid, friendid);
		this.friendname = friendname;
		this.friendtype = friendtype;
	}
	
	public void updateFriendStatus(EFriendStatus friendstatus) {
		this.friendstatus = friendstatus;
	}
}
*/