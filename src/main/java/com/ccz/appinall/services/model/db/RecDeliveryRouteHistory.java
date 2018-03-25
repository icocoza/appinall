package com.ccz.appinall.services.model.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

public class RecDeliveryRouteHistory extends DbRecord{
	static final String TBL_NAME = "deliveryroutehistory";
	public long routeid;
	public String deliverid;
	public String routelist;	//delimiter by comma
	public Timestamp routetime;
	public int ordercount;
	
	public RecDeliveryRouteHistory(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s (routeid LONG NOT NULL AUTO_INCREMENT, deliverid VARCHAR(64) NOT NULL,"
				 + "routelist VARCHAR(4096), routetime DATETIME DEFAULT now(), ordercount INTEGER DEFAULT 0, "
				 + "PRIMARY KEY(routeid), INDEX(deliverid))", RecDeliveryRouteHistory.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecDeliveryRouteHistory rec = (RecDeliveryRouteHistory)r;
		rec.routeid = rd.getLong("routeid");
		rec.deliverid = rd.getString("deliverid");
		rec.routelist = rd.getString("routelist");
		rec.routetime = rd.getDate("routetime");
		rec.ordercount = rd.getInt("ordercount");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecDeliveryRouteHistory(poolName));
	}

	public boolean insert(String deliverid, String routelist, int ordercount) {
		String sql = String.format("INSERT INTO %s (deliverid, routelist, ordercount) "
								+ "VALUES ('%s','%s',%d)", RecDeliveryRouteHistory.TBL_NAME, deliverid, routelist, ordercount);
		return super.insert(sql);
	}
	
	public List<RecDeliveryRouteHistory> getRouteList(String deliverid, int offset, int count) {
		String sql = String.format("SELECT * FROM %s WHERE deliverid='%s' DESC routetime LIMIT %d, %d", RecDeliveryRouteHistory.TBL_NAME, deliverid, offset, count);
		return super.getList(sql).stream().map(e->(RecDeliveryRouteHistory)e).collect(Collectors.toList());
	}
}
