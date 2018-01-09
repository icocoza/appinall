package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table( name="channel",
//		indexes = {	@Index(name = "idx_userids",  columnList="userid1, attendees", unique = false)})
public class RecChannel extends DbRecord {
	public static final String TBL_NAME = "channel";
	
	public String chid;
	public String userid1, userid2; 
	public Timestamp createtime, lasttime;
	public short attendeecnt;
	public String lastmsg;
	public short type;
	
	public String attendees;
	
	public RecChannel(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (chid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "userid1 VARCHAR(64) NOT NULL, userid2 VARCHAR(10240) NOT NULL, createtime DATETIME DEFAULT now(), "
				+ "lasttime DATETIME DEFAULT now(), attendeecnt SMALLINT NOT NULL, "
				+ "lastmsg VARCHAR(128) DEFAULT '', type SMALLINT, INDEX(userid1, lasttime, attendeecnt))", RecChannel.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecChannel rec = (RecChannel)r;
		rec.chid = rd.getString("chid");
		rec.userid1 = rd.getString("userid1");
		rec.userid2 = rd.getString("userid2");
		rec.createtime = rd.getDate("createtime");
		rec.lasttime = rd.getDate("lasttime");
		rec.attendeecnt = rd.getShort("attendeecnt");
		rec.lastmsg = rd.getString("lastmsg");
		rec.type = rd.getShort("type");
		
		rec.attendees = rec.userid2;
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecChannel(poolName));
	}

	public boolean insert(String chid, String userid1, String attendees, short attendeecnt) {
		String sql = String.format("INSERT INTO %s (chid, userid1, userid2, attendeecnt, type) "
				 + "VALUES('%s', '%s', '%s', %d, %d)", RecChannel.TBL_NAME, chid, userid1, attendees, attendeecnt, attendeecnt);
		return super.insert(sql);
	}
	
	public boolean delete(String chid) {
		String sql = String.format("DELETE FROM %s WHERE chid='%s'", RecChannel.TBL_NAME, chid);
		return super.delete(sql);
	}
	
	public boolean updateAttendee(String chid, String attendees, short attendeecnt) {
		String sql = String.format("UPDATE %s SET userid2='%s', attendeecnt=%d WHERE chid='%s'", RecChannel.TBL_NAME, attendees, attendeecnt, chid);
		return super.update(sql);
	}
	
	public boolean updateAttendee(String chid, String attendees, short attendeecnt, short type) {
		String sql = String.format("UPDATE %s SET userid2='%s', attendeecnt=%d, type=%d WHERE chid='%s'", RecChannel.TBL_NAME, attendees, attendeecnt, type, chid);
		return super.update(sql);
	}
	
	public boolean updateLasttime(String chid) {
		String sql = String.format("UPDATE %s, %s SET %s.lasttime=now(), %s.lasttime=now() WHERE %s.chid=%s.chid AND %s.chid='%s'", 
				RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, chid);
		return super.update(sql);
	}
	
	public boolean updateLastMsgAndTime(String chid, String lastmsg) {
		String sql = String.format("UPDATE %s, %s SET %s.lasttime=now(), %s.lasttime=now(), %s.lastmsg='%s' WHERE %s.chid=%s.chid AND %s.chid='%s'", 
				RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, lastmsg, 
				RecChannel.TBL_NAME, RecChMime.TBL_NAME, RecChannel.TBL_NAME, chid);
		return super.update(sql);
	}
	
	public RecChannel getChannel(String chid) {
		String sql = String.format("SELECT * FROM %s WHERE chid='%s'", RecChannel.TBL_NAME, chid);
		return (RecChannel) super.getOne(sql);
	}
	
	public RecChannel findChannel(String userid1, String userid2) {
		String sql = String.format("SELECT * FROM %s WHERE (userid1='%s' AND userid2='%s') OR (userid1='%s' AND userid2='%s')", RecChannel.TBL_NAME, userid1, userid2, userid2, userid1);
		return (RecChannel) super.getOne(sql);
	}
	
	public List<RecChLastMsg> getChannelLastMsg(List<String> chids) {
		return new RecChLastMsg(poolName).getChannelLastMsg(chids);
	}
	
	public class RecChLastMsg extends DbRecord {	//to enhance performance
		public String chid;
		public Timestamp lasttime;
		public String lastmsg;
		
		public RecChLastMsg(String poolName) {
			super(poolName);
		}

		@Override
		public boolean createTable() {	return false; 	}

		@Override
		protected DbRecord doLoad(DbReader rd, DbRecord r) {
			RecChLastMsg rec = (RecChLastMsg)r;
			rec.chid = rd.getString("chid");
			rec.lasttime = rd.getDate("lasttime");
			rec.lastmsg = rd.getString("lastmsg");
			return rec;
		}

		@Override
		protected DbRecord onLoadOne(DbReader rd) {
			return doLoad(rd, this);
		}

		@Override
		protected DbRecord onLoadList(DbReader rd) {
			return doLoad(rd, new RecChLastMsg(poolName));
		}
		
		public List<RecChLastMsg> getChannelLastMsg(List<String> chids) {
			String query = chids.stream().filter(e-> e != null && e.length() > 0).map(e->"'"+e+"'").collect(Collectors.joining(","));
			String sql = String.format("SELECT chid, lasttime, lastmsg FROM channel WHERE chid IN(%s)", query);
			return super.getList(sql).stream().map(e->(RecChLastMsg)e).collect(Collectors.toList());
		}
	}
}
/*{
	@Id
	@Column(length = 64, nullable = false)
	private String chid;
	
	@Column(length = 64, nullable = false)
	private String userid1;
	
	@Column(length = 64, nullable = false)
	private String attendees; 
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date lasttime;
	
	@Column(nullable = false)
	private Short attendeecnt;
	
	@Column(length = 128)
	@ColumnDefault("")
	private String lastmsg;
	
	@Column
	private Short type;
	
	public RecChannel() {	}
	
	public RecChannel(String chid, String userid1, String attendees, short attendeecnt) {	
		this.chid = chid;
		this.userid1 = userid1;
		this.attendees = attendees;
		this.attendeecnt = attendeecnt;
	}
	
	public void updateAttendee(String attendees, short attendeecnt) {
		this.attendees = attendees;
		this.attendeecnt = attendeecnt;
	}
	
	public void updateAttendee(String chid, String attendees, short attendeecnt, short type) {
		this.attendees = attendees;
		this.attendeecnt = attendeecnt;
		this.type = type;
	}
	
	public void updateLastMsg(String lastmsg) {
		this.lastmsg = lastmsg;
	}
}
*/