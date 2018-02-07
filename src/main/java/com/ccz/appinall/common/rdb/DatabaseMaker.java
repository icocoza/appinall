package com.ccz.appinall.common.rdb;

import java.sql.SQLException;

import com.ccz.appinall.library.dbhelper.DbConnMgr;
import com.ccz.appinall.library.dbhelper.DbHelper;

public class DatabaseMaker {
	final String poolName = "AppDbInitPool";
	public boolean createDatabase(String url, String dbName, String user, String pw) {
		try {
			DbConnMgr.getInst().createConnectionPool(poolName, "jdbc:mysql://"+url, user, pw, 2, 2);
			return DbHelper.createDatabase(poolName, dbName);
		} catch (SQLException e) {
			return false;
		}finally {
			DbConnMgr.getInst().removeConnectionPool(poolName);
		}
		
	}
}
