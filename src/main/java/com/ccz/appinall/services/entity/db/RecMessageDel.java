package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table( name="chmsgdel",
//		indexes = {	@Index(name = "idx_msgdel_chid",  columnList="chid", unique = false),
//				@Index(name = "idx_msgdel_deltime",  columnList="deltime", unique = false)})
public class RecMessageDel extends DbRecord {
	public static final String TBL_NAME = "chmesgdel";
	
	public String chid, userid, msgid;
	public Timestamp deltime;

	public RecMessageDel(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (chid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL, "
				+ "msgid VARCHAR(64) NOT NULL, deltime DATETIME DEFAULT now(), PRIMARY KEY(userid, msgid), INDEX(chid, deltime))", 
				RecMessageDel.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecMessageDel rec = (RecMessageDel)r;
		rec.chid = rd.getString("chid");
		rec.userid = rd.getString("userid");
		rec.msgid = rd.getString("msgid");
		rec.deltime = rd.getDate("deltime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecMessageDel(poolName));
	}
	
	public boolean insert(String chid, String userid, String msgid) {
		String sql = String.format("INSERT INTO %s (chid, userid, msgid) VALUES('%s', '%s', '%s')", 
				RecMessageDel.TBL_NAME, chid, userid, msgid);
		return super.insert(sql);
	}
	
	public List<RecDelId> getDelMessageIdList(String chid, String userid, Timestamp joinTime) {
		return new RecDelId(poolName).getList(chid, userid, joinTime);
	}

	public class RecDelId extends DbRecord {
		
		public String msgid;
		
		public RecDelId(String poolName) {
			super(poolName);
		}

		@Override
		public boolean createTable() { return false;	}

		@Override
		protected DbRecord doLoad(DbReader rd, DbRecord r) {
			RecDelId rec = (RecDelId)r;
			rec.msgid = rd.getString("msgid");
			return rec;
		}

		@Override
		protected DbRecord onLoadOne(DbReader rd) {
			return doLoad(rd, this);
		}

		@Override
		protected DbRecord onLoadList(DbReader rd) {
			return doLoad(rd, new RecDelId(poolName));
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		public List<RecDelId> getList(String chid, String userid, Timestamp joinTime) {
			String strToDate = "STR_TO_DATE('"+formatter.format(joinTime)+"', '%Y-%m-%d %H:%i:%s')";
			String sql = String.format("SELECT msgid FROM %s WHERE chid='%s' AND userid='%s' AND deltime > %s ",
					RecMessageDel.TBL_NAME, chid, userid, strToDate);
			return super.getList(sql).stream().map(e->(RecDelId)e).collect(Collectors.toList());
		}
		
	}
}
/* implements Serializable  {
	@EmbeddedId
	private PKMessageUser id;
	
	@Column(length = 64, nullable = false)
	private String chid;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date deltime;
	
	public RecMessageDel() {	}
	
	public RecMessageDel(String chid, String userid, String msgid) {
		id = new PKMessageUser(userid, msgid);
		this.chid = chid;
	}
}
*/