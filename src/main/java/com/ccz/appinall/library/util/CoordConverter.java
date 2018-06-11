package com.ccz.appinall.library.util;

public class CoordConverter {
	/**
	 * SK 좌표를 WGS84(GPS) 좌표로 변환한다.
	 * 
	 * @param nWorld
	 * 
	 * @return 성공 double[2], 실패 null
	 */
	public static double[] SK2WGS84(int[] nSk) {
		if (nSk != null && nSk.length > 1)
			return SK2WGS84(nSk[0], nSk[1]);

		return null;
	}

	/**
	 * SK 좌표를 WGS84(GPS) 좌표로 변환한다.
	 * 
	 * @param nSkX
	 * @param nSkY
	 * 
	 * @return 성공 double[2], 실패 null
	 */
	public static double[] SK2WGS84(int nSkX, int nSkY) {
		if (nSkX > 0 && nSkY > 0) {
			double[] dWgs84X = new double[1];
			double[] dWgs84Y = new double[1];

			// SK To Bessel
			double besselX, besselY;
			if (nSkX / 10000000 > 0 && nSkY / 10000000 > 0) {
				// SK To Bessel
				besselX = (double) ((double) nSkX / 360000.f);
				besselY = (double) ((double) nSkY / 360000.f);
			} else {
				// SK To Bessel
				besselX = (double) ((double) nSkX / 36000.f);
				besselY = (double) ((double) nSkY / 36000.f);
			}

			// WGS To Bessel
			Bessel2Wgs84(besselX, besselY, dWgs84X, dWgs84Y);

			return new double[] { dWgs84X[0], dWgs84Y[0] };
		}

		return null;
	}

	/**
	 * WGS84(GPS) 좌표를 SK 좌표로 변환한다.
	 * 
	 * @param dLonLat
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WGS842SK(double[] dLonLat) {
		if (dLonLat != null && dLonLat.length > 1)
			return WGS842SK(dLonLat[0], dLonLat[1]);

		return null;
	}

	/**
	 * WGS84(GPS) 좌표를 SK 좌표로 변환한다.
	 * 
	 * @param dLongitude
	 * @param dLatitude
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WGS842SK(double dLongitude, double dLatitude) {
		if (dLongitude > 0 && dLatitude > 0) {
			double[] dBesselX = new double[1];
			double[] dBesselY = new double[1];

			// WGS To Bessel
			Wgs842Bessel(dLongitude, dLatitude, dBesselX, dBesselY);

			// Bessel To SK
			return new int[] { (int) (dBesselX[0] * 36000.f), (int) (dBesselY[0] * 36000.f) };
		}

		return null;
	}

	/**
	 * 월드좌표를 WGS84(GPS) 좌표로 변환한다.
	 * 
	 * @param nWorld
	 * 
	 * @return 성공 double[2], 실패 null
	 */
	public static double[] WORLD2WGS84(int[] nWorld) {
		if (nWorld != null && nWorld.length > 1)
			return WORLD2WGS84(nWorld[0], nWorld[1]);

		return null;
	}

	/**
	 * 월드좌표를 WGS84(GPS) 좌표로 변환한다.
	 * 
	 * @param nWorldX
	 * @param nWorldY
	 * 
	 * @return 성공 double[2], 실패 null
	 */
	public static double[] WORLD2WGS84(int nWorldX, int nWorldY) {
		if (nWorldX > 0 && nWorldY > 0) {
			double[] dWgs84X = new double[1];
			double[] dWgs84Y = new double[1];
			double[] dBesselX = new double[1];
			double[] dBesselY = new double[1];

			// World To Bessel
			Bessel2Tile(nWorldX, nWorldY, dBesselX, dBesselY);

			// WGS To Bessel
			Bessel2Wgs84(dBesselX[0], dBesselY[0], dWgs84X, dWgs84Y);

			return new double[] { dWgs84X[0], dWgs84Y[0] };
		}

		return null;
	}

