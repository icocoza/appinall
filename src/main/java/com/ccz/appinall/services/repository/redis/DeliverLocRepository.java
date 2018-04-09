package com.ccz.appinall.services.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeliverLocRepository {
	@Autowired
	private RedisTemplate<String, String> redisGeoOperations;

	private final String DELIVER_LOC = "aia:gps:deliver";

	public long setLocation(String deliverid, double longitude, double latitude) {
		return redisGeoOperations.opsForGeo().geoAdd(DELIVER_LOC, new Point(longitude, latitude), deliverid);
	}

	
}
