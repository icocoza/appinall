package com.ccz.appinall.library.type.enums;

public enum EDbConnType {
	mysql("mysql"), phoenix("phoenix"), none("none");
	
	public final String value;
	
	private EDbConnType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	static public EDbConnType GetType(String type) {
		if(mysql.value.equals(type))
			return mysql;
		else if(phoenix.value.equals(type))
			return phoenix;
		return	none;
	}
}
