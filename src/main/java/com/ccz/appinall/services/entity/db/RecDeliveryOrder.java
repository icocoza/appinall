package com.ccz.appinall.services.entity.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.util.DateUtils;
import com.ccz.appinall.services.type.enums.EGoodsSize;
import com.ccz.appinall.services.type.enums.EGoodsType;
import com.ccz.appinall.services.type.enums.EGoodsWeight;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysql.jdbc.PreparedStatement;

public class RecDeliveryOrder extends DbRecord {
	static final String TBL_NAME = "deliveryorder";
	
	public String orderid;
	@JsonIgnore
	public String senderid;
	public String from, to;
	public String name, notice;
	public EGoodsSize size;
	public EGoodsWeight weight;
	public EGoodsType type;
	public int price;
	public Timestamp begintime, endtime, createtime;
	public String photourl;
	
	public RecDeliveryOrder() {
		super("");
	}
	
	public RecDeliveryOrder(String poolName) {
		super(poolName);
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (orderid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "senderid VARCHAR(64) NOT NULL, from VARCHAR(32) NOT NULL, to VARCHAR(32) NOT NULL, name VARCHAR(32) NOT NULL, "
				+ "notice VARCHAR(128), size VARCHAR(16) NOT NULL, weight VARCHAR(16) NOT NULL, type VARCHAR(16) NOT NULL, "
				+ "price INTEGER NOT NULL, begintime DATETIME NOT NULL, endtime DATETIME NOT NULL, photourl VARCHAR(128), "
				+ "createtime DATETIME DEFAULT now(), "
				+ "INDEX idx_userid(senderid), INDEX idx_size(size), INDEX idx_weight(weight), "
				+ "INDEX idx_begintime(begintime), INDEX idx_endtime(endtime))",  RecDeliveryOrder.TBL_NAME);
		return super.createTable(sql); 
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliveryOrder rec = (RecDeliveryOrder)r;
		rec.orderid = rd.getString("orderid");
		rec.senderid = rd.getString("senderid");
		rec.from = rd.getString("from");
		rec.to = rd.getString("to");		
		rec.name = rd.getString("name");
		rec.notice = rd.getString("notice");
		rec.size = EGoodsSize.getType(rd.getString("size"));
		rec.weight = EGoodsWeight.getType(rd.getString("weight"));
		rec.type = EGoodsType.getType(rd.getString("type"));
		rec.price = rd.getInt("price");
		rec.begintime = rd.getDate("begintime");
		rec.endtime = rd.getDate("endtime");
		rec.createtime = rd.getDate("createtime");
		rec.photourl = rd.getString("photourl");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliveryOrder(poolName));
	}
	
	public boolean insert(String orderid, String senderid, String from, String to, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType type, int price, long begintime, long endtime, String photourl) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begindt =  new Date(begintime);
		Date enddt = new Date(endtime);
		
		String sql = String.format("INSERT INTO %s (orderid, senderid, from, to, name, notice, size, weight, "
				+ "type, price, begintime, endtime, photourl) "
				+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s',%d,'%s','%s','%s')", RecDeliveryOrder.TBL_NAME,
				orderid, senderid, from, to, name, notice, size, weight, type, price, sdf.format(begindt), sdf.format(enddt), photourl);
		return super.insert(sql);
	}
	
	public List<String> getBuildIds() {
		return Arrays.asList(from, to);
	}
	
	public DbRecord getOrder(String orderid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s'", RecDeliveryOrder.TBL_NAME, orderid);
		if(super.getOne(sql)==DbRecord.Empty)
			return DbRecord.Empty;
		return this;
	}
	
	public boolean update(String orderid, String from, String to, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType type, int price, long begintime, long endtime, String photourl ) {
		if(getOrder(orderid) == DbRecord.Empty)
			return false;
		
		if(from!=null && from.length()>0) this.from = from;
		if(to!=null && to.length()>0) this.to = to;
		if(name!=null && name.length()>0) this.name = name;
		if(notice!=null && notice.length()>0) this.notice = notice;
		if(size!=null && size!=EGoodsSize.none) this.size = size;
		if(weight!=null && weight!=EGoodsWeight.none) this.weight = weight;
		if(type!=null && type!=EGoodsType.none) this.type = type; 
		if(price!=-1) this.price = price; 
		if(begintime!=-1) this.begintime = DateUtils.getTimestamp(begintime); 
		if(endtime!=-1) this.endtime = DateUtils.getTimestamp(endtime); 
		if(photourl!=null && photourl.length()>0) this.photourl = photourl;
		
		String sql = String.format("UPDATE %s SET from='%s', to='%s', name='%s', notice='%s', size='%s', weight='%s', type='%s', "
				+ "price='%d', begintime='%s', endtime='%s', photourl='%s' WHERE orderid='%s'", RecDeliveryOrder.TBL_NAME,
				this.from, this.to, this.name, this.notice, this.size, this.weight, this.type, this.price, 
				this.begintime, this.endtime, this.photourl, this.orderid);
		return super.update(sql);
	}

	public List<RecDeliveryOrder> getListOrder(String senderid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE senderid='%s' ORDER BY createtime DESC LIMIT %d, %d", 
				RecDeliveryOrder.TBL_NAME, senderid, offset, count);
		return super.getList(sql).stream().map(e->(RecDeliveryOrder)e).collect(Collectors.toList());
	}

	public List<RecDeliveryOrder> getListBegin(String senderid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE senderid='%s' ORDER BY begintime DESC LIMIT %d, %d", 
				RecDeliveryOrder.TBL_NAME, senderid, offset, count);
		return super.getList(sql).stream().map(e->(RecDeliveryOrder)e).collect(Collectors.toList());
	}

	public List<RecDeliveryOrder> getListEnd(String senderid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE senderid='%s' ORDER BY endtime DESC LIMIT %d, %d", 
				RecDeliveryOrder.TBL_NAME, senderid, offset, count);
		return super.getList(sql).stream().map(e->(RecDeliveryOrder)e).collect(Collectors.toList());
	}

	public List<RecDeliveryOrder> getListByIds(String[] orderids) {
		String sql = String.format("SELECT * FROM %s WHERE orderid in (?)", RecDeliveryOrder.TBL_NAME);
		return super.getList(sql, orderids).stream().map(e->(RecDeliveryOrder)e).collect(Collectors.toList());
	}
}
