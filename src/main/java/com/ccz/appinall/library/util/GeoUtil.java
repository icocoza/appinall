package com.ccz.appinall.library.util;

public class GeoUtil {
	private static final double LATITUDE_MIN = -85.05112878d;
	private static final double LATITUDE_MAX = 85.05112878d;
	private static final double LONGITUDE_MIN = -180d;
	private static final double LONGITUDE_MAX = 180d;

	private GeoUtil() {}
	
	public static boolean isInGeoPoint(double longitude, double latitude) {
		return LONGITUDE_MIN <= longitude && LONGITUDE_MAX >= longitude &&
				LATITUDE_MIN <= latitude && LATITUDE_MAX >= latitude;
	}
	
	public static boolean isInLongitude(double longitude) {
		return LONGITUDE_MIN <= longitude && LONGITUDE_MAX >= longitude;
	}
	
	public static boolean isInLatitude(double latitude) {
		return LATITUDE_MIN <= latitude && LATITUDE_MAX >= latitude;
	}
	
	public static boolean pnpoly(double[] vertx, double[] verty, double testx, double testy)
	{
	    int nvert = vertx.length;
	    int i, j;
	    boolean c = false;
	    for (i = 0, j = nvert-1; i < nvert; j = i++) {
	        if ( ((verty[i]>testy) != (verty[j]>testy)) &&
	                (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
	            c = !c;
	    }
	    return c;
	} 
}
