package com.ccz.appinall.services.model.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

//@Entity
//@Data
//@Table( name="msgread",
//		indexes = {	@Index(name = "idx_read_chid",  columnList="chid", unique = false)})
public class RecRead extends DbRecord {
	public static final String TBL_NAME = "chread";
	
	public String chid, userid, msgid;
	public long readtime;
	
	public RecRead(String poolName) {
		super(poolName);
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (chid VARCHAR(64) NOT NULL, "
				+ "userid VARCHAR(64) NOT NULL, msgid VARCHAR(64) NOT NULL, "
				+ "readtime DATETIME DEFAULT now(), PRIMARY KEY(userid, msgid), INDEX(chid))", RecRead.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecRead rec = (RecRead)r;
		rec.chid = rd.getString("chid");
		rec.userid = rd.getString("userid");
		rec.msgid = rd.getString("msgid");
		rec.readtime = rd.getLong("readtime");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecRead(poolName));
	}
	
	public boolean insert(String chid, String userid, String msgid) {
		String sql = String.format("INSERT INTO %s (chid, userid, msgid) "
				 + "VALUES('%s', '%s', '%s')", RecMessage.TBL_NAME, chid, userid, msgid);
		return super.insert(sql);
	}

}
/* implements Serializable  {
	private static final long serialVersionUID = -6786767658258406872L;

	@EmbeddedId
	private PKMessageUser id;
	
	@Column(length = 64, nullable = false)
	public String chid;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date readtime;
	
	public RecRead() {	}
	
	public RecRead(String chid, String userid, String msgid) {
		id = new PKMessageUser(userid, msgid);
		this.chid = chid;
	}
}
*/