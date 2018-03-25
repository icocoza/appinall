package com.ccz.appinall.services.model.address;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class RecGeo {
	private double lon, lat;
	
	public RecGeo(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
}
