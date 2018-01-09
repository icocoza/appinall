package com.ccz.appinall.services.action.auth;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.services.entity.db.RecUser;

import io.netty.channel.Channel;

public class AuthSession extends SessionItem<RecUser> {
	public String serviceCode;
	
	public AuthSession(Channel ch, int methodType) {
		super(ch, methodType);
	}
	
	@Override
	public String getKey() {
		return super.item.userid;
	}

	@Override
	public AuthSession putSession(RecUser rec, String serviceCode) {
		super.item = rec;
		this.serviceCode = serviceCode;
		return this;
	}
	
	public Channel getCh() {
		return super.channel;
	}

	public String getUserId() {
		return super.item.userid;
	}
	
	public String getUuid() {
		return super.item.devuuid;
	}
	
	public String getUsername() {
		return super.item.username;
	}
}
