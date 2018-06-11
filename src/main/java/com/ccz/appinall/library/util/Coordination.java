package com.ccz.appinall.library.util;

import org.springframework.data.geo.Point;

public class Coordination {
	
	static public double transferGrs80TmCoordY(double wgs84X, double wgs84Y)
	   {
	    	double CS_LON_RAD = wgs84Y * 0.0174532925199433;
	       double CS_LAT_RAD = wgs84X * 0.0174532925199433;
	       double CS_V = 6378137.0 / Math.pow(1.0 - 0.00669438 * Math.pow(Math.sin(CS_LAT_RAD), 2.0), 0.5);
	       double CS_T = Math.pow(Math.tan(CS_LAT_RAD), 2.0);
	       double CS_C = 0.00674 * Math.pow(Math.cos(CS_LAT_RAD), 2.0);
	       double CS_A = (CS_LON_RAD - 2.2165681500327987) * Math.cos(CS_LAT_RAD);
	       double CS_M = 6378137.0 * ((0.998326405 - 3.0 * Math.pow(0.00669438, 2.0) / 64.0 - 5.0 * Math.pow(0.00669438, 3.0) / 256.0) * CS_LAT_RAD - (0.0025103925 + 3.0 * Math.pow(0.00669438, 2.0) / 32.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(2.0 * CS_LAT_RAD) + (15.0 * Math.pow(0.00669438, 2.0) / 256.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(4.0 * CS_LAT_RAD) - 35.0 * Math.pow(0.00669438, 3.0) / 3072.0 * Math.sin(6.0 * CS_LAT_RAD));
	       wgs84Y = 200000.0 + 1.0 * CS_V * (CS_A + (1.0 - CS_T + CS_C) * Math.pow(CS_A, 3.0) / 6.0 + (5.0 - 18.0 * CS_T + Math.pow(CS_T, 2.0) + 72.0 * CS_C - 0.39092000000000005) * Math.pow(CS_A, 5.0) / 120.0);
	       wgs84X = 500000.0 + 1.0 * (CS_M - 4207498.01915032 + CS_V * Math.tan(CS_LAT_RAD) * (Math.pow(CS_A, 2.0) / 2.0 + (5.0 - CS_T + 9.0 * CS_C + 4.0 * Math.pow(CS_C, 2.0)) * Math.pow(CS_A, 4.0) / 24.0 + (61.0 - 58.0 * CS_T + Math.pow(CS_T, 2.0) + 600.0 * CS_C - 2.2242) * Math.pow(CS_A, 6.0) / 720.0));
	       return wgs84X;
	  }
	  

	static public double transferGrs80TmCoordX(double wgs84X, double wgs84Y){
	    double CS_LON_RAD = wgs84Y * 0.0174532925199433;
	       double CS_LAT_RAD = wgs84X * 0.0174532925199433;
	       double CS_V = 6378137.0 / Math.pow(1.0 - 0.00669438 * Math.pow(Math.sin(CS_LAT_RAD), 2.0), 0.5);
	       double CS_T = Math.pow(Math.tan(CS_LAT_RAD), 2.0);
	       double CS_C = 0.00674 * Math.pow(Math.cos(CS_LAT_RAD), 2.0);
	       double CS_A = (CS_LON_RAD - 2.2165681500327987) * Math.cos(CS_LAT_RAD);
	       double CS_M = 6378137.0 * ((0.998326405 - 3.0 * Math.pow(0.00669438, 2.0) / 64.0 - 5.0 * Math.pow(0.00669438, 3.0) / 256.0) * CS_LAT_RAD - (0.0025103925 + 3.0 * Math.pow(0.00669438, 2.0) / 32.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(2.0 * CS_LAT_RAD) + (15.0 * Math.pow(0.00669438, 2.0) / 256.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(4.0 * CS_LAT_RAD) - 35.0 * Math.pow(0.00669438, 3.0) / 3072.0 * Math.sin(6.0 * CS_LAT_RAD));
	       wgs84Y = 200000.0 + 1.0 * CS_V * (CS_A + (1.0 - CS_T + CS_C) * Math.pow(CS_A, 3.0) / 6.0 + (5.0 - 18.0 * CS_T + Math.pow(CS_T, 2.0) + 72.0 * CS_C - 0.39092000000000005) * Math.pow(CS_A, 5.0) / 120.0);
	       wgs84X = 500000.0 + 1.0 * (CS_M - 4207498.01915032 + CS_V * Math.tan(CS_LAT_RAD) * (Math.pow(CS_A, 2.0) / 2.0 + (5.0 - CS_T + 9.0 * CS_C + 4.0 * Math.pow(CS_C, 2.0)) * Math.pow(CS_A, 4.0) / 24.0 + (61.0 - 58.0 * CS_T + Math.pow(CS_T, 2.0) + 600.0 * CS_C - 2.2242) * Math.pow(CS_A, 6.0) / 720.0));
	       return wgs84Y;
	  }
/*	function transferGrs80TmCoordY(wgs84X, wgs84Y)
	   {
	    double CS_LON_RAD = parseFloat(wgs84Y) * 0.0174532925199433;
	       double CS_LAT_RAD = parseFloat(wgs84X) * 0.0174532925199433;
	       double CS_V = 6378137.0 / Math.pow(1.0 - 0.00669438 * Math.pow(Math.sin(CS_LAT_RAD), 2.0), 0.5);
	       double CS_T = Math.pow(Math.tan(CS_LAT_RAD), 2.0);
	       double CS_C = 0.00674 * Math.pow(Math.cos(CS_LAT_RAD), 2.0);
	       double CS_A = (CS_LON_RAD - 2.2165681500327987) * Math.cos(CS_LAT_RAD);
	       double CS_M = 6378137.0 * ((0.998326405 - 3.0 * Math.pow(0.00669438, 2.0) / 64.0 - 5.0 * Math.pow(0.00669438, 3.0) / 256.0) * CS_LAT_RAD - (0.0025103925 + 3.0 * Math.pow(0.00669438, 2.0) / 32.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(2.0 * CS_LAT_RAD) + (15.0 * Math.pow(0.00669438, 2.0) / 256.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(4.0 * CS_LAT_RAD) - 35.0 * Math.pow(0.00669438, 3.0) / 3072.0 * Math.sin(6.0 * CS_LAT_RAD));
	       wgs84Y = 200000.0 + 1.0 * CS_V * (CS_A + (1.0 - CS_T + CS_C) * Math.pow(CS_A, 3.0) / 6.0 + (5.0 - 18.0 * CS_T + Math.pow(CS_T, 2.0) + 72.0 * CS_C - 0.39092000000000005) * Math.pow(CS_A, 5.0) / 120.0);
	       wgs84X = 500000.0 + 1.0 * (CS_M - 4207498.01915032 + CS_V * Math.tan(CS_LAT_RAD) * (Math.pow(CS_A, 2.0) / 2.0 + (5.0 - CS_T + 9.0 * CS_C + 4.0 * Math.pow(CS_C, 2.0)) * Math.pow(CS_A, 4.0) / 24.0 + (61.0 - 58.0 * CS_T + Math.pow(CS_T, 2.0) + 600.0 * CS_C - 2.2242) * Math.pow(CS_A, 6.0) / 720.0));
	       return wgs84X;
	  }
	  

	function transferGrs80TmCoordX(wgs84X, wgs84Y){
	    double CS_LON_RAD = parseFloat(wgs84Y) * 0.0174532925199433;
	       double CS_LAT_RAD = parseFloat(wgs84X) * 0.0174532925199433;
	       double CS_V = 6378137.0 / Math.pow(1.0 - 0.00669438 * Math.pow(Math.sin(CS_LAT_RAD), 2.0), 0.5);
	       double CS_T = Math.pow(Math.tan(CS_LAT_RAD), 2.0);
	       double CS_C = 0.00674 * Math.pow(Math.cos(CS_LAT_RAD), 2.0);
	       double CS_A = (CS_LON_RAD - 2.2165681500327987) * Math.cos(CS_LAT_RAD);
	       double CS_M = 6378137.0 * ((0.998326405 - 3.0 * Math.pow(0.00669438, 2.0) / 64.0 - 5.0 * Math.pow(0.00669438, 3.0) / 256.0) * CS_LAT_RAD - (0.0025103925 + 3.0 * Math.pow(0.00669438, 2.0) / 32.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(2.0 * CS_LAT_RAD) + (15.0 * Math.pow(0.00669438, 2.0) / 256.0 + 45.0 * Math.pow(0.00669438, 3.0) / 1024.0) * Math.sin(4.0 * CS_LAT_RAD) - 35.0 * Math.pow(0.00669438, 3.0) / 3072.0 * Math.sin(6.0 * CS_LAT_RAD));
	       wgs84Y = 200000.0 + 1.0 * CS_V * (CS_A + (1.0 - CS_T + CS_C) * Math.pow(CS_A, 3.0) / 6.0 + (5.0 - 18.0 * CS_T + Math.pow(CS_T, 2.0) + 72.0 * CS_C - 0.39092000000000005) * Math.pow(CS_A, 5.0) / 120.0);
	       wgs84X = 500000.0 + 1.0 * (CS_M - 4207498.01915032 + CS_V * Math.tan(CS_LAT_RAD) * (Math.pow(CS_A, 2.0) / 2.0 + (5.0 - CS_T + 9.0 * CS_C + 4.0 * Math.pow(CS_C, 2.0)) * Math.pow(CS_A, 4.0) / 24.0 + (61.0 - 58.0 * CS_T + Math.pow(CS_T, 2.0) + 600.0 * CS_C - 2.2242) * Math.pow(CS_A, 6.0) / 720.0));
	       return wgs84Y;
	  }*/

    static public Point convertUTM2WGS(double zone, double easting, double northing, boolean northernHemisphere)
    {
    	    if(northernHemisphere == false)
    	        northing = 10000000 - northing;

    	    double a = 6378137;
    	    double e = 0.081819191;
    	    double e1sq = 0.006739497;
    	    double k0 = 0.9996;

    	    double arc = northing / k0;
    	    double mu = arc / (a * (1f - Math.pow(e, 2f) / 4.0 - 3f * Math.pow(e, 4f) / 64.0 - 5f * Math.pow(e, 6f) / 256.0));

    	    double ei = (1f - Math.pow((1f - e * e), (1f / 2.0))) / (1f + Math.pow((1f - e * e), (1f / 2.0)));

    	    double ca = 3f * ei / 2f - 27f * Math.pow(ei, 3f) / 32.0;

    	    double cb = 21f * Math.pow(ei, 2f) / 16f - 55f * Math.pow(ei, 4f) / 32f;
    	    double cc = 151f * Math.pow(ei, 3f) / 96f;
    	    double cd = 1097f * Math.pow(ei, 4f) / 512f;
    	    double phi1 = mu + ca * Math.sin(2f * mu) + cb * Math.sin(4f * mu) + cc * Math.sin(6f * mu) + cd * Math.sin(8f * mu);

    	    double n0 = a / Math.pow((1f - Math.pow((e * Math.sin(phi1)), 2f)), (1f / 2.0));

    	    double r0 = a * (1f - e * e) / Math.pow((1f - Math.pow((e * Math.sin(phi1)), 2f)), (3f / 2.0));
    	    double fact1 = n0 * Math.tan(phi1) / r0;

    	    double _a1 = 500000f - easting;
    	    double dd0 = _a1 / (n0 * k0);
    	    double fact2 = dd0 * dd0 / 2f;

    	    double t0 = Math.pow(Math.tan(phi1), 2f);
    	    double Q0 = e1sq * Math.pow(Math.cos(phi1), 2f);
    	    double fact3 = (5f + 3f * t0 + 10f * Q0 - 4f * Q0 * Q0 - 9f * e1sq) * Math.pow(dd0, 4f) / 24f;

    	    double fact4 = (61f + 90f * t0 + 298f * Q0 + 45f * t0 * t0 - 252f * e1sq - 3f * Q0 * Q0) * Math.pow(dd0, 6f) / 720f;

    	    double lof1 = _a1 / (n0 * k0);
    	    double lof2 = (1f + 2f * t0 + Q0) * Math.pow(dd0, 3f) / 6.0;
    	    double lof3 = (5f - 2f * Q0 + 28f * t0 - 3f * Math.pow(Q0, 2f) + 8f * e1sq + 24f * Math.pow(t0, 2f)) * Math.pow(dd0, 5f) / 120f;
    	    double _a2 = (lof1 - lof2 + lof3) / Math.cos(phi1);
    	    double _a3 = _a2 * 180f / Math.PI;

    	    double latitude = 180f * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

    	    if(northernHemisphere == false)
    	        latitude = -latitude;

    	    double longitude = ((zone > 0f) ? 6f * zone - 183.0 : 3.0) - _a3;

    	    return new Point(latitude, longitude);
    }
    
}
