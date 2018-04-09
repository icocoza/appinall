package com.ccz.appinall.services.controller.board;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.board.RecDataBoard.*;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.EBoardCmd;
import com.ccz.appinall.services.enums.EBoardError;
import com.ccz.appinall.services.model.db.RecBoard;
import com.ccz.appinall.services.model.db.RecBoardReply;
import com.ccz.appinall.services.model.db.RecVote;
import com.ccz.appinall.services.model.db.RecVoteInfo;
import com.ccz.appinall.services.model.db.RecVoteUser;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class BoardCommandAction extends CommonAction {
	
	public BoardCommandAction() {
		super.setCommandFunction(EBoardCmd.addboard.getValue(), doAddBoard); //O
		super.setCommandFunction(EBoardCmd.delboard.getValue(), doDelBoard);//O
		super.setCommandFunction(EBoardCmd.updatetitle.getValue(), doUpdateBoardTitle);//O
		super.setCommandFunction(EBoardCmd.updatecontent.getValue(), doUpdateBoardContent);//O
		super.setCommandFunction(EBoardCmd.updatecategory.getValue(), doUpdateBoardCategory);//O
		super.setCommandFunction(EBoardCmd.boardlist.getValue(), doGetBoardList);//O
		super.setCommandFunction(EBoardCmd.boardcontent.getValue(), doGetBoardContent);//O
		super.setCommandFunction(EBoardCmd.like.getValue(), incBoardLike);//O
		super.setCommandFunction(EBoardCmd.dislike.getValue(), incBoardDislike);//O
		super.setCommandFunction(EBoardCmd.addreply.getValue(), addReply); //O
		super.setCommandFunction(EBoardCmd.delreply.getValue(), delReply);//O
		super.setCommandFunction(EBoardCmd.replylist.getValue(), getReplyList);//O
		super.setCommandFunction(EBoardCmd.vote.getValue(), addVote);//O
		super.setCommandFunction(EBoardCmd.selvote.getValue(), selectVoteItem);//O
		super.setCommandFunction(EBoardCmd.voteitemlist.getValue(), getVoteItemList);//O
		super.setCommandFunction(EBoardCmd.voteupdate.getValue(), updateVoteInfo);//O
		super.setCommandFunction(EBoardCmd.voteitem.getValue(), updateVoteItem);//O
		super.setCommandFunction(EBoardCmd.changeselection.getValue(), changeVoteSelection);//O
		super.setCommandFunction(EBoardCmd.voteinfolist.getValue(), getVoteInfoList);//O
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EBoardError> res = new ResponseData<EBoardError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EBoardError>) cmdFunc.doAction(session, res, jdata);
			send(ch, res.toString());
			return true;
		}
		return false;
	}

	/** 
	 * add new article
	 * @param ch
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode]
	 * 					item type => "board"
	 * @return 
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doAddBoard = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		AddBoard data = new RecDataBoard().new AddBoard(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		return addBoard(ss, res, data);
	};
	
	private String getShortContent(String content) {
		if(content.length()<64)
			return content;
		return content.substring(0, 64);
	}
	
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doDelBoard = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		DelBoard data = new RecDataBoard().new DelBoard(jnode); 
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), data.boardid)==false)
			return res.setError(EBoardError.eFailDeleteBoard);
		return res.setError(EBoardError.eOK);
	};

	/** 
	 * update board's title
	 * @param ss: session handle
	 * @param res : response data
	 * @param eCmd : command enum
	 * @param userData, [board id], [title]
	 * @return eFailUpdate if failed, else eOK
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doUpdateBoardTitle = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		UpdateBoardTitle data = new RecDataBoard().new UpdateBoardTitle(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardTitle(ss.scode, ss.getUserId(), data.boardid, data.title)==false)
			return res.setError(EBoardError.eFailUpdate);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	};
	
	/** 
	 * update board's content
	 * @param ss
	 * @param res
	 * @param userData, [board id][content][hasimage][hasfile]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doUpdateBoardContent = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		UpdateBoardContent data = new RecDataBoard().new UpdateBoardContent(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardShortContent(ss.scode, ss.getUserId(), data.boardid, getShortContent(data.content), data.hasimage, data.hasfile)==false)
			return res.setError(EBoardError.eFailUpdate);
		DbAppManager.getInst().updateBoardContent(ss.scode, data.boardid, data.content);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	};

	/** 
	 * update board category
	 * @param ss
	 * @param res
	 * @param userData, [board id][category]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doUpdateBoardCategory = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		UpdateBoardCategory data = new RecDataBoard().new UpdateBoardCategory(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardCategory(ss.scode, ss.getUserId(), data.boardid, data.category)==false)
			return res.setError(EBoardError.eFailUpdate);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	};

	/** 
	 * get list of board
	 * @param ss
	 * @param res
	 * @param userData, [category][offset][count](userid) 
	 * @return {[board id][title][content][hasimage][hasfile][category][create user name][create time]}
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doGetBoardList = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		BoardList data = new RecDataBoard().new BoardList(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecBoard> boardList = null;
		if(data.userid==null)
			boardList = DbAppManager.getInst().getBoardList(ss.scode, data.category, data.offset, data.count);	//load all list
		else
			boardList = DbAppManager.getInst().getBoardList(ss.scode, data.userid, data.category, data.offset, data.count); //load a specific user's list
		if(boardList.size()<1)
			return res.setError(EBoardError.eNoListData);
		
		String param = boardList.stream().map(e-> String.format("%s%s%s%s%s%s%s%s%b%s%b%s%s%s%s%s%s", e.boardid, ASS.UNIT, 
								   e.itemtype, ASS.UNIT, e.title, ASS.UNIT, e.content, ASS.UNIT, e.hasimage, ASS.UNIT, e.hasfile, ASS.UNIT, 
								   e.category, ASS.UNIT, e.createusername, ASS.UNIT, e.createtime)).
						   collect(Collectors.joining(ASS.RECORD));
		
		return res.setError(EBoardError.eOK).setParam(param);
	};

	/** 
	 * get a content of boards
	 * @param ss
	 * @param res
	 * @param userData, [board id]
	 * @return null if not board id, else [boardid][content text]
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> doGetBoardContent = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		BoardContent data = new RecDataBoard().new BoardContent(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		//String[] usData = userData.split(ASS.UNIT, -1);
		String content = DbAppManager.getInst().getBoardContent(ss.scode, data.boardid);
		if(content==null)
			return res.setError(EBoardError.eNoData);
		DbAppManager.getInst().incBoardVisit(ss.scode, data.boardid);
		return res.setError(EBoardError.eOK).setParam(String.format("%s%s%s", data.boardid, ASS.UNIT, content));
	};
	
	/** 
	 * increase/decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][add or del:boolean]
	 * @return like count and 
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> incBoardLike = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		BoardLike data = new RecDataBoard().new BoardLike(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		//if add request
		if(data.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), ss.getUsername(), data.preference) == false)
			return res.setError(EBoardError.eAlreadyLiked);
		//else if del request
		else if(data.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), data.preference) == false)
			return res.setError(EBoardError.eNotExistLikedUser);
			
		DbAppManager.getInst().incBoardLike(ss.scode, data.boardid, data.isadd);
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][inc if true, else dec]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> incBoardDislike = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		BoardDislike data = new RecDataBoard().new BoardDislike(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		//if add request
		if(data.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), ss.getUsername(), data.preference) == false)
			return res.setError(EBoardError.eAlreadyDisliked);
		//else if del request
		else if(data.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), data.preference) == false)
			return res.setError(EBoardError.eNotExistDislikeUser);
		
		DbAppManager.getInst().incBoardDislike(ss.scode, data.boardid, data.isadd);
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * add a reply message
	 * @param ss
	 * @param res
	 * @param userData. [board id][parent reply id][depth][msg]
	 *  	  parent reply id: 0 if no parent id
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> addReply = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		AddReply data = new RecDataBoard().new AddReply(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().addReply(ss.scode, data.boardid, data.parentrepid, ss.getUserId(), ss.getUsername(), (short)data.depth, data.msg)==false)
			return res.setError(EBoardError.eFailAddReply);
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * delete a reply message
	 * @param ss
	 * @param res
	 * @param userData, [board id][reply id]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> delReply = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		DelReply data = new RecDataBoard().new DelReply(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().delReply(ss.scode, data.boardid, data.replyid, ss.getUserId())==false)
			return res.setError(EBoardError.eFailDeleteReply);
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * get reply list of board id
	 * @param ss
	 * @param res
	 * @param userData, [board id][offset][count]
	 * @return [board id]|[reply id][parent reply id][depth][msg][username][create time]
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> getReplyList = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		ReplyList data = new RecDataBoard().new ReplyList(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecBoardReply> replyList = DbAppManager.getInst().getReplyList(ss.scode, data.boardid, data.offset, data.count);
		if(replyList.size() < 1)
			return res.setError(EBoardError.eNoListData);
		String param = replyList.stream().map(e->String.format("%s%s%s%s%d%s%s%s%s%s%s", 
				e.replyid, ASS.UNIT, e.parentid, ASS.UNIT, e.depth, ASS.UNIT, e.msg, ASS.UNIT, e.username, ASS.UNIT, e.replytime)).
				collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(data.boardid+ASS.GROUP+param);
	};
	
	/** 
	 * add vote item
	 * @param ss
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode] | [expire time][isclose] | {[vote text][vote url]/..}
	 * 					item type=> "vote"
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> addVote = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		AddVote data = new RecDataBoard().new AddVote(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if( (res = addBoard(ss, res, data.board)).getError() != EBoardError.eOK)	//1. board info of vote
			return res;
		if( DbAppManager.getInst().addVoteInfo(ss.scode, res.getParam(), ss.getUserId(), data.expiretime, data.isclose) == false ) {
			DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), res.getParam());
			return res.setError(EBoardError.eInvalidParameter);
		}
		if(data.itemList.size() < 2) {
			DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), res.getParam());
			DbAppManager.getInst().deleteVoteInfo(ss.scode, res.getParam(), ss.getUserId());
			return res.setError(EBoardError.eInvalidParameter);
		}
		for(VoteText item : data.itemList) {
			String voteitemid = StrUtil.getSha1Uuid("vid");
			DbAppManager.getInst().addVote(ss.scode, res.getParam(), voteitemid, item.votetext, item.voteurl);
		}
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * select item of vote of board
	 * @param ss
	 * @param res
	 * @param userData, [board id][vote item id][is select or unselect,true or false]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> selectVoteItem = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		SelectVote data = new RecDataBoard().new SelectVote(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		RecVoteUser voteuser = DbAppManager.getInst().getVoteUser(ss.scode, ss.getUserId(), data.boardid);
		
		if(data.isselect==true && voteuser!=DbRecord.Empty)
			return res.setError(EBoardError.eAlreadyVoteUser);
		else if(data.isselect==false && voteuser==DbRecord.Empty)
			return res.setError(EBoardError.eNotExistVoteUser);
		if(data.isselect==true && DbAppManager.getInst().addVoteUser(ss.scode, ss.getUserId(), data.boardid, data.vitemid)==false)
			return res.setError(EBoardError.eFailAddVoteUser);
		else if(data.isselect==false && DbAppManager.getInst().delVoteUser(ss.scode, ss.getUserId(), data.boardid)==false)
			return res.setError(EBoardError.eFailDelVoteUser);
		
		DbAppManager.getInst().updateVoteSelection(ss.scode, data.boardid, data.vitemid, data.isselect);
		return res.setError(EBoardError.eOK);
	};
	
	/** 
	 * select vote list
	 * @param userData, [board id]
	 * @return boardid | {[vote item id][select count][vote text][vote url] }
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> getVoteItemList = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		VoteItemList data = new RecDataBoard().new VoteItemList(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecVote> voteList = DbAppManager.getInst().getVoteItemList(ss.scode, data.boardid);
		String param = voteList.stream().map(e->String.format("%s%s%s%s%s%s%s", 
					   e.vitemid, ASS.UNIT, e.selectcount, ASS.UNIT, e.votetext, ASS.UNIT, e.voteurl)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(data.boardid + ASS.GROUP + param);
	};

	/** 
	 * update vote info.. expire time or early close
	 * @param ss
	 * @param res
	 * @param userData, [board id][type][value]
	 * 		  type -> "expire", value = milliseconds
	 * 		  value -> "close", true or false
	 * @return [board id][type][value]
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> updateVoteInfo = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		VoteUpdate data = new RecDataBoard().new VoteUpdate(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		RecVoteInfo vi = DbAppManager.getInst().getVoteInfo(ss.scode, data.boardid);
		if(vi == DbRecord.Empty)
			return res.setError(EBoardError.eNotExistVoteInfo);
		else if(vi.userid.equals(ss.getUserId())==false)
			return res.setError(EBoardError.ePermissionDeni);
		else if(vi.expiretime < System.currentTimeMillis() || vi.isclosed == true)
			return res.setError(EBoardError.eAlreadyExpired);
			
		if("expire".equals(data.type))
			DbAppManager.getInst().updateVoteExpireTime(ss.scode, data.boardid, ss.getUserId(), Long.parseLong(data.value));
		else if("close".equals(data.type))
			DbAppManager.getInst().updateVoteClose(ss.scode, data.boardid, ss.getUserId(), Boolean.parseBoolean(data.value));
		return res.setError(EBoardError.eOK).setParam(data.boardid+ASS.UNIT+data.type+ASS.UNIT+data.value);
	};

	/** 
	 * update vote item
	 * @param ss
	 * @param res
	 * @param userData, [board id][vote item id][type][value]
	 * 		  type -> "text", "url"
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> updateVoteItem = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		VoteItem data = new RecDataBoard().new VoteItem(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		if("text".equals(data.type))
			DbAppManager.getInst().updateVoteitemText(ss.scode, data.boardid, data.vitemid, data.value);
		else if("url".equals(data.type))
			DbAppManager.getInst().updateVoteitemUrl(ss.scode, data.boardid, data.vitemid, data.value);
		return res.setError(EBoardError.eOK).setParam(data.boardid+ASS.UNIT+data.vitemid+ASS.UNIT+data.type+ASS.UNIT+data.value);
	};

	/** 
	 * change vote item selection
	 * @param ss
	 * @param res
	 * @param userData, [board id][new vote item id]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> changeVoteSelection = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		ChangeVoteSelection data = new RecDataBoard().new ChangeVoteSelection(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().changeSelectItem(ss.scode, ss.getUserId(), data.boardid, data.vitemid)==false)
			return res.setError(EBoardError.eNotExistVoteInfo);
		return res.setError(EBoardError.eOK).setParam(data.boardid+ASS.UNIT+data.vitemid);
	};
	
	/** 
	 * get vote info list
	 * @param ss
	 * @param res
	 * @param userData, {[board id]}
	 * @return {[board id][expire time][is closed]}, if exist
	 */
	ICommandFunction<AuthSession, ResponseData<EBoardError>, JsonNode> getVoteInfoList = (AuthSession ss, ResponseData<EBoardError> res, JsonNode jnode) -> {
		VoteInfoList data = new RecDataBoard().new VoteInfoList(jnode);
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		List<RecVoteInfo> vinfolist = DbAppManager.getInst().getVoteInfoList(ss.scode, data.boardids);
		if(vinfolist.size()<1)
			return res.setError(EBoardError.eNoListData);

		String param = vinfolist.stream().map(e->String.format("%s%s%d%s%b", 
				   e.boardid, ASS.UNIT, e.expiretime, ASS.UNIT, e.isclosed)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(param);
	};
	
	
	private ResponseData<EBoardError> addBoard(AuthSession ss, ResponseData<EBoardError> res, AddBoard data) {
		String boardid = StrUtil.getSha1Uuid("brd");
		if(DbAppManager.getInst().addBoardShort(ss.scode, boardid, data.itemtype, data.title, getShortContent(data.content), data.hasimage,
				data.hasfile, data.category, data.appcode, ss.getUserId(), ss.getUsername())==false) 		//insert content's shortcut
			return res.setError(EBoardError.eFailAddBoard);
		
		DbAppManager.getInst().addBoardContent(ss.scode, boardid, data.content);	//insert content
		DbAppManager.getInst().addBoardCount(ss.scode, boardid);
		return res.setError(EBoardError.eOK).setParam(boardid);
	}

}
