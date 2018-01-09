package com.ccz.appinall.library.module.fcm;

public class FCMConfig {
	private static FCMConfig s_pThis;
	public static FCMConfig getInst() { return s_pThis = (s_pThis == null) ? new FCMConfig() : s_pThis;	}
	public static void freeInst() {		s_pThis = null;		}

	private String  fcmUrl = "fcm-xmpp.googleapis.com";
	private int 	fcmPort = 5236;    //for commerce
	private int 	fcmPortQa = 5236; //for test
	private String  gcmElementName = "gcm";
	private String  gcmNamespace = "google:mobile:data";
	
	public String getFcmUrl()	{	return	fcmUrl;	}
	public int getFcmPort()	{	return	fcmPort;	}
	public int getFcmPortQa()	{	return	fcmPortQa;	}
	public String getElementName()	{	return	gcmElementName;	}
	public String getNamespace()	{	return	gcmNamespace;	}
	
	public String setFcmUrl(String  fcmUrl)	{	return	this.fcmUrl = fcmUrl;	}
	public int setFcmPort(int 	fcmPort)	{	return	this.fcmPort = fcmPort;	}
	public int setFcmPortQa(int 	fcmPortQa)	{	return	this.fcmPortQa = fcmPortQa;	}
	public String setElementName(String  gcmElementName)	{	return	this.gcmElementName = gcmElementName;	}
	public String setNamespace(String  gcmNamespace)	{	return	this.gcmNamespace = gcmNamespace;	}
	
}
