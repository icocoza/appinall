package com.ccz.appinall.library.dbhelper;

import java.util.LinkedList;
import java.util.List;

import com.ccz.appinall.library.type.enums.EDbTypes;

public abstract class DbRecord {
	public String poolName, serviceCode;
	public static final DbRecord Empty = null;

	abstract public boolean createTable();
    abstract protected DbRecord doLoad(DbReader rd, DbRecord r);
    
    abstract protected DbRecord onLoadOne(DbReader rd);
    abstract protected DbRecord onLoadList(DbReader rd);
    
    public DbRecord(String poolName) {
    	this.poolName = poolName;
    }
    
	protected DbRecord getOne(String sql)
    {
        DbReader rd = DbHelper.select(poolName, sql);
        try
        {
            if (rd!=DbReader.Empty && rd.hasNext() )
            	return onLoadOne(rd);
            return DbRecord.Empty;
        }
        catch(Throwable e) {
        	return DbRecord.Empty;
        }
        finally
        {
            if (rd != DbReader.Empty)
                rd.close();
        }
    }
	
    protected List<DbRecord> getList(String sql)
    {
        DbReader rd = DbHelper.select(poolName, sql);
        List<DbRecord> records = new LinkedList<DbRecord>();
        try
        {
            while (rd!=DbReader.Empty && rd.hasNext()==true)
                records.add(onLoadList(rd));
        }
        catch(Exception e) {     }
        finally {
            if(rd != DbReader.Empty)
                rd.close();
        }
        return records;
    }

    protected boolean exist(String sql) {
    	return DbHelper.exist(poolName, sql);
    }
    
    protected int count(String sql) {
    	return DbHelper.count(poolName, sql);
    }
    
	@SuppressWarnings("unchecked")
	protected <T> T getField(String sql, String field, T emptyType, EDbTypes type) {
        DbReader rd = DbHelper.select(poolName, sql);
        try
        {
            if (rd==DbReader.Empty || rd.hasNext()==false)
                return emptyType;
            emptyType = (T)(type == EDbTypes.eShort ? rd.getShort(field) : type == EDbTypes.eInt ? rd.getInt(field) : type == EDbTypes.eLong ? rd.getLong(field) :
            	type == EDbTypes.eString ? rd.getString(field) : type == EDbTypes.eDate ? rd.getDate(field) : type == EDbTypes.eBool ? rd.getBoolean(field) :emptyType);
            return emptyType;
        }
        catch(Throwable e) {
        	return emptyType;
        }
        finally
        {
            if (rd != DbReader.Empty)
                rd.close();
        }
	}   

	protected boolean createTable(String sql) {
		return DbHelper.nonSelect(poolName, sql);
	}
	
	protected boolean insert(String sql) {
		return DbHelper.nonSelect(poolName, sql);
	}
	
	protected boolean delete(String sql) {
		return DbHelper.nonSelect(poolName, sql);
	}
	
	protected boolean update(String sql) {
		return DbHelper.nonSelect(poolName, sql);
	}
}
