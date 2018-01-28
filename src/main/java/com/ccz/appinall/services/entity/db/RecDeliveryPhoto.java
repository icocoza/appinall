package com.ccz.appinall.services.entity.db;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.type.enums.EDeliverPhoto;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecDeliveryPhoto extends DbRecord {
	static final String TBL_NAME = "deliveryphoto";
	
	public long id;
	public String orderid;
	public String senderid, deliverid;
	public String photourl;
	public EDeliverPhoto phototype;

	public RecDeliveryPhoto(String poolName) {
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

}
