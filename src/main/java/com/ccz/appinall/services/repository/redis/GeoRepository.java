package com.ccz.appinall.services.repository.redis;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class GeoRepository {
	private static String ADDR_GEO = "addr:geo";
	
	@Autowired private RedisTemplate<String, String> geoOperations;	//저장 규격 TYPE1
	
	public List<String> searchOrder(double longitude, double latitude, int radiusMeter, int limit) {
		Circle circle = new Circle(new Point(longitude, latitude), new Distance(radiusMeter, RedisGeoCommands.DistanceUnit.METERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance().includeCoordinates().sortAscending().limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =geoOperations.opsForGeo().geoRadius(ADDR_GEO, circle, args);
        return results.getContent().stream().map(x -> x.getContent().getName()).collect(Collectors.toList());
	}

}
