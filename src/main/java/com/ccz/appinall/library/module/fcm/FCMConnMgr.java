package com.ccz.appinall.library.module.fcm;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;


public class FCMConnMgr {
	private static FCMConnMgr s_pThis = null;
	public static FCMConnMgr getInst() {	return s_pThis = (s_pThis == null? new FCMConnMgr() : s_pThis);	}
	public static void freeInst() {	s_pThis = null;	}
	
	private ConcurrentHashMap<String, FCMConnPool> connectionMap = new ConcurrentHashMap<String, FCMConnPool>();
	
	public void createConnectionPool(String poolName, String senderId, String senderKey, String fcmUrl, int fcmPort, int initCount, int maxCount) throws Exception {
		connectionMap.put(poolName, new FCMConnPool(poolName, senderId, senderKey, fcmUrl, fcmPort, initCount, maxCount));
	}

	public FCMConnection getConnection(String poolName) throws Exception {
        FCMConnPool pool = (FCMConnPool) connectionMap.get(poolName);
        if (pool != null)
            return pool.getConnection();
        return null;
    }
	
	public void freeConnection(String poolName, FCMConnection connection) {
        FCMConnPool pool = (FCMConnPool) connectionMap.get(poolName);
        if (pool != null)
            pool.returnConnection(connection);
    }
	
	public boolean hasPool(String poolName) {
		if(connectionMap==null)
			return false;
		return connectionMap.contains(poolName);
	}
	
	public void removeConnectionPool(String poolName) throws Exception {
		try {
			FCMConnPool pool = connectionMap.get(poolName);
			if(pool != null)
				pool.closeConnections();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connectionMap.remove(poolName);
	}
}
