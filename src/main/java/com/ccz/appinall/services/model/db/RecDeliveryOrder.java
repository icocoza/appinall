package com.ccz.appinall.services.model.db;

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
import com.ccz.appinall.services.enums.EGoodsSize;
import com.ccz.appinall.services.enums.EGoodsType;
import com.ccz.appinall.services.enums.EGoodsWeight;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysql.jdbc.PreparedStatement;

import lombok.Getter;
import lombok.Setter;

public class RecDeliveryOrder extends DbRecord {
	static final String TBL_NAME = "deliveryorder";
	
	public String orderid;
	@JsonIgnore
	public String senderid;
	public String fromid, toid;
	public String name, notice;
	public EGoodsSize size;
	public EGoodsWeight weight;
	public EGoodsType goodstype;
	public int price;
	public Timestamp begintime, endtime, createtime;
	public String photourl;
	
	public int deliverCount = 0;	//from count(*)
	public RecDeliveryOrder() {
		super("");
	}
	
	public RecDeliveryOrder(String poolName) {
		super(poolName);
	}
	
	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (orderid VARCHAR(64) NOT NULL PRIMARY KEY, "
				+ "senderid VARCHAR(64) NOT NULL, fromid VARCHAR(32) NOT NULL, toid VARCHAR(32) NOT NULL, name VARCHAR(32) NOT NULL, "
				+ "notice VARCHAR(128), size VARCHAR(16) NOT NULL, weight VARCHAR(16) NOT NULL, goodstype VARCHAR(16) NOT NULL, "
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
		rec.fromid = rd.getString("fromid");
		rec.toid = rd.getString("toid");		
		rec.name = rd.getString("name");
		rec.notice = rd.getString("notice");
		rec.size = EGoodsSize.getType(rd.getString("size"));
		rec.weight = EGoodsWeight.getType(rd.getString("weight"));
		rec.goodstype = EGoodsType.getType(rd.getString("goodstype"));
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
	
	public boolean insert(String orderid, String senderid, String fromid, String toid, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType goodstype, int price, long begintime, long endtime, String photourl) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begindt =  new Date(begintime);
		Date enddt = new Date(endtime);
		
		String sql = String.format("INSERT INTO %s (orderid, senderid, fromid, toid, name, notice, size, weight, "
				+ "goodstype, price, begintime, endtime, photourl) "
				+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s',%d,'%s','%s','%s')", RecDeliveryOrder.TBL_NAME,
				orderid, senderid, fromid, toid, name, notice, size, weight, goodstype, price, sdf.format(begindt), sdf.format(enddt), photourl);
		return super.insert(sql);
	}
	
	public List<String> getBuildIds() {
		return Arrays.asList(fromid, toid);
	}
	
	public DbRecord getOrder(String orderid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s'", RecDeliveryOrder.TBL_NAME, orderid);
		if(super.getOne(sql)==DbRecord.Empty)
			return DbRecord.Empty;
		return this;
	}
	
	public boolean update(String orderid, String fromid, String toid, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType goodstype, int price, long begintime, long endtime, String photourl ) {
		if(getOrder(orderid) == DbRecord.Empty)
			return false;
		
		if(fromid!=null && fromid.length()>0) this.fromid = fromid;
		if(toid!=null && toid.length()>0) this.toid = toid;
		if(name!=null && name.length()>0) this.name = name;
		if(notice!=null && notice.length()>0) this.notice = notice;
		if(size!=null && size!=EGoodsSize.none) this.size = size;
		if(weight!=null && weight!=EGoodsWeight.none) this.weight = weight;
		if(goodstype!=null && goodstype!=EGoodsType.none) this.goodstype = goodstype; 
		if(price!=-1) this.price = price; 
		if(begintime!=-1) this.begintime = DateUtils.getTimestamp(begintime); 
		if(endtime!=-1) this.endtime = DateUtils.getTimestamp(endtime); 
		if(photourl!=null && photourl.length()>0) this.photourl = photourl;
		
		String sql = String.format("UPDATE %s SET fromid='%s', toid='%s', name='%s', notice='%s', size='%s', weight='%s', goodstype='%s', "
				+ "price='%d', begintime='%s', endtime='%s', photourl='%s' WHERE orderid='%s'", RecDeliveryOrder.TBL_NAME,
				this.fromid, this.toid, this.name, this.notice, this.size, this.weight, this.goodstype, this.price, 
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
		String qOrderids = Arrays.stream(orderids).map(x -> "'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT * FROM %s WHERE orderid in (%s)", RecDeliveryOrder.TBL_NAME, qOrderids);
		return super.getList(sql).stream().map(e->(RecDeliveryOrder)e).collect(Collectors.toList());
	}

}
