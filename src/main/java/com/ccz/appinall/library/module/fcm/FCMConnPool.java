package com.ccz.appinall.library.module.fcm;

import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.ccz.appinall.library.util.ConnectionPool;

public class FCMConnPool  extends ConnectionPool<FCMConnection> implements FCMResultListener{
	
	private String senderId, senderKey, fcmUrl;
	private int fcmPort;
	
	public FCMConnPool(String poolName, String senderId, String senderKey, String fcmUrl, int fcmPort, int initCount, int maxCount) throws Exception {
		super(poolName, maxCount);
		this.senderId = senderId;
		this.senderKey = senderKey;
		this.fcmUrl = fcmUrl;
		this.fcmPort = fcmPort;
		
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
	}


	@Override
	protected FCMConnection createConnection() throws Exception {
		FCMConnection fcmConnection = new FCMConnection(senderId, senderKey, this);
		if(fcmConnection.connectServer(fcmUrl, fcmPort)==true)
			return fcmConnection;
		return null;
	}
	
	@Override
	public void closeConnections() throws Exception {
		for(FCMConnection conn : pools)
			conn.closeServer();
	}


	@Override
	protected boolean isClosed(FCMConnection obj) {
		return obj.isConnected();
	}


	@Override
	public void OnAck(String msgId, String registrationId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnNAck(String msgId, String registrationId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnConnectionDraining(FCMConnection conn) {
		try {
			conn.closeServer();
			super.removeConnection(conn);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void OnControlMessage(String controlType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnStanzaError(String xml) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnCloseConnectionOnSending(String registrationId, String msgId, String jsonPush) {
		// TODO Auto-generated method stub
		
	}

}
