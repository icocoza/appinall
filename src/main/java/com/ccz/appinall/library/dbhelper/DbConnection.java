package com.ccz.appinall.library.dbhelper;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConnection {
	Connection connection;
	
	public DbConnection(Connection con) {
		this.connection = con;
	}
	
	public Connection getConn() {
		return connection;
	}
	
	public Connection getConn(boolean bAuto) {
		try {
			connection.setAutoCommit(bAuto);
		} catch (SQLException e) {
		}
		return connection;
	}
}
