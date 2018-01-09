package com.ccz.appinall.services.entity.db;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EAdminAppStatus;

import lombok.Setter;

//@Entity
//@Data
//@Table( name="adminapp")
@Setter
public class RecAdminApp  extends DbRecord {
	static final String TBL_NAME = "adminapp";
	
	public String appid, email, scode, title, version;
	public boolean updateforce;
	public String storeurl, description;
	public EAdminAppStatus status;
	public Date regtime, statustime;
	public String token;
	public String fcmid, fcmkey;

	public RecAdminApp(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (appid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "email VARCHAR(64) NOT NULL, scode VARCHAR(32) NOT NULL, title VARCHAR(64) NOT NULL, version VARCHAR(8) DEFAULT '', "
				+ "updateforce BOOLEAN DEFAULT false, storeurl VARCHAR(512) DEFAULT '', description VARCHAR(64) DEFAULT '', status VARCHAR(9) DEFAULT 'ready', "
				+ "regtime DATETIME DEFAULT now(),  statustime DATETIME DEFAULT now(), token VARCHAR(512) NOT NULL,"
				+ "fcmid VARCHAR(32), fcmkey VARCHAR(256))", RecAdminApp.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecAdminApp rec = (RecAdminApp)r;
		rec.appid = rd.getString("appid");
		rec.email = rd.getString("email");
		rec.scode = rd.getString("scode");
		rec.title = rd.getString("title");
		rec.version = rd.getString("version");
		rec.updateforce = rd.getBoolean("updateforce");
		rec.storeurl = rd.getString("storeurl");
		rec.description = rd.getString("description");
		rec.status = EAdminAppStatus.getType(rd.getString("status"));
		rec.regtime = rd.getDate("regtime");
		rec.statustime = rd.getDate("statustime");
		rec.token = rd.getString("token");
		rec.fcmid = rd.getString("fcmid");
		rec.fcmkey = rd.getString("fcmkey");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecAdminApp(poolName));
	}
	
	public boolean insert(String appid, String email, String scode, String title, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String token, String fcmid, String fcmkey) {
		String sql = String.format("INSERT INTO %s (appid, email, scode, title, version, updateforce, storeurl, description, status, token, fcmid, fcmkey) "
								 + "VALUES('%s', '%s', '%s', '%s', '%s', %b, '%s', '%s', '%s', '%s', '%s', '%s')", RecAdminApp.TBL_NAME,
								 appid, email, scode, title, version, updateforce, storeurl, description, status, token, fcmid, fcmkey);
		return super.insert(sql);
	}
	
	public RecAdminApp getApp(String appid) {
		String sql = String.format("SELECT * FROM %s WHERE appid='%s'", RecAdminApp.TBL_NAME, appid);
		return (RecAdminApp) super.getOne(sql);
	}

	public RecAdminApp getApp(String email, String scode) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s' AND scode='%s'", RecAdminApp.TBL_NAME, email, scode);
		return (RecAdminApp) super.getOne(sql);
	}

	public boolean updateApp(String email, String appid, String title, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String fcmid, String fcmkey) {
		String sql = String.format("UPDATE %s SET title='%s', version='%s', updateforce=%b, storeurl='%s', description='%s', "
								 + "status='%s', statustime=now(), fcmid='%s', fcmkey='%s' "
								 + "WHERE email='%s' AND appid='%s'", RecAdminApp.TBL_NAME, 
								 title, version, updateforce, storeurl, description, status.getValue(), fcmid, fcmkey, email, appid);
		return super.update(sql);
	}

	public boolean updateStatus(String email, String appid, EAdminAppStatus status) {
		String sql = String.format("UPDATE %s SET status='%s', statustime=now() WHERE email='%s' AND appid='%s'", RecAdminApp.TBL_NAME, status.getValue(), email, appid);
		return super.update(sql);
	}

	public List<RecAdminApp> getList(EAdminAppStatus status, int offset, int count) {
		String sql = String.format("SELECT * FROM %s ORDER BY statustime DESC LIMIT %d, %d", 
				RecAdminApp.TBL_NAME, offset, count);
		if(EAdminAppStatus.all != status)
			sql = String.format("SELECT * FROM %s WHERE status='%s' ORDER BY statustime DESC LIMIT %d, %d", 
				RecAdminApp.TBL_NAME, status.getValue(), offset, count);
		return super.getList(sql).stream().map(e->(RecAdminApp)e).collect(Collectors.toList());
	}

	public List<RecAdminApp> getList(String email, EAdminAppStatus status, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s' ORDER BY statustime DESC LIMIT %d, %d", 
				RecAdminApp.TBL_NAME, email, offset, count);
		if(EAdminAppStatus.all != status)
			sql = String.format("SELECT * FROM %s WHERE email='%s' AND status='%s' ORDER BY statustime DESC LIMIT %d, %d", 
				RecAdminApp.TBL_NAME, email, status.getValue(), offset, count);
		return super.getList(sql).stream().map(e->(RecAdminApp)e).collect(Collectors.toList());
	}
	
	public int getAppCount(String email, EAdminAppStatus status) {
		if(EAdminAppStatus.all == status)
			return super.count(String.format("SELECT COUNT(*) FROM %s WHERE email='%s'", RecAdminApp.TBL_NAME, email));
		return super.count(String.format("SELECT COUNT(*) FROM %s WHERE email='%s' AND status='%s'", RecAdminApp.TBL_NAME, email, status.getValue())); 
	}
	
	public boolean hasSCode(String scode) {
		String sql = String.format("SELECT * FROM %s WHERE scode='%s'", RecAdminApp.TBL_NAME, scode);
		return super.exist(sql);

	}
}

/*{
	
	@Id
	@Column(length = 64, nullable = false)
	private String appid;
	
	@Column(length = 64, nullable = false)
	private String email;
	
	@Column(length = 32, nullable = false)
	private String scode;
	
	@Column(length = 8, nullable = false)
	@ColumnDefault("")
	private String version;
	
	@Column(nullable = false)
	private boolean updateforce = false;
	
	@Column(length = 256, nullable = false)
	@ColumnDefault("")
	private String storeurl;
	
	@Column(length = 64, nullable = false)
	@ColumnDefault("")
	private String description;
	
	@Column(length = 8)
    @Enumerated(EnumType.STRING)
	private EAdminAppStatus status;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date regtime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	private Date statustime;
	
	@Column(length = 256, nullable = false)
	@ColumnDefault("")
	private String token;
	
	@Column(length = 32)
	private String fcmid;
	
	@Column(length = 256)
	private String fcmkey;
	
	public RecAdminApp() {	}
	
	public RecAdminApp(String appid, String email, String scode, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String token, String fcmid, String fcmkey) {
		this.appid = appid;
		this.email = email;
		this.scode = scode;
		this.version = version;
		this.updateforce = updateforce;
		this.storeurl = storeurl;
		this.description = description;
		this.status = status;
		this.token = token;
		this.fcmid = fcmid;
		this.fcmkey = fcmkey;
	}
	
	public void updateApp(String appid, String email, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String fcmid, String fcmkey) {
		this.appid = appid;
		this.email = email;
		this.version = version;
		this.updateforce = updateforce;
		this.storeurl = storeurl;
		this.description = description;
		this.status = status;
		this.fcmid = fcmid;
		this.fcmkey = fcmkey;
	}
	
	public void updateStatus(String appid, String email, EAdminAppStatus status) {
		this.appid = appid;
		this.email = email;
		this.status = status;
	}
}*/
