package com.ccz.appinall.services.controller.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.enums.EBoardItemType;
import com.ccz.appinall.services.enums.EBoardPreference;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RecDataBoard {
	public class AddBoard {
		public EBoardItemType itemtype;
		public String title, content; 
		public boolean hasimage, hasfile;
		public String appcode = "";
		private String category;
		@Getter private List<String> fileids;
		public AddBoard(JsonNode jnode) {
			itemtype = EBoardItemType.getType(jnode.get("itemtype").asText());
			title = jnode.get("title").asText();
			content = jnode.get("content").asText(); 
			hasimage = jnode.get("hasimage").asBoolean();
			hasfile = jnode.get("hasfile").asBoolean();
			category = jnode.get("category").asText();
			if(jnode.has("appcode"))
				appcode = jnode.get("appcode").asText();
			
			if(jnode.has("fileids")) {
				fileids = new ArrayList<String>();
				ArrayNode arrNode = (ArrayNode) jnode.get("fileids");
				for(int i=0; i<arrNode.size(); i++)
					fileids.add(arrNode.get(i).asText());
			}
		}
		
		public int getCategoryInex() {
			try {
				return Integer.parseInt(category);
			}catch(Exception e) {
				return 0;
			}
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
	
	public class UpdateBoard {
		public String boardid, title, content, category; 
		public boolean hasimage, hasfile;
		
		public UpdateBoard(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			title = jObj.get("title").asText();
			content = jObj.get("content").asText(); 
			hasimage = jObj.get("hasimage").asBoolean();
			hasfile = jObj.get("hasfile").asBoolean();
			category = jObj.get("category").asText();
		}

	}

	public class BoardList {
		private String category;
		@Getter private int offset, count;
		@Getter private String userid;
		
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
			if(jObj.has("userid"))
				userid = jObj.get("userid").asText();
		}
		
		public int getCategoryInex() {
			return Integer.parseInt(category);
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
		public String boardid;
		public EBoardPreference preference;
		public boolean isadd;
		
		public BoardLike(JsonNode jObj) {
			boardid = jObj.get("boardid").asText();
			preference = EBoardPreference.getType(jObj.get("preference").asText());
			isadd = jObj.get("isadd").asBoolean();
		}

	}
	public class BoardDislike extends BoardLike{
		public BoardDislike(JsonNode jObj) {
			super(jObj);
		}

	}
	public class AddReply {
		public String boardid, parentrepid = "0";
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
		//public boolean isclose;
		//public List<VoteText> itemList = new ArrayList<>();
		public List<String> itemList = new ArrayList<>();
		
		public AddVote(JsonNode jObj) {
			board = new AddBoard(jObj);
			expiretime = jObj.get("expiretime").asLong();
			//isclose = jObj.get("isclose").asBoolean();
			ArrayNode jArr = (ArrayNode) jObj.get("voteitems");
			for(JsonNode jItem : jArr)
				//itemList.add(new VoteText(jItem.get("votetext").asText(), jItem.get("voteurl").asText()));
				itemList.add(jItem.asText());
			}
		}
		
//		public class VoteText {
//			public String votetext, voteurl;
//			
//			public VoteText(String votetext, String voteurl) {
//				this.votetext = votetext;
//				this.voteurl = voteurl;
//			}
//		}

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

	@Data
	@NoArgsConstructor
	public class VoteUpdate {
		private String boardid;
		private Long expiretime;
		private Boolean isclose;
		private List<VoteItemData> voteitems;
		
		public VoteUpdate(String json) throws JsonParseException, JsonMappingException, IOException {
			ObjectMapper objectMapper = new ObjectMapper();
			VoteUpdate obj = (VoteUpdate) objectMapper.readValue(json, VoteUpdate.class);
			this.boardid = obj.boardid;
			this.expiretime = obj.expiretime;
			this.isclose = obj.isclose;
			this.voteitems = obj.voteitems;
		}
	}
	
	@Data
	public class VoteItemData {
		public VoteItemData() {		}
		public String vitemid;
		public String votetext;
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