	/**
	 * WGS84(GPS) 좌표를 월드좌표로 변환한다.
	 * 
	 * @param dLonLat
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WGS842WORLD(double[] dLonLat) {
		if (dLonLat != null && dLonLat.length > 1)
			return WGS842WORLD(dLonLat[0], dLonLat[1]);

		return null;
	}

	/**
	 * WGS84(GPS) 좌표를 월드좌표로 변환한다.
	 * 
	 * @param dLongitude
	 * @param dLatitude
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WGS842WORLD(double dLongitude, double dLatitude) {
		if (dLongitude > 0 && dLatitude > 0) {

			int[] nWorldX = new int[1];
			int[] nWorldY = new int[1];
			double[] dBesselX = new double[1];
			double[] dBesselY = new double[1];

			// WGS To Bessel
			Wgs842Bessel(dLongitude, dLatitude, dBesselX, dBesselY);

			// Bessel To World
			Tile2Bessel(dBesselX[0], dBesselY[0], nWorldX, nWorldY);

			return new int[] { nWorldX[0], nWorldY[0] };
		}

		return null;
	}

	/**
	 * 월드좌표를 SK 좌표로 변환한다.
	 * 
	 * @param nWorld
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WORLD2SK(int nWorld[]) {
		if (nWorld != null && nWorld.length > 1)
			return WORLD2SK(nWorld[0], nWorld[1]);

		return null;
	}

	/**
	 * 월드좌표를 SK 좌표로 변환한다.
	 * 
	 * @param nWorldX
	 * @param nWorldY
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] WORLD2SK(int nWorldX, int nWorldY) {
		if (nWorldX > 0 && nWorldY > 0) {
			int[] nSK = new int[2];
			nSK[0] = (int) ((nWorldX / 256.f / 2048.f) * 36000.f);
			nSK[1] = (int) ((nWorldY / 256.f / 2048.f) * 36000.f);

			return nSK;
		}

		return null;
	}

	/**
	 * 월드좌표를 SK 좌표로 변환한다.
	 * 
	 * @param nWorld
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] SK2WORLD(int nSk[]) {
		if (nSk != null && nSk.length > 1)
			return SK2WORLD(nSk[0], nSk[1]);

		return null;
	}

	/**
	 * SK 좌표를 월드좌표로 변환한다.
	 * 
	 * @param nSkX
	 * @param nSkY
	 * 
	 * @return 성공 int[2], 실패 null
	 */
	public static int[] SK2WORLD(int nSkX, int nSkY) {
		if (nSkX > 0 && nSkY > 0) {
			int[] nWorld = new int[2];
			if (nSkX / 10000000 > 0 && nSkY / 10000000 > 0) {
				nWorld[0] = (int) ((nSkX / 360000.f) * 256.f * 2048.f + 0.5f);
				nWorld[1] = (int) ((nSkY / 360000.f) * 256.f * 2048.f + 0.5f);
			} else {
				nWorld[0] = (int) ((nSkX / 36000.f) * 256.f * 2048.f + 0.5f);
				nWorld[1] = (int) ((nSkY / 36000.f) * 256.f * 2048.f + 0.5f);
			}

			return nWorld;
		}

		return null;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Private
	// ///////////////////////////////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////
	// Bessel
	private static final double BESSEL_A = 6377397.155;
	private static final double BESSEL_RF = 299.1528128;
	private static final double BESSEL_B = BESSEL_A - BESSEL_A / BESSEL_RF;
	private static final int B2W_DELTAX = -147;
	private static final int B2W_DELTAY = 506;
	private static final int B2W_DELTAZ = 687;

	// ////////////////////////////////////////////////////////////////
	// W2B
	private static final double W2B_DELTAX = 128;
	private static final double W2B_DELTAY = -481;
	private static final double W2B_DELTAZ = -664;
	private static final double W2B_DELTAA = -739.845;
	private static final double W2B_DELTAF = -0.000010037483;

	// ////////////////////////////////////////////////////////////////
	// WGS84
	private static final double WGS84_A = 6378137.0;
	private static final double WGS84_F = 1.0 / 298.257223563;
	private static final double WGS84_RF = 298.257223563;
	private static final double WGS84_B = WGS84_A - WGS84_A / WGS84_RF;
	private static final double WGS84_EE = 2.0 * WGS84_F - WGS84_F * WGS84_F;

	private static void Wgs842Bessel(double wx, double wy, double[] pbx, double[] pby) {
		double rn, rm, d_pi, d_lamda;
		double h, pwx, pwy;

		h = 0.0;
		pbx[0] = pwx = wx;
		pby[0] = pwy = wy;
		Deg2Rad(pbx, pby);
		wx = pbx[0];
		wy = pby[0];
		pbx[0] = pwx;
		pby[0] = pwy;

		rn = WGS84_A / Math.sqrt(1 - WGS84_EE * Math.pow(Math.sin(wy), 2.));
		rm = WGS84_A * (1 - WGS84_EE) / Math.pow(Math.sqrt(1 - WGS84_EE * Math.pow(Math.sin(wy), 2.)), 3.);
		d_pi = (-W2B_DELTAX * Math.sin(wy) * Math.cos(wx) - W2B_DELTAY * Math.sin(wy) * Math.sin(wx) + W2B_DELTAZ
				* Math.cos(wy) + W2B_DELTAA * (rn * WGS84_EE * Math.sin(wy) * Math.cos(wy)) / WGS84_A + W2B_DELTAF
				* (rm * WGS84_A / WGS84_B + rn * WGS84_B / WGS84_A) * Math.sin(wy) * Math.cos(wy))
				/ ((rm + h) * Math.sin(Math.PI / 180. * 1 / 3600.));
		d_lamda = (-W2B_DELTAX * Math.sin(wx) + W2B_DELTAY * Math.cos(wx))
				/ ((rn + h) * Math.cos(wy) * Math.sin(Math.PI / 180. * 1 / 3600.));

		pbx[0] += d_lamda / 3600.0;
		pby[0] += d_pi / 3600.0;
	}

	private static void Bessel2Wgs84(double bx, double by, double[] pwx, double[] pwy) {
		double[] X = new double[1];
		double[] Y = new double[1];
		double[] Z = new double[1];
		double[] H = new double[1];

		Geod2ECEF(by, bx, 0., X, Y, Z, BESSEL_A, BESSEL_B);
		X[0] += B2W_DELTAX;
		Y[0] += B2W_DELTAY;
		Z[0] += B2W_DELTAZ;
		ECEF2Geod(X[0], Y[0], Z[0], pwy, pwx, H, WGS84_A, WGS84_B);
	}

	private static void Tile2Bessel(double dBesselX, double dBesselY, int[] nTileX, int[] nTileY) {
		nTileX[0] = (int) (dBesselX * 256.f * 2048.f + 0.5f);
		nTileY[0] = (int) (dBesselY * 256.f * 2048.f + 0.5f);
	}

	private static void Bessel2Tile(int nTileX, int nTileY, double[] dBesselX, double[] dBesselY) {
		dBesselX[0] = (double) (nTileX / 256.f / 2048.f);
		dBesselY[0] = (double) (nTileY / 256.f / 2048.f);
	}

	private static void Deg2Rad(double ptx[], double pty[]) {
		ptx[0] *= (Math.PI / 180.0);
		pty[0] *= (Math.PI / 180.0);
	}

	private static void Geod2ECEF(double lat, double lon, double hei, double[] px, double[] py, double[] pz, double a,
			double b) {
		double lat_r = lat * Math.PI / 180.;
		double lon_r = lon * Math.PI / 180.;

		double f = (a - b) / a;
		double sqre = 2 * f - f * f;
		double N = a / Math.sqrt(1 - sqre * Math.sin(lat_r) * Math.sin(lat_r));

		px[0] = (N + hei) * Math.cos(lat_r) * Math.cos(lon_r);
		py[0] = (N + hei) * Math.cos(lat_r) * Math.sin(lon_r);
		pz[0] = (N * (1 - sqre) + hei) * Math.sin(lat_r);
	}

	private static void ECEF2Geod(double x, double y, double z, double plat[], double plon[], double phei[], double a,
			double b) {
		double p = Math.sqrt(x * x + y * y);
		double theta = Math.atan((z * a) / (p * b));
		double sqrep = (a * a - b * b) / (b * b);

		double f = (a - b) / a;
		double sqre = 2 * f - f * f;

		double lat_r = Math.atan((z + sqrep * b * Math.sin(theta) * Math.sin(theta) * Math.sin(theta))
				/ (p - sqre * a * Math.cos(theta) * Math.cos(theta) * Math.cos(theta)));
		double lon_r = Math.atan2(y, x);

		plat[0] = lat_r * 180. / Math.PI;
		plon[0] = lon_r * 180. / Math.PI;
		phei[0] = p / Math.cos(lat_r) - a / Math.sqrt(1 - sqre * Math.sin(lat_r) * Math.sin(lat_r));
	}
}
