package com.ccz.appinall.services.model.redis;

import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.enums.ERedisQueueCmd;

import lombok.Getter;
import lombok.Setter;

public class QueueDeliveryStatus extends QueueCmd {
	@Getter QueueData data;
	Notification notification = new Notification();
	
	public QueueDeliveryStatus() {}	//need for serialize
	
	public QueueDeliveryStatus(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		super.cmd = ERedisQueueCmd.delivery_status;
		super.scode = scode;
		super.from = from;
		super.to = to;
		this.data = new QueueData(orderid, status, msg);
	}
	
	public QueueDeliveryStatus setNotification(String title, String body) {
		notification.setTitle(title);
		notification.setBody(body);
		return this;
	}
	
	public class QueueData {
		@Getter String orderid;
	    @Getter EDeliveryStatus status;
	    @Getter long   createtime;
	    @Getter String msg;

	    public QueueData() {}		//need for serialize
	    public QueueData(String orderid, EDeliveryStatus status, String msg) {
	    		this.orderid = orderid;
	    		this.status = status;
	    		this.createtime = System.currentTimeMillis();
	    		this.msg = msg;
	    }
	}
	
	public class Notification {
		@Getter @Setter String title, body;
		public Notification() {}
	}
}
