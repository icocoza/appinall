package com.ccz.appinall.services.repository.redis;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.ccz.appinall.services.controller.address.Location;

import lombok.Getter;

@Repository
public class OrderGeoRepository {

	@Autowired
	private RedisTemplate<String, String> geoOperations;	//저장 규격 TYPE1

	private final String ORDER_FROM = "aia:gps:order:start";
	private final String ORDER_TO = "aia:gps:order:end";
	
	public long addStartLocation(String orderid, double longitude, double latitude) {
		return geoOperations.opsForGeo().geoAdd(ORDER_FROM, new Point(longitude, latitude), orderid);
	}
	
	public long addEndLocation(String orderid, double longitude, double latitude) {
		return geoOperations.opsForGeo().geoAdd(ORDER_TO, new Point(longitude, latitude), orderid);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addLocation(final String orderid, double fromLon, double fromLac, double toLon, double toLac) {
		geoOperations.execute(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				try {
				connection.multi();
				connection.geoAdd(ORDER_FROM.getBytes(), new Point(fromLon, fromLac), orderid.getBytes());
				connection.geoAdd(ORDER_TO.getBytes(), new Point(toLon, toLac), orderid.getBytes());
				connection.exec();
				}catch(Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public Map<String, Location> searchOrderFrom(double longitude, double latitude, int radius, int limit, int routeIndex) {
		return this.searchOrder(ORDER_FROM, longitude, latitude, radius, limit, routeIndex);
	}
	
	public Map<String, Location> searchOrderTo(double longitude, double latitude, int radius, int limit, int routeIndex) {
		return this.searchOrder(ORDER_TO, longitude, latitude, radius, limit, routeIndex);
	}

	/**
	 * redis내 gps 반경 검색 진행 
	 * @param key
	 * @param longitude
	 * @param latitude
	 * @param radius
	 * @param limit
	 * @param routeIndex 진행 방향 검색을 진행해야 함. 따라서 index가 낮은 곳에서 높은 곳으로 결과를 재정리 할 필요가 있음. 진행방향과 역배송 막기 위함
	 * @return
	 */
	private Map<String, Location> searchOrder(String key, double longitude, double latitude, int radius, int limit, int routeIndex) {
		
		Circle circle = new Circle(new Point(longitude, latitude), new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance().includeCoordinates().sortAscending().limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =geoOperations.opsForGeo().geoRadius(key, circle, args);
        return results.getContent().stream().map(x -> new Location(x, routeIndex)).collect(Collectors.toMap(Location::getOrderid, x -> x));
	}
	
}
