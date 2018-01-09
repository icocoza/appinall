package com.ccz.appinall.library.module.hbase;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;

public class HBaseConnPoolMgr {
	private static HBaseConnPoolMgr s_pThis = null;
	
	public static HBaseConnPoolMgr getInst() {
		if(s_pThis==null)
			s_pThis = new HBaseConnPoolMgr();
		return s_pThis;
	}
	
	public static void freeInst() {
		s_pThis = null;
	}
	
	private ConcurrentHashMap<String, HBaseConnPool> connectionMap = new ConcurrentHashMap<String, HBaseConnPool>();
	
	public void createConnectionPool(String poolName, Configuration config, int initConn, int maxConn) throws Exception {		
		connectionMap.put(poolName, new HBaseConnPool(config, poolName, initConn, maxConn));
	}

	public HBaseConnection getConnection(String poolName) throws Exception {
		if(hasConnectionPool(poolName) == false)
			return null;
		HBaseConnPool pool = (HBaseConnPool) connectionMap.get(poolName);
        if (pool != null) {
            return pool.getConnection();
        }
        return null;
    }

	public void returnConnection(String poolName, HBaseConnection connection) {
		if(hasConnectionPool(poolName) == false)
			return;
		HBaseConnPool pool = (HBaseConnPool) connectionMap.get(poolName);
        if (pool != null)
            pool.returnConnection(connection);
    }

	public void closeConnectionPool() {
		Collection<HBaseConnPool> pools = connectionMap.values();
		if(pools!=null)
			for(HBaseConnPool pool : pools)
				pool.closeConnections();
	}
	
	private boolean hasConnectionPool(String poolName) {
		if(connectionMap==null)
			return false;
		return connectionMap.contains(poolName);
	}	
}
