package com.ccz.appinall.library.module.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import com.ccz.appinall.library.util.ConnectionPool;

public class HBaseConnPool  extends ConnectionPool<HBaseConnection> {
	Configuration config;
	
	public HBaseConnPool(Configuration config, String poolName, int initCount, int maxCount) throws Exception {
		super(poolName, maxCount);
		this.config = config;
		super.init(initCount);
	}

	@Override
	protected HBaseConnection createConnection() {
		try {
			return new HBaseConnection(ConnectionFactory.createConnection(config));
		} catch (IOException e) {
		}
		return null;
	}

	@Override
	public void closeConnections() {
		try{
			for(HBaseConnection conn : pools)
				conn.getConn().close();
		} catch (IOException e) {
		}
	}

	@Override
	protected boolean isClosed(HBaseConnection conn) {
		if(conn==null)
			return false;
		return conn.getConn().isClosed();
	}

}
