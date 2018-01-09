package com.ccz.appinall.services.action.board;

import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RecDataBoard {
	public class AddBoard {
		public String itemtype, title, content; 
		public boolean hasimage, hasfile;
		public String category, appcode;
		
		public AddBoard(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			itemtype = sarray[0];
			title = sarray[1];
			content = sarray[2]; 
			hasimage = Boolean.parseBoolean(sarray[3]);
			hasfile = Boolean.parseBoolean(sarray[4]);
			category = sarray[5];
			appcode = sarray[6];
		}
		public AddBoard(JsonNode jObj) {
			itemtype = jObj.get("itemtype").asText();
			title = jObj.get("title").asText();
			content = jObj.get("content").asText(); 
			hasimage = jObj.get("hasimage").asBoolean();
			hasfile = jObj.get("hasfile").asBoolean();
			category = jObj.get("category").asText();
			appcode = jObj.get("appcode").asText();
		}
	}
	public class DelBoard {
		public String boardid;
		
		public DelBoard(String data) {
			boardid = data;
		}
		public DelBoard(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
		}

	}
	public class UpdateBoardTitle {
		String boardid, title;
		
		public UpdateBoardTitle(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			title = sarray[1];
		}
		public UpdateBoardTitle(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			title = jObj.get("title").asText();
		}

	}
	public class UpdateBoardContent {
		public String boardid, content; 
		public boolean hasimage, hasfile;
		
		public UpdateBoardContent(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			content = sarray[1]; 
			hasimage = Boolean.parseBoolean(sarray[2]);
			hasfile = Boolean.parseBoolean(sarray[3]);
			
		}
		public UpdateBoardContent(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			content = jObj.get("content").asText(); 
			hasimage = jObj.get("hasimage").asBoolean();
			hasfile = jObj.get("hasfile").asBoolean();
		}

	}
	public class UpdateBoardCategory {
		public String boardid, category;
		
		public UpdateBoardCategory(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			category = sarray[1]; 
		}
		public UpdateBoardCategory(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			category = jObj.get("category").asText(); 
		}

	}
	public class BoardList {
		public String category;
		public int offset, count;
		public String userid;
		
		public BoardList(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			category = sarray[0];
			offset = Integer.parseInt(sarray[1]);
			count = Integer.parseInt(sarray[2]);
			if(sarray.length>3)
				userid = sarray[3];
		}
		public BoardList(JsonNode jObj) {
			category = jObj.get("category").asText();
			offset = jObj.get("offset").asInt(); 
			count = jObj.get("count").asInt(); 
			userid = jObj.get("userid").asText();
		}
	}
	public class BoardContent extends DelBoard{
		public BoardContent(String data) {
			super(data);
		}
		public BoardContent(JsonNode jObj) {
			super(jObj);
		}

	}
	public class BoardLike {
		public String boardid, preference;
		public boolean isadd;
		
		public BoardLike(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			preference = sarray[1];
			isadd = Boolean.parseBoolean(sarray[2]);
		}
		public BoardLike(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			preference = jObj.get("preference").asText();
			isadd = jObj.get("isadd").asBoolean();
		}

	}
	public class BoardDislike extends BoardLike{
		public BoardDislike(String data) {
			super(data);
		}
		public BoardDislike(JsonNode jObj) {
			super(jObj);
		}

	}
	public class AddReply {
		public String boardid, parentrepid;
		public int depth;
		public String msg;
		
		public AddReply(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			parentrepid = sarray[1];
			depth = Integer.parseInt(sarray[2]);
			msg = sarray[3];
		}
		public AddReply(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			parentrepid = jObj.get("parentrepid").asText();
			depth = jObj.get("depth").asInt(); 
			msg = jObj.get("msg").asText();
		}
	}
	public class DelReply {
		public String boardid, replyid;
		public DelReply(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			replyid = sarray[1];
		}
		public DelReply(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			replyid = jObj.get("replyid").asText();
		}

	}
	public class ReplyList {
		public String boardid;
		public int offset, count;

		public ReplyList(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			offset = Integer.parseInt(sarray[1]);
			count = Integer.parseInt(sarray[2]);
		}
		public ReplyList(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			offset = jObj.get("offset").asInt(); 
			count = jObj.get("count").asInt(); 
		}

	}
	public class AddVote {
		public AddBoard board;
		public long expiretime;
		public boolean isclose;
		public List<VoteText> itemList = new ArrayList<>();
		
		public AddVote(String data) {
			String[] sarray = data.split(ASS.GROUP, -1);
			board = new AddBoard(sarray[0]);
			String[] second = sarray[1].split(ASS.UNIT);
			expiretime = Long.parseLong(second[0]);
			isclose = Boolean.parseBoolean(second[1]);
			String[] third = sarray[2].split(ASS.RECORD, -1);
			for(String vote : third) {
				String[] svote = vote.split(ASS.UNIT, -1);
				itemList.add(new VoteText(svote[0], svote[1]));
			}
		}
		public AddVote(JsonNode jObj) {
			board = new AddBoard(jObj);
			expiretime = jObj.get("expiretime").asLong();
			isclose = jObj.get("isclose").asBoolean();
			ArrayNode jArr = (ArrayNode) jObj.get("voteitem");
			for(JsonNode jItem : jArr)
				itemList.add(new VoteText(jItem.get("votetext").asText(), jItem.get("voteurl").asText()));
			}
		}
		
		public class VoteText {
			public String votetext, voteurl;
			
			public VoteText(String votetext, String voteurl) {
				this.votetext = votetext;
				this.voteurl = voteurl;
			}
		}

	public class SelectVote {
		public String boardid, vitemid;
		public boolean isselect;
		
		public SelectVote(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			vitemid = sarray[1];
			isselect = Boolean.parseBoolean(sarray[2]);
		}
		
		public SelectVote(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			vitemid = jObj.get("vitemid").asText();
			isselect = jObj.get("isselect").asBoolean();
		}

	}
	public class VoteItemList extends DelBoard{
		public VoteItemList(String data) {
			super(data);
		}
		public VoteItemList(JsonNode jObj) {
			super(jObj);
		}

	}
	public class VoteUpdate {
		public String boardid, type, value;
		
		public VoteUpdate(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			type = sarray[1];
			value = sarray[2];
		}
		public VoteUpdate(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			type = jObj.get("type").asText();
			value = jObj.get("value").asText();
		}

	}
	public class VoteItem {
		public String boardid, vitemid, type, value;
		public VoteItem(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			vitemid = sarray[1];
			type = sarray[2];
			value = sarray[3];
		}
		public VoteItem(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			vitemid = jObj.get("vitemid").asText();
			type = jObj.get("type").asText();
			value = jObj.get("value").asText();
		}

	}
	public class ChangeVoteSelection {
		public String boardid, vitemid;
		public ChangeVoteSelection(String data) {
			String[] sarray = data.split(ASS.UNIT, -1);
			boardid = sarray[0];
			vitemid = sarray[1];
		}
		public ChangeVoteSelection(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			vitemid = jObj.get("vitemid").asText();
		}

	}
	public class VoteInfoList {
		List<String> boardids = new ArrayList<>();
		public VoteInfoList(String data) {
			String[] records = data.split(ASS.RECORD, -1);
			for(String record : records)
				boardids.add(record);
		}
		public VoteInfoList(JsonNode jObj) {
			ArrayNode jArr = (ArrayNode) jObj.get("boardids");
			for(Object jItem : jArr)
				boardids.add((String)jItem);
		}

	}
}
