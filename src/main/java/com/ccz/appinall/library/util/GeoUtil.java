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
	
	public static double toDMS(double wgs84) {
        int d = (int)wgs84;
        int m = (int) ((wgs84 - (double)d)* 60f);
        double s =  ((wgs84 - (double)d) * 60f - (double)m) * 60f;
        String dms = String.format("%d.%02d%02d", d, m, (int)(s * 10000f));
        return Double.parseDouble(dms);
	}
	
	public static double toDegree(double dms) {
		String svalue = dms +"";
		int idx = svalue.indexOf(".");
		
		int degree = Integer.parseInt(svalue.substring(0, idx));
		idx += 1;	//point
		int minute = Integer.parseInt(svalue.substring(idx, idx+2));
		idx += 2;
		int second = Integer.parseInt(svalue.substring(idx, idx+2));
		idx += 2;
		
		String remain = svalue.substring(idx, svalue.length());
	    double allSecond = Double.parseDouble(String.format("%d.%s", second, remain));
		
		double belowDigit = ((minute * 60f) + allSecond) / 3600;
		return degree + belowDigit;
	}
}
