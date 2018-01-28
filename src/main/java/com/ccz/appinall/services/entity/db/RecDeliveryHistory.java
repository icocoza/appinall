package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EDeliveryStatus;

public class RecDeliveryHistory extends DbRecord {
	static final String TBL_NAME = "deliveryhistory";
	
	public String orderid, deliverid;
	public EDeliveryStatus status;
	public Timestamp statustime;
	public String message;

	public RecDeliveryHistory(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean createTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean insert(String orderid, String deliverid, EDeliveryStatus status, String message) {
		return false;
	}
	
	public List<RecDeliveryHistory> getHistoryList(String orderid) {
		return null;
	}
}
