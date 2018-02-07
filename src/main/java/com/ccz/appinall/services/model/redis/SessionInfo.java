package com.ccz.appinall.services.model.redis;

import java.util.Random;

import lombok.Getter;

public class SessionInfo {
	@Getter
	private String userId, ip;
	private int randomCode;
	
	public SessionInfo() {	}
	
	public SessionInfo(String userId, String ip) {
		this.userId = userId;
		this.ip = ip;
		this.randomCode = new Random().nextInt(100000);
	}
	
	public boolean equals(SessionInfo us) {
		return this.userId.equals(us.userId) && this.ip.equals(us.ip) && this.randomCode == us.randomCode;
	}
}
