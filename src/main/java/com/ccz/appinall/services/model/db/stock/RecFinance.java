package com.ccz.appinall.services.model.db.stock;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.domain.stock.AnnualResultsParser.AnnualResult;

public class RecFinance extends DbRecord {
	static final String TBL_NAME = "financevalue";
	public int code;
	public int year=0;	//yyyy.mm
	public long sales;	//long * 100000000
	public long bizProfit;//long * 100000000
	public long netProfit;//long * 100000000
	public float bizProfitRate;
	public float netProfitRate;
	public float roeRate;
	public float debtRate;
	public float quickRate;
	public float reserveRate;
	public int eps, bps;

	public RecFinance(String poolName) {
		super(poolName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "code INTEGER NOT NULL, year INTEGER NOT NULL, sales LONG, bizProfit LONG, netProfit LONG, "
				+ "bizProfitRate FLOAT, netProfitRate FLOAT, roeRate FLOAT, debtRate FLOAT, quickRate FLOAT, reserveRate FLOAT, "
				+ "eps INTEGER, bps INTEGER, PRIMARY KEY(code, year), "
				+ "INDEX idx_sales(sales), INDEX idx_bizProfit(bizProfit), INDEX idx_netProfit(netProfit),"
				+ "INDEX idx_bizProfitRate(bizProfitRate), idx_debtRate(debtRate))", RecFinance.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecFinance rec = (RecFinance)r;
		rec.code = rd.getInt("code");
		rec.year = rd.getInt("year");
		rec.sales = rd.getLong("sales");
		rec.bizProfit = rd.getLong("bizProfit");
		rec.netProfit = rd.getLong("netProfit");
		rec.bizProfitRate = rd.getFloat("bizProfitRate");
		rec.netProfitRate = rd.getFloat("netProfitRate");
		rec.roeRate = rd.getFloat("roeRate");
		rec.debtRate = rd.getFloat("debtRate");
		rec.quickRate = rd.getFloat("quickRate");
		rec.reserveRate = rd.getFloat("reserveRate");
		rec.eps = rd.getInt("eps");
		rec.bps = rd.getInt("bps");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecFinance(poolName));
	}
	
	public boolean insert(int code, AnnualResult ar) {
		String sql = qInsert(code, ar);
		return super.insert(sql);

	}
	
	public String qInsert(int code, AnnualResult ar) {
		String sql = String.format("INSERT INTO %s (code, year, sales, bizProfit, netProfit, "
				+ "bizProfitRate, netProfitRate, roeRate, debtRate, quickRate, reserveRate, eps, bps) "
				+ "VALUES(%d, %d, %d, %d, %d, %f, %f, %f, %f, %f, %f, %d, %d)", RecFinance.TBL_NAME,
				code, ar.year, ar.sales, ar.bizProfit, ar.netProfit, ar.bizProfitRate, ar.netProfitRate, 
				ar.roeRate, ar.debtRate, ar.quickRate, ar.reserveRate, ar.eps, ar.bps);
		return sql;
	}

	public boolean isExistYear(int code, int year) {
		String sql = String.format("SELECT * FROM %s WHERE code=%d AND year=%s", RecFinance.TBL_NAME, code, year);
		return super.exist(sql);
	}

}
