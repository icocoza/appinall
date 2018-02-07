package com.ccz.appinall.services.model.db;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecDeliverCount extends DbRecord{
	static final String TBL_NAME = "deliveryapply";	
	
	public String orderid;
	public int count;
	
	public RecDeliverCount(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliverCount rec = (RecDeliverCount)r;
		rec.orderid = rd.getString("orderid");
		rec.count = rd.getInt(2);
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliverCount(poolName));
	}
	
	public Map<String, Integer> getDeliverCount(String[] orderids) {
		String qOrderids = Arrays.stream(orderids).map(x -> "'" + x + "'").collect(Collectors.joining(","));
		String sql = String.format("SELECT orderid, count(*) FROM %s WHERE orderid in (%s) group by orderid", RecDeliverCount.TBL_NAME, qOrderids);
		return super.getList(sql).stream().map(e->(RecDeliverCount)e).collect(Collectors.toMap(x->x.orderid, x->x.count));
	}

}
