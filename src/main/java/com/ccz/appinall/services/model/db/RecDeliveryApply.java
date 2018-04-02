package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EDeliverType;
import com.ccz.appinall.services.enums.EDeliverMethod;
import com.ccz.appinall.services.enums.EGoodsSize;
import com.ccz.appinall.services.enums.EGoodsType;
import com.ccz.appinall.services.enums.EGoodsWeight;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecDeliveryApply extends DbRecord {
	static final String TBL_NAME = "deliveryapply";

	@JsonIgnore
	public String orderid;
	public String deliverid;
	public String username;
	public Timestamp begintime, endtime, choosetime;
	public int price;
	public EDeliverType delivertype;
	public EDeliverMethod deliverytype;
	public boolean enabled;
	
	public RecDeliveryApply(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (orderid VARCHAR(64) NOT NULL, deliverid VARCHAR(64) NOT NULL, "
				+ "username VARCHAR(64) NOT NULL, begintime DATETIME, endtime DATETIME, choosetime DATETIME DEFAULT now(), "
				+ "price INTEGER DEFAULT 0, delivertype VARCHAR(12), deliverytype VARCHAR(12), enabled BOOLEAN DEFAULT true,"
				+ "PRIMARY KEY(orderid, deliverid))",  RecDeliveryApply.TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliveryApply rec = (RecDeliveryApply)r;
		rec.orderid = rd.getString("orderid");
		rec.deliverid = rd.getString("deliverid");
		rec.username = rd.getString("username");
		rec.begintime = rd.getDate("begintime");
		rec.endtime = rd.getDate("endtime");
		rec.choosetime = rd.getDate("choosetime");
		rec.price = rd.getInt("price");
		rec.delivertype = EDeliverType.getType(rd.getString("delivertype"));
		rec.deliverytype = EDeliverMethod.getType(rd.getString("deliverytype"));
		rec.enabled = rd.getBoolean("enabled");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliveryApply(poolName));
	}

	public boolean insert(String orderid, String deliverid, String username, long begintime, long endtime, int price, EDeliverType delivertype, EDeliverMethod deliverytype) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begindt =  new Date(begintime);
		Date enddt = new Date(endtime);
		
		String sql = String.format("INSERT INTO %s (orderid, deliverid, username, begintime, endtime, price, delivertype, deliverytype) "
				+ "VALUES ('%s','%s','%s','%s','%s',%d,'%s','%s')", RecDeliveryApply.TBL_NAME,
				orderid, deliverid, username, sdf.format(begindt), sdf.format(enddt), price, delivertype.getValue(), deliverytype.getValue());
		return super.insert(sql);
	}
	
	public boolean insert(String orderid, String deliverid, String username) {
		String sql = String.format("INSERT INTO %s (orderid, deliverid, username) "
				+ "VALUES ('%s','%s','%s')", RecDeliveryApply.TBL_NAME, orderid, deliverid, username);
		return super.insert(sql);
	}
	
	public List<RecDeliveryApply> getDeliverList(String orderid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s'", RecDeliveryApply.TBL_NAME, orderid);
		return super.getList(sql).stream().map(e->(RecDeliveryApply)e).collect(Collectors.toList());
	}
	
	public boolean updateEnabled(String orderid, String deliverid, boolean enabled) {
		String sql = String.format("UPDATE %s SET enabled=%b WHERE orderid='%s' AND deliverid='%s'", RecDeliveryApply.TBL_NAME, enabled, orderid, deliverid);
		return super.update(sql);
	}
	
}
