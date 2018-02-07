package com.ccz.appinall.services.repository.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DeliverGeoRepository {
	@Autowired
	private RedisTemplate<String, String> geoOperations;	//저장 규격 TYPE1

	private final String DELIVER_LOC = "deliverloc";

	public long addStartLocation(String orderid, double longitude, double latitude) {
		return geoOperations.opsForGeo().geoAdd(DELIVER_LOC, new Point(longitude, latitude), orderid);
	}

	
}
