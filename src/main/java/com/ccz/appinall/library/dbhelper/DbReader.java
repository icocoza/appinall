package com.ccz.appinall.library.dbhelper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DbReader {
	private Statement stmt;
    private ResultSet rs;
    private DbConnection conn;
    private String poolName;
    public static final DbReader Empty = null;

    public DbReader(String poolName, DbConnection conn, Statement stmt, ResultSet reader)
    {
	    	this.poolName = poolName;
	    	this.stmt = stmt;
        this.rs = reader;
        this.conn = conn;
    }

    public void close(){
	    	try{
		    	if(conn != null)
		    		DbConnMgr.getInst().freeConnection(poolName, conn);
		    	if(stmt != null)
		    		stmt.close();
		    	if(rs != null)
					rs.close();
	    	}catch(SQLException e) {
	    		e.printStackTrace();
	    	}
    }
    
    public boolean hasNext() {
	    	try {
			return rs.next();
		} catch (Exception e) {
			return false;
		}
    }
    
    public int count()
    {
        int count = 0;
        try {
			if(rs.last()) { 
				count = rs.getRow();
				rs.beforeFirst();
			}
		} catch (Exception e) {
			return -1;
		}
        return count;
    }

    public String getString(String field)
    {
        try
        {
            return rs.getString(field);
        }
        catch (SQLException e) { 
			return null; 
		}
    }
    
    public String getString(int index) {
        try
        {
            return rs.getString(index);
        }
        catch (SQLException e) { 
			return null; 
		}
    }
    
    public short getShort(String field)
    {
        try
        {
            return rs.getShort(field);
        }
        catch (SQLException e) { 
			return 0; 
		}
    }

    public int getInt(String field)
    {
        try
        {
            return rs.getInt(field);
        }
        catch (SQLException e) { 
			return 0; 
		}
    }
    
    public int getInt(int index)
    {
        try
        {
            return rs.getInt(index);
        }
        catch (SQLException e) { 
			return 0; 
		}
    }

    public long getLong(String field)
    {
        try
        {
            return rs.getLong(field);
        }
        catch (SQLException e) { 
			return 0; 
		}
    }

    public double getDouble(String field)
    {
        try
        {
            return rs.getDouble(field);
        }
        catch (SQLException e) { 
			return 0; 
		}
    }

    public Boolean getBoolean(String field)
    {
        try
        {
            return rs.getBoolean(field);
        }
        catch (SQLException e) { 
			return null; 
		}
    }
    
    public Timestamp getDate(String field)
    {
        try
        {
            return rs.getTimestamp(field);
        }
        catch (SQLException e) { 
			return null; 
		}
    }

    public Date getDate2(String field)
    {
        try
        {
            return rs.getDate(field);
        }
        catch (SQLException e) { 
			return null; 
		}
    }
}
