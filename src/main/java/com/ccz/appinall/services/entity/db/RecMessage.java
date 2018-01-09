package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EMessageType;

//@Entity
//@Data
//@Table( name="chmessage",
//		indexes = {	@Index(name = "idx_msg_chid",  columnList="chid", unique = false),
//		@Index(name = "idx_msg_createtime",  columnList="createtime", unique = false)})
public class RecMessage extends DbRecord {
	public static final String TBL_NAME = "chmessage";
	
	public String msgid, chid;
	public Timestamp createtime;
	public EMessageType msgtype;
	public String senderid;	//userid(64)
	public String message;
	public int readcnt;
	
	public RecMessage(String poolName) {
		super(poolName);
	}
	
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (msgid VARCHAR(64) NOT NULL PRIMARY KEY, chid VARCHAR(64) NOT NULL, "
				+ "senderid VARCHAR(64) NOT NULL, createtime DATETIME DEFAULT now(), msgtype TINYINT, message VARCHAR(1024) NOT NULL, readcnt INT DEFAULT 0, "
				+ "INDEX(chid, createtime) )", RecMessage.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecMessage rec = (RecMessage)r;
		rec.msgid = rd.getString("msgid");
		rec.chid = rd.getString("chid");
		rec.senderid = rd.getString("senderid");
		rec.createtime = rd.getDate("createtime");
		rec.msgtype = EMessageType.getType(rd.getString("msgtype"));
		rec.message = rd.getString("message");
		rec.readcnt = rd.getInt("readcnt");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecMessage(poolName));
	}
	
	public boolean insert(String msgid, String chid, String senderid, EMessageType msgtype, String msg) {
		String sql = String.format("INSERT INTO %s (msgid, chid, senderid, msgtype, message) "
				 + "VALUES('%s', '%s', '%s', %d, '%s')", RecMessage.TBL_NAME, msgid, chid, senderid, msgtype.getValue(), msg);
		return super.insert(sql);
	}
	
	public boolean delete(String msgid) {
		String sql = String.format("DELETE FROM %s WHERE msgid='%s'", RecMessage.TBL_NAME, msgid);
		return super.delete(sql);
	}

	public boolean deleteChMsg(String chid) {
		String sql = String.format("DELETE FROM %s WHERE chid='%s'", RecMessage.TBL_NAME, chid);
		return super.delete(sql);
	}
	
	public RecMessage getMessage(String chid, String msgid) {
		String sql = String.format("SELECT * FROM %s WHERE chid='%s' AND msgid='%s'", RecMessage.TBL_NAME, chid, msgid);
		return (RecMessage) super.getOne(sql);
	}
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public List<RecMessage> getMessageList(String chid, Timestamp joinTime, int offset, int count) {
		String strToDate = "STR_TO_DATE('"+formatter.format(joinTime)+"', '%Y-%m-%d %H:%i:%s')";
		String sql = String.format("SELECT * FROM %s WHERE chid='%s' AND createtime > %s "
				+ "ORDER BY createtime DESC LIMIT %d, %d", RecMessage.TBL_NAME, chid, strToDate, offset, count);
		return super.getList(sql).stream().map(e->(RecMessage)e).collect(Collectors.toList());
	}

	public List<RecMessage> getMessageListWithoutDeletion(String chid, Timestamp joinTime, String deleteIdsWithComma, int offset, int count) {
		String strToDate = "STR_TO_DATE('"+formatter.format(joinTime)+"', '%Y-%m-%d %H:%i:%s')";
		String sql = String.format("SELECT * FROM %s WHERE chid='%s' AND createtime > %s AND msgid NOT IN(%s) "
				+ "ORDER BY createtime DESC LIMIT %d, %d", RecMessage.TBL_NAME, chid, strToDate, deleteIdsWithComma, offset, count);
		return super.getList(sql).stream().map(e->(RecMessage)e).collect(Collectors.toList());
	}

	public boolean incReadCount(String msgid) {
		String sql = String.format("UPDATE %s SET readcnt = readcnt+1 WHERE msgid='%s'", RecMessage.TBL_NAME, msgid);
		return super.update(sql);
	}

}
/* {
	@Id
	@Column(length = 64, nullable = false)
	private String msgid;
	
	@Column(length = 64, nullable = false)
	private String chid;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(length = 8)
    @Enumerated(EnumType.STRING)
	private EMessageType msgtype;
	
	@Column(length = 64, nullable = false)
	private String senderid;	//userid(64)
	
	@Column(length = 1024)
	private String message;
	
	@Column
	private Integer readcnt;
	
	public RecMessage() {	}
	
	public RecMessage(String msgid, String chid, String senderid, EMessageType msgtype, String message) {	
		this.msgid = msgid;
		this.chid = chid;
		this.senderid = senderid;
		this.msgtype = msgtype;
		this.message = message;
	}
	
}
*/