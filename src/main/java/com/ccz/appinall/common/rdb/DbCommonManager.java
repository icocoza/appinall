package com.ccz.appinall.common.rdb;

import java.sql.Timestamp;

import com.ccz.appinall.library.dbhelper.DbConnMgr;
import com.ccz.appinall.services.domain.stock.AnnualResultsParser.AnnualResult;
import com.ccz.appinall.services.domain.stock.DailySiseParser;
import com.ccz.appinall.services.model.db.stock.RecFinance;
import com.ccz.appinall.services.model.db.stock.RecStockValue;

public class DbCommonManager {
	private final String POOLNAME = "commonPool";
	private final String DBNAME = "common";
	private final String SCODE = DBNAME;
	
	public static DbCommonManager s_pThis;
	public static DbCommonManager getInst() {
		return s_pThis = (s_pThis == null ? new DbCommonManager() : s_pThis);
	}
	public static void freeInst() {		s_pThis = null; 	}

	
	String dbUrl, dbUser, dbPw, dbOptions;
	String poolName;
	public boolean createCommonDatabase(String url, String options, String user, String pw) {
		this.dbUrl = url;
		this.dbOptions = options;
		this.dbUser = user;
		this.dbPw = pw;
		return new DatabaseMaker().createDatabase(POOLNAME, url, DBNAME, options, user, pw);
	}

	public boolean initApp(int initPool, int maxPool) {
		try {
			DbConnMgr.getInst().createConnectionPool(POOLNAME, "jdbc:mysql://"+dbUrl+"/"+SCODE+"?"+dbOptions, dbUser, dbPw, initPool, maxPool);
			new RecStockValue(POOLNAME).createTable();
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insertStockData(DailySiseParser ds) {
		return new RecStockValue(POOLNAME).insert(ds);
	}
	 
	public boolean isExistStockDate(int stockCode, Timestamp stockAt) {
		return new RecStockValue(POOLNAME).isExist(stockCode, stockAt);
	}
	
	public boolean insertFinanceData(int code, AnnualResult ar) {
		return new RecFinance(POOLNAME).insert(code, ar);
	}
	
	public String queryFinanceData(int code, AnnualResult ar) {
		return new RecFinance(POOLNAME).qInsert(code, ar);
	}
	
	public boolean isExistFinanceData(int code, int year) {
		return new RecFinance(POOLNAME).isExistYear(code, year);
	}

}
