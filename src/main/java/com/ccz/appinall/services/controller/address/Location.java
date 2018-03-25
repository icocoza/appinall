package com.ccz.appinall.services.controller.address;

import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;

import lombok.Getter;


@Getter
public class Location {
    String orderid;
    double longitude, latitude;
    double distance;
    
    int routeIndex;

    public Location(GeoResult<RedisGeoCommands.GeoLocation<String>> result, int routeIndex){
        this.orderid = result.getContent().getName();
        this.longitude = result.getContent().getPoint().getX();
        this.latitude = result.getContent().getPoint().getY();
        this.distance = result.getDistance().getValue();
    }
    
    public boolean equalId(Location order) {
    		return this.orderid.equals(order.orderid);
    }
}
