package com.ccz.appinall.library.module.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

public class HBaseManager {
	
	static public Algorithm compressType = Algorithm.SNAPPY;
	
	static public void createTable(Configuration config, TableName tableName, String[] columnFamilies, int[] maxValues, int[] ttlSeconds ) throws IOException {
		Connection conn = ConnectionFactory.createConnection(config);
		Admin admin = conn.getAdmin();		
        try {
        	if(admin.tableExists(tableName)==true)
            	return;    
            HTableDescriptor desc = new HTableDescriptor(tableName);
            int index = 0;
            for(String cf :columnFamilies) {
                HColumnDescriptor cfamily = new HColumnDescriptor(cf.getBytes());
                int maxValue = (maxValues == null? 1 : maxValues[index]);
                cfamily.setMaxVersions(maxValue);
                
                if(ttlSeconds!=null && ttlSeconds.length>index && ttlSeconds[index]>0) cfamily.setTimeToLive(ttlSeconds[index]);
                
                cfamily.setCompressionType(compressType);
                desc.addFamily(cfamily);
                index++;
            }
            admin.createTable(desc);
        } finally {
            if(admin !=null) admin.close();
        }
	}
	
	static public boolean existTable(Configuration config, String tableName) {
		TableName tblName = TableName.valueOf(tableName);
        try {
        	Connection conn = ConnectionFactory.createConnection(config);
    		Admin admin = conn.getAdmin();
        	return admin.tableExists(tblName);
        }catch(Exception e) {
        	return false;
        }
	}
	
}
