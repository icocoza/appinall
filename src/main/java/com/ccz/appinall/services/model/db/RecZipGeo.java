package com.ccz.appinall.services.model.db;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;

import lombok.Getter;

public class RecZipGeo extends DbRecord {

	public static final String TBL_NAME = "zipgeo";
	
	public String buildid; 	
	public String geo;		

	@Getter private Point geoPoi;
	
	public RecZipGeo(String poolName) {
		super(poolName);
	}
	//SELECT buildid, ST_AsText(geo) FROM zipgeo WHERE ST_Contains( ST_MakeEnvelope(Point(127.139807+0.001, 37.555790+0.001),Point(127.139807-0.001, 37.555790-0.001)), geo);
	public boolean createTable() {
		String sql = String.format("CREATE TABLE %s(buildid VARCHAR(32) PRIMARY KEY NOT NULL, geo GEOMETRY NOT NULL, SPATIAL KEY geo (geo) ) ENGINE=MYISAM", RecZipGeo.TBL_NAME);
		return super.createTable(sql);
	}
	
	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecZipGeo rec = (RecZipGeo)r;
		rec.buildid = rd.getString("buildid");
		rec.geo = rd.getString("geotext");
		//POINT(37.352902 126.585147)
		String[] point = rec.geo.replace("POINT(", "").replace(")", "").split(" ", -1);
		geoPoi = new Point(Double.parseDouble(point[1]), Double.parseDouble(point[0]));
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecZipGeo(poolName));
	}

	public RecZipGeo getPoi(String buildid) {
		String sql = String.format("SELECT buildid, ST_AsText(geo) as geo FROM %s WHERE buildid='%s'", RecZipGeo.TBL_NAME, buildid);
		return (RecZipGeo) super.getOne(sql);
	}
	
	public List<RecZipGeo> getPoiByGps(long longitude, long latitude) {
		String sql = String.format("SELECT buildid, ST_AsText(geo) as geotext FROM %s WHERE ST_Distance_Sphere(geo, POINT(%d, %d) < 0.004", RecZipGeo.TBL_NAME, latitude, longitude);
		return super.getList(sql).stream().map(e->(RecZipGeo)e).collect(Collectors.toList());
	}
	/*
	 * 
	 *  SELECT name,
		ST_Distance_Sphere(coordinates, POINT(48.861105, 2.335337))
		FROM places
		
		SELECT name FROM places
WHERE ST_Distance_Sphere(coordinates, POINT(48.861105, 2.335337)) < 10000

	 * SELECT * FROM zipgeo WHERE ST_Within
	 * SELECT ST_Within(ST_GEOMFROMTEXT('POINT(100.52438735961914 13.748889613522605)'),
            ST_GEOMFROMTEXT('POLYGON((100.49503326416016 13.766897133254545, 100.55940628051758 13.746555203977,100.56266784667969 13.72170897580617, 100.48885345458984 13.739051587150175,
            100.49503326416016 13.766897133254545))'))
			As geoFenceStatus*/
	
}
