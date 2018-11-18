package com.ccz.appinall.services.controller.auth;

import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.server.session.SessionItem;
import com.ccz.appinall.services.controller.address.CategoryTable;
import com.ccz.appinall.services.enums.EUserType;
import com.ccz.appinall.services.model.db.RecUser;
import com.ccz.appinall.services.model.redis.SessionData;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

public class AuthSession extends SessionItem<RecUser> {
	public String scode;
	
	@Setter @Getter EUserType userType = EUserType.none;
	@Setter @Getter SessionData sessionData;
	
	@Getter List<String> cleanUpIds = new ArrayList<>();
	
	@Getter @Setter List<CategoryTable> userTableInfo;
	@Getter String buildid;
	@Getter double lon = 0f, lat = 0f;
	
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
	
	public void setBuildingInfo(String buildid, double lon, double lat) {
		this.buildid = buildid;
		this.lon = lon;
		this.lat = lat;
	}
	
	public boolean isIn500m(double lon, double lat) {
		if(lon == 0f || lat == 0f)
			return false;
		double absLon = Math.abs(this.lon - lon);
		double absLat = Math.abs(this.lat - lat);
		if( absLon < 0.005f)
			return true;
		else if( absLat < 0.005f)
			return true;
		else if(absLon + absLat < 0.014f)	//or use the Pythagorean theorem
			return true;
		return false;
	}

}
