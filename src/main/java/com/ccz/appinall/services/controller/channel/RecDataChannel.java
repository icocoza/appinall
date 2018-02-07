package com.ccz.appinall.services.controller.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RecDataChannel {
	public class ChCreate {
		
		public List<String> attendees;
		
		public ChCreate(String data) {
			String[] sarray = data.split(ASS.RECORD, -1);
			attendees = Arrays.asList(sarray);
		}
		public ChCreate(JsonNode jObj) {
			attendees = new ArrayList<>();
			
			ArrayNode jArr = (ArrayNode)jObj.get("attendees");
			for(JsonNode jitem : jArr)
				attendees.add(jitem.asText());
		}
	}
	public class ChExit { 
		public String chid;
		public ChExit(String data) {
			chid = data;
		}
		public ChExit(JsonNode jObj) {
			chid = jObj.get("chid").asText();
		}
		
	}
	public class ChEnter extends ChExit{
		public ChEnter(String data) {
			super(data);
		}
		public ChEnter(JsonNode jObj) {
			super(jObj);
		}
		
	}
	public class ChInvite{
		public String chid;
		public List<String> attendees;
		
		public ChInvite(String data) {
			String[] sgroup = data.split(ASS.GROUP, -1);
			chid = sgroup[0];
			String[] srecord = sgroup[1].split(ASS.RECORD, -1);
			attendees = Arrays.asList(srecord);
		}
		public ChInvite(JsonNode jObj) {
			chid = jObj.get("chid").asText();
			attendees = new ArrayList<>();
			
			ArrayNode jArr = (ArrayNode) jObj.get("attendees");
			for(JsonNode jitem : jArr)
				attendees.add(jitem.asText());
		}
	}
	
	public class ChMime {
		public int offset, count;
		public ChMime(String data) {
			String[] sunit = data.split(ASS.UNIT, -1);
			offset = Integer.parseInt(sunit[0]);
			count = Integer.parseInt(sunit[1]);
		}
		public ChMime(JsonNode jObj) {
			offset = jObj.get("offset").asInt();
			count = jObj.get("count").asInt();
		}
		
	}
	public class ChLastMsg {
		public List<String> chids;
		public ChLastMsg(String data) {
			String[] srecord = data.split(ASS.RECORD, -1);
			chids = Arrays.asList(srecord);
		}
		public ChLastMsg(JsonNode jObj) {
			chids = new ArrayList<>();
			ArrayNode jArr = (ArrayNode) jObj.get("chids");
			for(JsonNode jitem : jArr)
				chids.add(jitem.asText());
		}
		
	}
	public class ChInfo extends ChMime{
		public ChInfo(String data) {
			super(data);
		}
		public ChInfo(JsonNode jObj) {
			super(jObj);
		}
		
	}
}
