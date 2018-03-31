package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EDeliverType;
import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.enums.EDeliverMethod;

public class RecDeliveryStatus extends DbRecord{
	static final String TBL_NAME = "deliverystatus";
	
	public String orderid, deliverid;
	public EDeliveryStatus status;
	public Timestamp updatetime;
	public String startcode, endcode;
	
	public RecDeliveryStatus(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (orderid VARCHAR(64) NOT NULL, deliverid VARCHAR(64) NOT NULL, "
				+ "status VARCHAR(12) NOT NULL, updatetime DATETIME DEFAULT now(), "
				+ "startcode VARCHAR(8) DEFAULT '', endcode VARCHAR(8) DEFAULT '', PRIMARY KEY(orderid, deliverid))",  RecDeliveryStatus.TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliveryStatus rec = (RecDeliveryStatus)r;
		rec.orderid = rd.getString("orderid");
		rec.deliverid = rd.getString("deliverid");
		rec.status = EDeliveryStatus.getType(rd.getString("status"));
		rec.updatetime = rd.getDate("updatetime");
		rec.startcode = rd.getString("startcode");
		rec.endcode = rd.getString("endcode");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliveryStatus(poolName));
	}

	public boolean insert(String orderid, String deliverid, EDeliveryStatus status) {
		String sql = String.format("INSERT INTO %s (orderid, deliverid, status) "
								+ "VALUES ('%s','%s','%s')", RecDeliveryStatus.TBL_NAME, orderid, deliverid, status.getValue());
		return super.insert(sql);
	}
	
	public boolean updateStatus(String orderid, String deliverid, EDeliveryStatus status) {
		String sql = String.format("UPDATE %s SET status='%s' WHERE orderid='%s' AND deliverid='%s'", RecDeliveryStatus.TBL_NAME, 
					status.getValue(), orderid, deliverid);
		return super.update(sql);
	}

	public boolean updateStartStatus(String orderid, String deliverid, EDeliveryStatus status, String passcode) {
		String sql = String.format("UPDATE %s SET status='%s', startcode='%s' WHERE orderid='%s' AND deliverid='%s'", RecDeliveryStatus.TBL_NAME, 
				status.getValue(), passcode, orderid, deliverid);
		return super.update(sql);
	}

	public boolean updateEndStatus(String orderid, String deliverid, EDeliveryStatus status, String passcode) {
		String sql = String.format("UPDATE %s SET status='%s', endcode='%s' WHERE orderid='%s' AND deliverid='%s'", RecDeliveryStatus.TBL_NAME, 
				status.getValue(), passcode, orderid, deliverid);
		return super.update(sql);
	}

	public RecDeliveryStatus getStatus(String orderid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s'", RecDeliveryStatus.TBL_NAME, orderid);
		return (RecDeliveryStatus) super.getOne(sql);
	}
	public RecDeliveryStatus getStatus(String orderid, String deliverid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s' AND deliverid='%s'", RecDeliveryStatus.TBL_NAME, orderid, deliverid);
		return (RecDeliveryStatus) super.getOne(sql);
	}
	
	public boolean deleteStatus(String orderid) {
		String sql = String.format("DELETE FROM %s WHERE orderid='%s'", RecDeliveryStatus.TBL_NAME, orderid);
		return super.delete(sql);

	}
}
