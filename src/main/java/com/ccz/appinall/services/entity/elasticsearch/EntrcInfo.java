package com.ccz.appinall.services.entity.elasticsearch;

import lombok.Getter;

public class EntrcInfo {
	@Getter
	private String id;
	public String dongcode, rcode, buildno, buildsubno, base;
	public double x, y;
	
	@Getter
	private String lactitude, longitude;
	@Getter
	private double lac, lon;
	
	public String sicode, entrance, roadname, buildname;
	
	public EntrcInfo() {}
	
	public EntrcInfo(String dongcode, String rcode, String buildno, String buildsubno, String base, double x, double y) {
		this.dongcode = dongcode;
		this.rcode = rcode;
		this.buildno = buildno;
		this.buildsubno = buildsubno;
		this.base = base;
		this.x = x;
		this.y = y;
		makeId();
	}
	
	public void makeId() {
		this.id = dongcode + rcode +"-"+ buildno +"-"+ buildsubno +"-"+ base;
	}
	
	public void setCoordinate(String coord) {	//127d8'25.272"E	37d33'17.808"N 0.000
		String[] sp = coord.split(" ", -1);
		if(sp.length<2)
			return;
		String[] coords = sp[0].split("\t", -1);
		lactitude = coords[0];
		longitude = coords[1];
		lac = Double.parseDouble(lactitude.replace(".", "").replace("'", "").replace("\"", "").replace("E", "").replace("d", "."));
		lon = Double.parseDouble(longitude.replace(".", "").replace("'", "").replace("\"", "").replace("N", "").replace("d", "."));
	}
}
