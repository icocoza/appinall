package com.ccz.appinall.services.model.redis;

import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.enums.ERedisQueueCmd;

import lombok.Getter;
import lombok.Setter;

public class QueueDeliveryStatus extends QueueCmd {
	@Getter QueueData data;
	@Getter @Setter String title, body;
	
	public QueueDeliveryStatus() {}	//need for serialize
	
	public QueueDeliveryStatus(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		super.scode = scode;
		super.cmd = ERedisQueueCmd.owy_status;
		super.from = from;
		super.to = to;
		this.data = new QueueData(orderid, status, msg);
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
	
}
