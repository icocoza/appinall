package com.ccz.appinall.services.repository.redis;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.Getter;

@Repository
public class GeoRepository {

	@Autowired
	private RedisTemplate<String, String> geoOperations;	//저장 규격 TYPE1

	private final String ORDER_START = "order_start";
	private final String ORDER_END = "order_end";
	
	public long addStartLocation(String orderid, double longitude, double latitude) {
		return geoOperations.opsForGeo().geoAdd(ORDER_START, new Point(longitude, latitude), orderid);
	}
	
	public long addEndLocation(String orderid, double longitude, double latitude) {
		return geoOperations.opsForGeo().geoAdd(ORDER_END, new Point(longitude, latitude), orderid);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addLocation(final String orderid, double fromLon, double fromLac, double toLon, double toLac) {
		geoOperations.execute(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.multi();
				connection.geoAdd(ORDER_START.getBytes(), new Point(fromLon, fromLac), orderid.getBytes());
				connection.geoAdd(ORDER_END.getBytes(), new Point(toLon, toLac), orderid.getBytes());
				connection.exec();
				return null;
			}
		});
	}
	public List<Location> searchOrderStart(double longitude, double latitude, int radius, int limit) {
		return this.searchOrder(ORDER_START, longitude, latitude, radius, limit);
	}
	
	public List<Location> searchOrderEnd(double longitude, double latitude, int radius, int limit) {
		return this.searchOrder(ORDER_END, longitude, latitude, radius, limit);
	}

	private List<Location> searchOrder(String key, double longitude, double latitude, int radius, int limit) {
		
		Circle circle = new Circle(new Point(longitude, latitude), new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance().includeCoordinates().sortAscending().limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =geoOperations.opsForGeo().geoRadius(key, circle, args);
        return results.getContent().stream().map(Location::new).collect(Collectors.toList());
	}
	
    @Getter
    public static class Location {

        String orderid;
        double longitude, latitude;
        double distance;

        private Location(GeoResult<RedisGeoCommands.GeoLocation<String>> result){
            this.orderid = result.getContent().getName();
            this.longitude = result.getContent().getPoint().getX();
            this.latitude = result.getContent().getPoint().getY();
            this.distance = result.getDistance().getValue();
        }
        
        public boolean equalId(Location order) {
        		return this.orderid.equals(order.orderid);
        }
    }
}
