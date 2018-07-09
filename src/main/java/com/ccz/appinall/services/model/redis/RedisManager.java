package com.ccz.appinall.services.model.redis;

import java.util.List;

import com.ccz.appinall.library.module.redis.RedisCommander;
import com.ccz.appinall.library.module.redis.SingleConnection;

public class RedisManager {

	public static RedisManager s_pThis;
	
	public static RedisManager getInst() {
		if(s_pThis==null)	s_pThis = new RedisManager();
		return s_pThis;
	}
	public static void freeInst() {
		s_pThis = null;
	}
	
	private static String ADDR_GEO = "addr:geo";
	
	SingleConnection redisSingleConnection;
	RedisCommander redisCommander;
	
	public void init(String urlWithPort) {
		redisSingleConnection = new SingleConnection(urlWithPort);
		redisCommander = new RedisCommander(redisSingleConnection);
	}
	
	public long addGeo(double longitude, double latitude, String member) {
		return redisCommander.addGeo(ADDR_GEO, longitude, latitude, member);
	}
	
	public List<String> getGeoRadius(double longitude, double latitude, int meterRadius) {
		return redisCommander.getGeoRadius(ADDR_GEO, longitude, latitude, meterRadius);
	}
}
