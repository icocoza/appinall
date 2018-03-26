package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.EDeliverPhoto;
import com.ccz.appinall.services.enums.EUserType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecDeliveryPhoto extends DbRecord {
	static final String TBL_NAME = "deliveryphoto";
	
	public long autoid;
	public String fileid;
	public String orderid, userid;
	public EUserType usertype;
	public EDeliverPhoto phototype;

	public RecDeliveryPhoto(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (autoid INT NOT NULL AUTO_INCREMENT PRIMARY KEY, fileid VARCHAR(64) NOT NULL, "
				+ "orderid VARCHAR(64) NOT NULL, userid VARCHAR(64) NOT NULL, usertype VARCHAR(16), phototype VARCHAR(16), "
				+ "INDEX idx_fileid(fileid), INDEX idx_orderid(orderid), INDEX idx_userid(userid))", RecDeliveryPhoto.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliveryPhoto rec = (RecDeliveryPhoto)r;
		rec.autoid = rd.getLong("autoid");
		rec.fileid = rd.getString("fileid");
		rec.userid = rd.getString("userid");
		rec.orderid = rd.getString("orderid");
		rec.usertype = EUserType.getType(rd.getString("usertype"));
		//rec.phototype = EDeliverPhoto.getType(rd.getString("phototype"));
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliveryPhoto(poolName));
	}

	public boolean insert(String fileid, String orderid, String userid, EUserType usertype) {
		return super.insert(qInsert(fileid, orderid, userid, usertype));
	}
	
	static public String qInsert(String fileid, String orderid, String userid, EUserType usertype) {
		String sql = String.format("INSERT INTO %s (fileid, orderid, userid, usertype) "
				+ "VALUES('%s','%s','%s','%s')", RecDeliveryPhoto.TBL_NAME, 
				fileid, orderid, userid, usertype.getValue());
		return sql;
	}
	
	public boolean deleteOrder(String orderid) {
		String sql = String.format("DELETE FROM %s WHERE orderid='%s'", RecDeliveryPhoto.TBL_NAME, orderid);
		return super.delete(sql);
	}

	public boolean deleteOrderFile(String orderid, List<String> fileids) {
		String filestr = fileids.stream().map(x -> "'"+x+"'").collect(Collectors.joining(","));
		String sql = String.format("DELETE FROM %s WHERE orderid='%s' AND fileid IN(%s)", RecDeliveryPhoto.TBL_NAME, orderid, filestr);
		return super.delete(sql);
	}
	
	public List<RecDeliveryPhoto> getDeliveryPhotoList(String orderid) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s'", RecDeliveryPhoto.TBL_NAME, orderid);
		return super.getList(sql).stream().map(e->(RecDeliveryPhoto)e).collect(Collectors.toList());
	}

	public List<RecDeliveryPhoto> getDeliveryPhotoList(String orderid, EUserType usertype) {
		String sql = String.format("SELECT * FROM %s WHERE orderid='%s' AND usertype='%s'", RecDeliveryPhoto.TBL_NAME, orderid, usertype.getValue());
		return super.getList(sql).stream().map(e->(RecDeliveryPhoto)e).collect(Collectors.toList());
	}

}
