package com.ccz.appinall.library.dbhelper;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class DbConnMgr {
	
	private static DbConnMgr s_pThis = null;
	public static synchronized DbConnMgr getInst() {
		return s_pThis = (s_pThis == null? new DbConnMgr() : s_pThis);
	}
	
	public static void freeInst() {	s_pThis = null;	}
	
	private ConcurrentHashMap<String, DbConnPool> connectionMap = new ConcurrentHashMap<String, DbConnPool>();
	
	public void createConnectionPool(String poolName, String url, String uid, String pw, int initCount, int maxCount) throws SQLException {
		connectionMap.put(poolName, new DbConnPool(poolName, url, uid, pw, initCount, maxCount));
	}

	public DbConnection getConnection(String poolName) throws Exception {
        DbConnPool pool = (DbConnPool) connectionMap.get(poolName);
        if (pool != null)
            return pool.getConnection();
        return null;
    }
	
	public void freeConnection(String poolName, DbConnection connection) {
        DbConnPool pool = (DbConnPool) connectionMap.get(poolName);
        if (pool != null)
            pool.returnConnection(connection);
    }
	
	public boolean hasPool(String poolName) {
		if(connectionMap==null)
			return false;
		return connectionMap.contains(poolName);
	}
	
	public void removeConnectionPool(String poolName) {
		try {
			DbConnPool pool = connectionMap.get(poolName);
			if(pool != null)
				pool.closeConnections();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connectionMap.remove(poolName);
	}
}
