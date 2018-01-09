package com.ccz.appinall.config;

import com.ccz.appinall.library.util.ConfigLoader;

public class DefaultConfig {
	private static DefaultConfig s_pThis;
	public static DefaultConfig getInst() { return s_pThis = (s_pThis == null) ? new DefaultConfig() : s_pThis;	}
	public static void freeInst() {		s_pThis = null;		}
	
	private static final String CONFIG_FILE = "/configuration.cfg";
	
//	private String defaultDir = "./fileupload";
//	private String keystorePath = "";
//	private String keystorePW = "";
//	private String websocketPath = "/wss";
//
//	private String adminMysqlUrl, adminMysqlUser, adminMysqlPw, adminMysqlPoolname, adminMysqlDbName;
//	
//	public String getDefaultDir() {	return defaultDir;	}
//	public String getKeystorePath() { return keystorePath;	}
//	public String getKeystorePW()	{ 	return keystorePW;	}
//	public String getWebsocketPath() {	return websocketPath;	}
//	
//	
//	public String getAdminMysqlUrl() {
//		return adminMysqlUrl;
//	}
//	public String getAdminMysqlUser() {
//		return adminMysqlUser;
//	}
//	public String getAdminMysqlPw() {
//		return adminMysqlPw;
//	}
//	public String getAdminMysqlPoolname() {
//		return adminMysqlPoolname;
//	}
//	public String getAdminMysqlDbName() {
//		return adminMysqlDbName;
//	}
//
//	public void loadDefaultConfiguration() {
//		ConfigLoader.getInst().loadConfig(CONFIG_FILE);
//		adminMysqlUrl = ConfigLoader.getInst().getString("admin.mysql.url");
//		adminMysqlUser = ConfigLoader.getInst().getString("admin.mysql.user");
//		adminMysqlPw = ConfigLoader.getInst().getString("admin.mysql.pw");
//		adminMysqlPoolname = ConfigLoader.getInst().getString("admin.mysql.poolname");
//		adminMysqlDbName = ConfigLoader.getInst().getString("admin.mysql.dbname");
//	}

}
