package com.ccz.appinall.services.controller.auth;

import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.services.controller.address.CategoryTable;
import com.ccz.appinall.services.enums.EUserType;
import com.ccz.appinall.services.model.db.RecUser;
import com.ccz.appinall.services.model.redis.SessionData;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class AuthSession extends SessionItem<RecUser> {
	public String scode;
	
	@Setter @Getter EUserType userType = EUserType.none;
	@Setter @Getter SessionData sessionData;
	
	@Getter List<String> cleanUpIds = new ArrayList<>();
	
	@Getter @Setter List<CategoryTable> userTableInfo;
	
	public AuthSession(Channel ch) {
		super(ch, 0);
	}
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
	
	public void addCleanUpId(String id) {
		cleanUpIds.add(id);
	}
	public void delCleanUpId(String id) {
		cleanUpIds.remove(id);
	}
	
	public String getTableIdByCategoryIndex(int index) {
		if(index <0 || userTableInfo.size() <= index)
			return null;
		return userTableInfo.get(index).getTableid();
	}
}
