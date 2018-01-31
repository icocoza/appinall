package com.ccz.appinall.library.module.fcm;

public interface FCMResultListener {
	public void OnAck(String msgId, String registrationId);
	public void OnNAck(String msgId, String registrationId);
	public void OnConnectionDraining(FCMConnection conn);
	public void OnControlMessage(String controlType);
	public void OnStanzaError(String xml);
	public void OnCloseConnectionOnSending(String registrationId, String msgId, String jsonPush);
}
