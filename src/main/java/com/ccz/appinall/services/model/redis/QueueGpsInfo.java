package com.ccz.appinall.services.model.redis;

import com.ccz.appinall.services.controller.location.RecDataLocation.GpsInfo;
import com.ccz.appinall.services.enums.ERedisQueueCmd;

public class QueueGpsInfo extends QueueCmd {

	private GpsInfo data;
	
	public QueueGpsInfo() {		
		super.cmd = ERedisQueueCmd.gps_info;
	}
	
	public QueueGpsInfo(String scode, String from, String to, GpsInfo gpsInfo) {		
		super.cmd = ERedisQueueCmd.gps_info;
		super.scode = scode;
		super.from = from;
		super.to = to;
		data = gpsInfo;
	}
	
}
