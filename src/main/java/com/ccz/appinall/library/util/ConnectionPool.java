package com.ccz.appinall.library.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ConnectionPool<C> {
    public abstract void closeConnections() throws Exception;
    protected abstract C createConnection() throws Exception;    
    protected abstract boolean isClosed(C obj);

	protected ConcurrentLinkedQueue<C> pools = new ConcurrentLinkedQueue<C>();
	
	protected String poolName = "";
	protected int nMaxCount = 32, nCheckoutCount = 0;

	public ConnectionPool(String poolName, int maxCount) {
		this.poolName = poolName;
		this.nMaxCount = maxCount;
	}
	public int getCheckoutCount() {	return nCheckoutCount;	}
    public String getPoolName() {	return poolName;	}
	
    protected void init(int count) throws Exception {
    	C connection = null;
    	for(int i=0; i<count; i++)
    		if( (connection = createConnection()) != null)
    			pools.add(connection);
    }
    
    protected void addPool(C obj) {
    	if(obj!=null)
    		pools.add(obj);
    }
    
    public synchronized C getConnection() throws Exception {
   	   	C connection = pools.poll();
        if(connection != null)
        		nCheckoutCount++;
        else if (nCheckoutCount < nMaxCount) {
        		nCheckoutCount++;
            return createConnection();
        }
        if (connection != null && isClosed(connection)) {
        		removeConnection(connection);		//not available connection which is created once upon a time 
            connection = getConnection();
            if(connection != null && isClosed(connection)){	
            		removeConnection(connection);	//not available connection if pool status
            		connection = createConnection();
            }
        }
        return connection;
    }
    
    public synchronized void returnConnection(C connection) {
	    	if(connection!=null)
	    		nCheckoutCount--;
	    	pools.add(connection);
	    	this.notifyAll();
    }    

    public synchronized void removeConnection(C connection) {
	    	--nCheckoutCount;
	    	if(pools.contains(connection))    		
	    		pools.remove(connection);
	    	connection = null;
    }

}
