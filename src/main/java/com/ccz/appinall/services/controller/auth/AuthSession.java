package com.ccz.appinall.services.controller.auth;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.services.model.db.RecUser;

import io.netty.channel.Channel;

public class AuthSession extends SessionItem<RecUser> {
	public String scode;
	
	public AuthSession(Channel ch, int methodType) {
		super(ch, methodType);
	}
	
	@Override
	public String getKey() {
		return super.item.userid;
	}

	@Override
	public AuthSession putSession(RecUser rec, String scode) {
		super.item = rec;
		this.scode = scode;
		return this;
	}
	
	public Channel getCh() {
		return super.channel;
	}

	public String getUserId() {
		return super.item.userid;
	}
	
	public String getUsername() {
		return super.item.username;
	}
}
