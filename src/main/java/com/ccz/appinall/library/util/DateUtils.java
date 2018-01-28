package com.ccz.appinall.library.util;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtils {
	static public Timestamp getTimestamp(Date date){ 
		return date == null ? null : new Timestamp(date.getTime()); 
	}

	static public Timestamp getTimestamp(long date){
		return getTimestamp(new Date(date));
	}
}
