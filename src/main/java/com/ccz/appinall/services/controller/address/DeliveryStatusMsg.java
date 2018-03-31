package com.ccz.appinall.services.controller.address;

import com.ccz.appinall.services.enums.EDeliveryStatus;
import com.ccz.appinall.services.model.redis.QueueDeliveryStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryStatusMsg {
	
	public String makeStatusString(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		try {
			QueueDeliveryStatus queueDeliveryStatus = new QueueDeliveryStatus(scode, from, to, orderid, status, msg);	//for setting message formation
			queueDeliveryStatus.setNotification(getTitle(status, from, to), getBody(status, from, to));
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(status);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public QueueDeliveryStatus makeStatusObject(String scode, String from, String to, String orderid, EDeliveryStatus status, String msg) {
		QueueDeliveryStatus queueDeliveryStatus = new QueueDeliveryStatus(scode, from, to, orderid, status, msg);	//for setting message formation
		return queueDeliveryStatus.setNotification(getTitle(status, from, to), getBody(status, from, to));
	}

	private String getTitle(EDeliveryStatus status, String from, String to) {
		switch(status) {
			case apply: return String.format("%s 님이 배송 신청 하였습니다.", from);
			case ready: return String.format("%s 님이 배송을 요청 하였습니다.", from);
			case assign: return String.format("%s 님이 배송을 체크인 하였습니다.", from);
			case start: return String.format("%s 님이 출발지로 이동합니다.", from);
			case gotcha: return String.format("%s 님이 %s의 배송품 전달 받았습니다.", to, from);
			case delivering: return String.format("%s 님이 목적지로 이동합니다.", from);
			case delivered: return String.format("%s 님이 배송이 전달 되었습니다.", from);
			case confirm: return String.format("%s 님의 배송이 완료처리 되었습니다.", from);
			case finish: return String.format("%s 님이 배송이 최종 정산 되었습니다.", from);
			case cancel_bysender: return String.format("%s 님이 배송요청을 취소하였습니다.", from);
			case cancel_bydeliver: return String.format("%s 님이 배송요청을 취소하였습니다.", to);
			case sendback: return String.format("%s 님이 배송을 취소하였습니다.", to);
			default:
				return "새로운 메시지가 도착하였습니다.";
		}
	}
	
	private String getBody(EDeliveryStatus status, String from, String to) {
		switch(status) {
			case ready: return String.format("%s 님이 배송을 요청 하였습니다.", from);
			case assign: return String.format("%s 님이 배송을 체크인 하였습니다.", from);
			case start: return String.format("%s 님이 출발지로 이동합니다.", from);
			case gotcha: return String.format("%s 님이 %s의 배송품 전달 받았습니다.", to, from);
			case delivering: return String.format("%s 님이 목적지로 이동합니다.", from);
			case delivered: return String.format("%s 님이 배송이 전달 되었습니다.", from);
			case confirm: return String.format("%s 님의 배송이 완료처리 되었습니다.", from);
			case finish: return String.format("%s 님이 배송이 최종 정산 되었습니다.", from);
			case cancel_bysender: return String.format("%s 님이 배송요청을 취소하였습니다.", from);
			case cancel_bydeliver: return String.format("%s 님이 배송요청을 취소하였습니다.", to);
			case sendback: return String.format("%s 님이 배송을 취소하였습니다.", to);
			default:
				return "새로운 메시지가 도착하였습니다.";
		}
	}
}
