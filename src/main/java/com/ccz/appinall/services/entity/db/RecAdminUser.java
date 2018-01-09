package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EAdminStatus;
import com.ccz.appinall.services.type.enums.EUserRole;

//@Entity
//@Data
//@Table( name="adminuser")
public class RecAdminUser extends DbRecord {
	static final String TBL_NAME = "adminuser";
	
	public String email, passwd;
	public EAdminStatus adminstatus;
	public EUserRole userrole;
	public String username;
	public Date birthday;
	public String nationality;
	public int sex;
	public Timestamp   jointime, leavetime, lasttime, lastpwchange;

	public RecAdminUser(String poolName) {
		super(poolName);
		try {
			birthday = new Date(new SimpleDateFormat("yyyy/mm/dd").parse("1970/01/01").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (email VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "passwd VARCHAR(64) NOT NULL, adminstatus VARCHAR(8) DEFAULT '', userrole VARCHAR(12) DEFAULT '', username VARCHAR(64),"
				+ "birthday DATE, nationality VARCHAR(8) DEFAULT '', sex INTEGER DEFAULT 0,"
				+ "jointime DATETIME DEFAULT now(), lasttime DATETIME DEFAULT now(), leavetime DATETIME, lastpwchange DATETIME DEFAULT now())", RecAdminUser.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecAdminUser rec = (RecAdminUser)r;
		rec.email = rd.getString("email");
		rec.passwd = rd.getString("passwd");
		rec.adminstatus = EAdminStatus.getType("adminstatus");
		rec.userrole = EUserRole.getType("userrole");
		rec.username = rd.getString("username");
		rec.birthday = rd.getDate2("birthday");
		rec.nationality = rd.getString("nationality");
		rec.sex = rd.getInt("sex");
		rec.jointime = rd.getDate("jointime");
		rec.lasttime = rd.getDate("lasttime");
		rec.leavetime = rd.getDate("leavetime");
		rec.lastpwchange = rd.getDate("lastpwchange");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecAdminUser(super.poolName));
	}
	
	public boolean insert(String email, String passwd, EAdminStatus adminstatus, EUserRole userrole, String username, String nationality) {
		this.email = email;
		this.passwd = passwd;
		this.adminstatus = adminstatus;
		this.userrole = userrole;
		this.username = username;
		this.nationality = nationality;
		String sql = String.format("INSERT INTO %s (email, passwd, adminstatus, userrole, username, nationality) "
								 + "VALUES('%s', '%s', '%s', '%s', '%s', '%s')", RecAdminUser.TBL_NAME,
								 email, passwd, adminstatus.getValue(), userrole.getValue(), username, nationality);
		return super.insert(sql);
	}
	
	public boolean insert(String email, String passwd, EAdminStatus adminstatus, EUserRole userrole, String username, Date birthday, String nationality, int sex) {
		this.email = email;
		this.passwd = passwd;
		this.adminstatus = adminstatus;
		this.userrole = userrole;
		this.username = username;
		if(birthday!=null)
			this.birthday = birthday;
		this.nationality = nationality;
		this.sex = sex;
		String sql = String.format("INSERT INTO %s (email, passwd, adminstatus, userrole, username, birthday, nationality, sex) "
								 + "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', %d)", RecAdminUser.TBL_NAME,
								 email, passwd, adminstatus.getValue(), userrole.getValue(), username, this.birthday.toString(), nationality, sex);
		return super.insert(sql);
	}
	
	public RecAdminUser getUser(String email) {
		String sql = String.format("SELECT * FROM %s WHERE email='%s'", RecAdminUser.TBL_NAME, email);
		return (RecAdminUser) super.getOne(sql);
	}

	public boolean updateLastVisit(String email) {
		String sql = String.format("UPDATE %s SET lasttime=now() WHERE email='%s'", RecAdminUser.TBL_NAME, email);
		return super.update(sql);
	}

	public boolean updateLeave(String email) {
		String sql = String.format("UPDATE %s SET passwd='', leavetime=now(), lasttime=now() WHERE email='%s'", RecAdminUser.TBL_NAME, email);
		return super.update(sql);
	}


}
/*{
	@Id
	@Column(length = 64, nullable = false)
	public String email;
	
	@Column(length = 64, nullable = false)
	public String passwd;
	
	@Column(length = 8)
    @Enumerated(EnumType.STRING)
	public EAdminUserType usertype;
	
	@Column(length = 64, nullable = false)
	public String username;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date birthday;
	
	@Column(length = 8)
	@ColumnDefault("")
	public String nationality;
	
	@Column
	public int sex = 0;
	
	@CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	public Date jointime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	public Date leavetime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	public Date lasttime;
	
	@UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
	public Date lastpwchange;
	
	public RecAdminUser() {	}
	
	public RecAdminUser(String email, String passwd, EAdminUserType usertype, String username, Date birthday, String nationality, int sex) {
		this.email = email;
		this.passwd = passwd;
		this.usertype = usertype;
		this.username = username;
		this.birthday = birthday;
		this.nationality = nationality;
		this.sex = sex;
	}
}
*/