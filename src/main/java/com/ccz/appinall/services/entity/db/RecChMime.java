package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table(name="chmime")
public class RecChMime extends DbRecord {
	public static final String TBL_NAME = "chmime";
	
	public String userid; 	//key
	public String chid;		//key
	public Timestamp addtime, lasttime;

	public RecChMime(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (userid VARCHAR(64) NOT NULL, chid VARCHAR(64) NOT NULL, "
				+ "addtime DATETIME DEFAULT now(), lasttime DATETIME DEFAULT now(), PRIMARY KEY(userid, chid))", RecChMime.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecChMime rec = (RecChMime)r;
		rec.userid = rd.getString("userid");
		rec.chid = rd.getString("chid");
		rec.addtime = rd.getDate("addtime");
		rec.lasttime = rd.getDate("lasttime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecChMime(poolName));
	}

	public boolean insert(String userid, String chid) {
		String sql = String.format("INSERT INTO %s (userid, chid) VALUES('%s', '%s', '%s')", RecChMime.TBL_NAME, userid, chid);
		return super.insert(sql);
	}
	
	public boolean delete(String userid, String chid) {
		String sql = String.format("DELETE FROM %s WHERE userid='%s' AND chid='%s'", RecChMime.TBL_NAME, userid, chid);
		return super.delete(sql);
	}
	
	public boolean updateLastTime(String chid) {
		String sql = String.format("UPDATE %s SET lasttime=now() WHERE chid='%s'", RecChMime.TBL_NAME, chid);
		return super.update(sql);
	}
	
	public RecChMime getChannel(String userid, String chid) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' AND chid='%s'", RecChMime.TBL_NAME, userid, chid);
		return (RecChMime) super.getOne(sql);
	}
	public List<RecChMime> getChannelList(String userid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE userid='%s' ORDER BY lasttime DESC LIMIT %d, %d", RecChMime.TBL_NAME, userid, offset, count);
		return super.getList(sql).stream().map(e->(RecChMime)e).collect(Collectors.toList());
	}
	
	public int getChannelCount(String userid) {
		return super.count(String.format("SELECT COUNT(*) FROM %s WHERE userid='%s'", RecChMime.TBL_NAME, userid)); 
	}
	
	public List<RecChMimeExt> getChannelInfoList(String userid, int offset, int count) {
		return new RecChMimeExt(poolName).getChannelInfoList(userid, offset, count);
	}
	
	public class RecChMimeExt extends DbRecord {
		public String chid;
		public Timestamp lasttime;
		public String userid1, userid2;
		public short attendeecnt;
		public String lastmsg;
		
		public RecChMimeExt(String poolName) {
			super(poolName);
		}

		@Override
		public boolean createTable() {	return false; 	}

		@Override
		protected DbRecord doLoad(DbReader rd, DbRecord r) {
			RecChMimeExt rec = (RecChMimeExt)r;
			rec.chid = rd.getString("chid");
			rec.userid1 = rd.getString("userid1");
			rec.userid2 = rd.getString("userid2");
			rec.lasttime = rd.getDate("lasttime");
			rec.attendeecnt = rd.getShort("attendeecnt");
			rec.lastmsg = rd.getString("lastmsg");
			return rec;
		}

		@Override
		protected DbRecord onLoadOne(DbReader rd) {
			return doLoad(rd, this);
		}

		@Override
		protected DbRecord onLoadList(DbReader rd) {
			return doLoad(rd, new RecChMimeExt(poolName));
		}
		
		public List<RecChMimeExt> getChannelInfoList(String userid, int offset, int count) {
			String sql = String.format("SELECT chmime.chid, channel.userid1, channel.userid2, chmime.lasttime, channel.attendeecnt, channel.lastmsg "
									 + "FROM chmime JOIN channel ON(chmime.chid=channel.chid) "
									 + "WHERE chmime.userid='%s' ORDER BY chmime.lasttime DESC LIMIT %d, %d", userid, offset, count);
			return super.getList(sql).stream().map(e->(RecChMimeExt)e).collect(Collectors.toList());
		}
	}

}

/*implements Serializable {
	private static final long serialVersionUID = 6579748770593933092L;
	
	@EmbeddedId
	private PKChMime id;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date addtime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date lasttime;
	
	public RecChMime()	{	}
	
	public RecChMime(String userid, String chid) {
		id =  new PKChMime(userid, chid);
	}
}
*/