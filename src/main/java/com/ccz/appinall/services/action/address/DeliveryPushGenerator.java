package com.ccz.appinall.services.action.address;

import com.ccz.appinall.services.entity.redis.QueueDeliveryStatus;
import com.ccz.appinall.services.type.enums.EDeliveryStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DeliveryPushGenerator {

	static public QueueDeliveryStatus makeDeliveryStatus(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		QueueDeliveryStatus queueDeliveryStatus = new QueueDeliveryStatus(scode, from, to, orderid, status, msg);
		queueDeliveryStatus.setTitle(getTitle(status, from, to));
		queueDeliveryStatus.setBody(getBody(status, from, to));
		return queueDeliveryStatus;
	}
	
	static private String getTitle(EDeliveryStatus status, String from, String to) {
		switch(status) {
			case ready: return String.format("%s 님이 배송을 요청 하였습니다.", from);
			case assign: return String.format("%s 님이 배송을 체크인 하였습니다.", from);
			case start: return String.format("%s 님이 출발지로 이동합니다.", from);
			case gotcha: return String.format("%s 님이 %s의 배송품 전달 받았습니다.", to, from);
			case delivering: return String.format("%s 님이 목적지로 이동합니다.", from);
			case delivered: return String.format("%s 님이 배송이 전달 되었습니다.", from);
			case confirm: return String.format("%s 님의 배송이 완료처리 되었습니다.", from);
			case finish: return String.format("%s 님이 배송이 최종 정산 되었습니다.", from);
			case cancel: return String.format("%s 님이 배송요청을 취소하였습니다.", from);
			case sendback: return String.format("%s 님이 배송을 취소하였습니다.", to);
			default:
				return "새로운 메시지가 도착하였습니다.";
		}
	}
	
	static private String getBody(EDeliveryStatus status, String from, String to) {
		switch(status) {
			case ready: return String.format("%s 님이 배송을 요청 하였습니다.", from);
			case assign: return String.format("%s 님이 배송을 체크인 하였습니다.", from);
			case start: return String.format("%s 님이 출발지로 이동합니다.", from);
			case gotcha: return String.format("%s 님이 %s의 배송품 전달 받았습니다.", to, from);
			case delivering: return String.format("%s 님이 목적지로 이동합니다.", from);
			case delivered: return String.format("%s 님이 배송이 전달 되었습니다.", from);
			case confirm: return String.format("%s 님의 배송이 완료처리 되었습니다.", from);
			case finish: return String.format("%s 님이 배송이 최종 정산 되었습니다.", from);
			case cancel: return String.format("%s 님이 배송요청을 취소하였습니다.", from);
			case sendback: return String.format("%s 님이 배송을 취소하였습니다.", to);
			default:
				return "새로운 메시지가 도착하였습니다.";
		}
	}

}


/*
 {
   scode: "owy", 
   cmd: "owy_status",
   to: "receive001",
   from: "sender001",
   fromname: "sender user name or nickname",
   data: {
      status: "start",
      time: "2018/01/31 11:37 PM",
      msg: "messages"
   },
   alarmTitle: "your order starts now",
   alarmBody: "who's order starts at 11:37PM..."
} 
 */