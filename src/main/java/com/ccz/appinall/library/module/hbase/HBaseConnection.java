package com.ccz.appinall.library.module.hbase;

import org.apache.hadoop.hbase.client.Connection;

public class HBaseConnection {
	Connection connection;
	
	public HBaseConnection(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConn() {
		return connection;
	}

}
