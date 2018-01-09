package com.ccz.appinall.services.action.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.type.enums.EMessageType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RecDataMessage {
	public class Msg {
		public String chid;
		public EMessageType eMsgType;
		public String msg;
		
		public Msg(String data) {
			String[] sunit = data.split(ASS.UNIT, -1);
			chid = sunit[0];
			eMsgType = EMessageType.getType(sunit[1]);
			msg = sunit[1];
		}
		public Msg(JsonNode jObj) {
			chid = jObj.get("chid").asText();
			eMsgType = EMessageType.getType(jObj.get("msgtype").asText());
			msg = jObj.get("msg").asText();
		}
	}
	public class SyncMsg {
		public String chid;
		public int offset, count;
		
		public SyncMsg(String data) {
			String[] sunit = data.split(ASS.UNIT, -1);
			chid = sunit[0];
			offset = Integer.parseInt(sunit[1]);
			count = Integer.parseInt(sunit[2]);
		}
		public SyncMsg(JsonNode jObj) {
			chid = jObj.get("chid").asText();
			offset = jObj.get("offset").asInt();
			count = jObj.get("count").asInt();
		}
	}
	
	public class ReadMsg {
		public String chid, msgid;
		public ReadMsg(String data) {
			String[] sunit = data.split(ASS.UNIT, -1);
			chid = sunit[0];
			msgid = sunit[1];
		}
		public ReadMsg(JsonNode jObj) {
			chid = jObj.get("chid").asText();
			msgid = jObj.get("msgid").asText();
		}
	}
	public class DelMsg {
		public String chid;
		public List<String> msgids = new ArrayList<>();
		public DelMsg(String data) {
			String[] sgroup = data.split(ASS.GROUP, -1);
			chid = sgroup[0];
			msgids = Arrays.asList(sgroup[1]);
		}
		public DelMsg(JsonNode jObj) {
			chid = jObj.get("chid").asText();
			ArrayNode jArr = (ArrayNode) jObj.get("msgids");
			for(JsonNode jitem : jArr)
				msgids.add(jitem.asText());
		}
	}
}
