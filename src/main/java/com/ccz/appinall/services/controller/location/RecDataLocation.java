package com.ccz.appinall.services.controller.location;

import com.ccz.appinall.services.controller.RecDataCommon;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataLocation {
	
	@Getter
	public class GpsInfo extends RecDataCommon {
		private double lat, lon;
		private int dir, speed;
		private String status; //in rest, driving, jam..etc...
		
		public GpsInfo(JsonNode jnode) {
			super(jnode);
			this.lat = jnode.get("lat").asDouble();
			this.lon = jnode.get("lon").asDouble();
			this.dir = jnode.get("dir").asInt();
			this.speed = jnode.get("speed").asInt();
			this.status = jnode.get("status").asText();
		}
	}
	
	@Getter
	public class JoinChannel extends RecDataCommon {
		private String deliverid;
		private String orderid;
		
		public JoinChannel(JsonNode jnode) {
			super(jnode);
			this.deliverid = jnode.get("deliverid").asText();
			this.orderid = jnode.get("orderid").asText();
		}

	}
	
	@Getter
	public class LeaveChannel extends JoinChannel {

		public LeaveChannel(JsonNode jnode) {
			super(jnode);
		}

	}
}
