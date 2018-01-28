package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EDeliveryStatus;

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
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		return null;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return null;
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return null;
	}

	public boolean insert(String orderid, String deliverid, EDeliveryStatus status) {
		return false;
	}
	
	public boolean updateStatus(String orderid, String deliverid, EDeliveryStatus status) {
		return false;
	}

	public boolean updateStatus(String orderid, String deliverid, EDeliveryStatus status, String passcode) {
		return false;
	}

	public RecDeliveryStatus getStatus(String orderid) {
		return null;
	}
	public RecDeliveryStatus getStatus(String orderid, String deliverid) {
		return null;
	}
	
	public boolean deleteStatus(String orderid) {
		return false;
	}
}
