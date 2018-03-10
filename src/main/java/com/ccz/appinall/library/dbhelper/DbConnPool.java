package com.ccz.appinall.library.dbhelper;

import com.mysql.jdbc.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.ccz.appinall.library.util.ConnectionPool;

public class DbConnPool extends ConnectionPool<DbConnection> {
	String url, uid, pw;
	
	public DbConnPool(String poolName, String url, String uid, String pw, int initCount, int maxCount) throws SQLException {
		super(poolName, maxCount);
		this.url = url;
		this.uid = uid;
		this.pw = pw;
		
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
	}
	
	@Override
	protected DbConnection createConnection() throws SQLException{
        try {
	        	Properties info = new Properties();
	        	if(uid !=null && uid.length()>0 && pw !=null && pw.length()>0 ) {
	        		info.put("user", uid);
	        		info.put("password", pw);
	        	}
	        	Driver driver = null;
	        	if(url.contains("jdbc:phoenix:"))	
	        		driver = (Driver)Class.forName("org.apache.phoenix.jdbc.PhoenixDriver").newInstance();
	        	else 
	        		driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
	    		return new DbConnection(driver.connect(url, info));
        } catch (Exception e) {
        		e.printStackTrace();
            return null;
        }      
	}
	
	@Override
	public void closeConnections() throws SQLException {
		for(DbConnection conn : pools)
			conn.getConn().close();
	}


	@Override
	protected boolean isClosed(DbConnection jconn) {
		try{
			return jconn.getConn().isClosed() || !jconn.getConn().isValid(3);
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
