package com.ccz.appinall.services.model.redis;

import java.util.Random;

import lombok.Getter;

public class SessionData {
	@Getter
	private String userid, ip;
	private int random;
	
	public SessionData() {	}
	
	public SessionData(String userId, String ip) {
		this.userid = userId;
		this.ip = ip;
		this.random = new Random().nextInt(100000);
	}
	
	public boolean equals(SessionData us) {
		return this.userid.equals(us.userid) && this.ip.equals(us.ip) && this.random == us.random;
	}
}
