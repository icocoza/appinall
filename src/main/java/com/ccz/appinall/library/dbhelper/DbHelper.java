package com.ccz.appinall.library.dbhelper;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.ccz.appinall.services.entity.db.RecDeliveryOrder;

public class DbHelper {
	static int CURSOR_TYPE = ResultSet.TYPE_SCROLL_INSENSITIVE;
	
	
	static public boolean createDatabase(String poolName, String dbName) {
		if(findDatabase(poolName, dbName)==true)
			return true;
		
		String sql = "CREATE DATABASE " + dbName;
		boolean bResult = DbHelper.nonSelect(poolName, sql);
		DbConnMgr.getInst().removeConnectionPool(poolName);
		return bResult;
	}
	
	static public boolean findDatabase(String poolName, String dbName) {
		DbReader reader = DbHelper.getMetaData(poolName);
		try{
			while (reader.hasNext()) {
				String databaseName = reader.getString(1);
				if(databaseName != null && databaseName.equals(dbName))
					return true;
			}
			return false;
		}finally{
			reader.close();
		}
	}

	static public DbReader select(String poolName, String sql) 
	{
		return select(poolName, sql, CURSOR_TYPE);
	}
    
    static public DbReader select(String poolName, String sql, int resultSetType)
    {
    		DbConnection conn = null;
        try
        {
	        	conn = DbConnMgr.getInst().getConnection(poolName);
	        	Statement stmt = conn.getConn(true).createStatement();
	        	ResultSet rs = stmt.executeQuery(sql);
	        return new DbReader(poolName, conn, stmt, rs);
        }
        catch (Exception e)
        {
	        	System.out.println(e.getMessage());
	        	if(conn!=null)
	        		DbConnMgr.getInst().freeConnection(poolName, conn);
            return DbReader.Empty;
        }
    }
    
    static public DbReader preparedSelect(String poolName, String sql, String[] ids) {
		DbConnection conn = null;
        try
        {
	        	conn = DbConnMgr.getInst().getConnection(poolName);
	        	PreparedStatement stmt = conn.connection.prepareStatement(sql);
	        	Array array = stmt.getConnection().createArrayOf("String", ids);
	        	stmt.setArray(1, array);
	        	ResultSet rs = stmt.executeQuery(sql);
		    return new DbReader(poolName, conn, stmt, rs);
        }
        catch (Exception e) {
	        	System.out.println(e.getMessage());
	        	if(conn!=null)
	        		DbConnMgr.getInst().freeConnection(poolName, conn);
	        return DbReader.Empty;
        }
    }

    static public DbReader getMetaData(String poolName) {
    	DbConnection conn = null;
        try
        {
        	conn = DbConnMgr.getInst().getConnection(poolName);
        	ResultSet rs = conn.getConn().getMetaData().getCatalogs();
            return new DbReader(poolName, conn, null, rs);
        }
        catch (Exception e)
        {
        	System.out.println(e.getMessage());
        	if(conn!=null)
        		DbConnMgr.getInst().freeConnection(poolName, conn);
            return DbReader.Empty;
        }    	
    }
    
    static public boolean nonSelect(String poolName, String sql)
    {
    		DbConnection conn = null;
    		Statement stmt = null;
        try
        {
	        	conn = DbConnMgr.getInst().getConnection(poolName);
	        	stmt = conn.getConn(true).createStatement();
	        	boolean bok = (stmt.executeUpdate(sql)>0 ? true : false);
	        	stmt.close();
	        	return bok;
        }
        catch (Exception e) {
	        	System.out.println(e.getMessage());
	            return false;
        }finally {
	        	if(conn != null)
	        		DbConnMgr.getInst().freeConnection(poolName, conn);
        }
    }
    
    /*insert, update, delete only*/
    static public boolean multiQuery(String poolName, String[] sqls)
    {
    		Connection conn = null;
    		DbConnection dbConn = null;
        try
        {
	        	boolean bok = false;
	        	dbConn = DbConnMgr.getInst().getConnection(poolName);
	        conn = dbConn.getConn(false);
	        	for(String sql : sqls) {
	        		Statement stmt = conn.createStatement();
	        		bok = (stmt.executeUpdate(sql)>0 ? true : false);
	        		stmt.close();
	        		if(bok==false)
	        			return false;
	        	}
	        	conn.commit();
	        	return true;
        }
        catch (Exception e)
        {
	        	System.out.println(e.getMessage());
            return false;
        }finally {
	        	if(conn != null)
	        		DbConnMgr.getInst().freeConnection(poolName, dbConn);
        }
    }

    static public int count(String poolName, String sql)
    {
    	int count = 0;
    	DbConnection conn = null;
        try
        {
        	conn = DbConnMgr.getInst().getConnection(poolName);
        	Statement stmt = conn.getConn(true).createStatement();
        	ResultSet result = stmt.executeQuery(sql);
        	if(result.next())
        		count = result.getInt(1);
        	result.close();
        	stmt.close();
        	return count;
        }
        catch (Exception e)
        {
            return -1;
        }finally {
        	if(conn!=null)
        		DbConnMgr.getInst().freeConnection(poolName, conn);
        }
    }
    
   
    static public boolean exist(String poolName, String sql) {
    		DbConnection conn = null;
        try
        {
	        	conn = DbConnMgr.getInst().getConnection(poolName);
	        	Statement stmt = conn.getConn(true).createStatement();
	        	ResultSet result = stmt.executeQuery(sql);
	        	boolean bOk = result.next();
	        	result.close();
	        	stmt.close();
	        	return bOk;
        }
        catch (Exception e)
        {
            return false;
        }finally {
	        	if(conn!=null)
	        		DbConnMgr.getInst().freeConnection(poolName, conn);
        }
    }
    
}
