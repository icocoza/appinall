package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.util.List;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EDeliverType;
import com.ccz.appinall.services.type.enums.EDeliveryType;

public class RecDeliveryApply extends DbRecord {
	static final String TBL_NAME = "deliveryapply";
	
	public String orderid, deliverid;
	public Timestamp begintime, endtime, choosetime;
	public int price;
	public EDeliverType delivertype;
	public EDeliveryType deliverytype;
	public boolean enabled;
	
	public RecDeliveryApply(String poolName) {
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

	public boolean insert(String orderid, String deliverid, long begintime, long endtime, int price, EDeliverType delivertype, EDeliveryType deliverytype) {
		String sql = "";
		return super.insert(sql);
	}
	
	public List<RecDeliveryApply> getDeliverList(String orderid) {
		return null;
	}
	
	public boolean updateEnabled(String orderid, String deliverid, boolean enabled) {
		return false;
	}
	
}
