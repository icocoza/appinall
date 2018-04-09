package com.ccz.appinall.services.controller.friend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EFriendStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RecDataFriend {
	public class AddFriend {
		public String friendtype;
		public List<IdName> friendlist = new ArrayList<>();
		
		public AddFriend(String data) {
			String[] sgroup = data.split(ASS.GROUP, -1);
			friendtype = sgroup[0];
			String[] srecord = sgroup[1].split(ASS.RECORD, -1);
			for(String idname : srecord) {
				String[] sunit = idname.split(ASS.UNIT, -1);
				friendlist.add(new IdName(sunit[0], sunit[1]));
			}
		}
		public AddFriend(JsonNode jObj) {
			friendtype = jObj.get("friendtyype").asText();
			ArrayNode jArr = (ArrayNode) jObj.get("friendlist");
			for(JsonNode item : jArr) {
				friendlist.add(new IdName(item.get("userid").asText(), item.get("username").asText()));
			}
		}
	}
	
	public class IdName {
		public String userid;
		public String username;
		
		public IdName(String id, String name) {
			this.userid = id;
			this.username = name;
		}
	}
	
	public class DelFriend {
		List<String> friendids;
		public DelFriend(String data) {
			String[] srecord = data.split(ASS.RECORD, -1);
			friendids = Arrays.asList(srecord);
		}
		public DelFriend(JsonNode jObj) {
			friendids = new ArrayList<>();
			ArrayNode jArr = (ArrayNode) jObj.get("friendids");
			for(JsonNode jnode : jArr)
				friendids.add(jnode.asText());
		}
	}
	public class ChangeFriendStatus {
		List<IdStatus> friendstatus;
		public ChangeFriendStatus(String data) {
			String[] srecord = data.split(ASS.RECORD, -1);	//[id][status]
			friendstatus = Arrays.asList(srecord).stream().map(e->e.split(ASS.UNIT, -1)).map(s->new IdStatus(s[0], s[1])).collect(Collectors.toList());
		}
		public ChangeFriendStatus(JsonNode jObj) {
			ArrayNode jArr = (ArrayNode) jObj.get("friendstatus");
			for(JsonNode jitem : jArr) {
				friendstatus.add(new IdStatus(jitem.get("userid").asText(), jitem.get("status").asText()));
			}
		}
	}
	public class IdStatus {
		public String userid;
		public EFriendStatus estatus;
		
		public IdStatus(String userid, String status) {
			this.userid = userid;
			this.estatus = EFriendStatus.getType(status);
		}
	}

	public class FriendCount {
		public EFriendStatus estatus;
		
		public FriendCount(JsonNode jObj) {
			estatus = EFriendStatus.getType(jObj.get("status").asText());
		}
	}

	public class Friendids {
		public EFriendStatus estatus;
		public int offset, count;
		
		public Friendids(String data) {
			String[] sunit = data.split(ASS.UNIT);
			estatus = EFriendStatus.getType(sunit[0]);
			offset = Integer.parseInt(sunit[1]);
			count = Integer.parseInt(sunit[2]);
		}
		public Friendids(JsonNode jObj) {
			estatus = EFriendStatus.getType(jObj.get("status").asText());
			offset = jObj.get("offset").asInt();
			count = jObj.get("count").asInt();
		}
	}

	public class FriendInfos extends DelFriend {
		public FriendInfos(String data) {
			super(data);
		}
		public FriendInfos(JsonNode jObj) {
			super(jObj);
		}
	}
	public class AppendMe {
		public int offset, count;
		public EFriendStatus estatus = EFriendStatus.friend;
		
		public AppendMe(String data) {
			String[] sunit = data.split(ASS.UNIT);
			offset = Integer.parseInt(sunit[0]);
			count = Integer.parseInt(sunit[1]);
		}
		public AppendMe(JsonNode jObj) {
			offset = jObj.get("offset").asInt();
			count = jObj.get("count").asInt();
		}
	}
	public class BlockMe extends AppendMe{
		public BlockMe(String data) {
			super(data);
			estatus = EFriendStatus.block;
		}
		public BlockMe(JsonNode jObj) {
			super(jObj);
			estatus = EFriendStatus.block;
		}
	}

}
