package com.ccz.appinall.services.action.board;

import java.util.List;
import java.util.stream.Collectors;

import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.action.CommonAction;
import com.ccz.appinall.services.action.auth.AuthSession;
import com.ccz.appinall.services.action.board.RecDataBoard.*;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecBoard;
import com.ccz.appinall.services.entity.db.RecBoardReply;
import com.ccz.appinall.services.entity.db.RecVote;
import com.ccz.appinall.services.entity.db.RecVoteInfo;
import com.ccz.appinall.services.entity.db.RecVoteUser;
import com.ccz.appinall.services.type.enums.EBoardCmd;
import com.ccz.appinall.services.type.enums.EBoardError;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class BoardCommandAction extends CommonAction {

	public BoardCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	private boolean processBoardData(Channel ch, String[] data, JsonNode jdata) {
		ResponseData<EBoardError> res = null;
		if(data != null)
			res = new ResponseData<EBoardError>(data[0], data[1], data[2]);
		else
			res = new ResponseData<EBoardError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		AuthSession ss = (AuthSession) ch.attr(sessionKey).get();
		switch(EBoardCmd.getType(res.getCommand())) {
		case addboard:
			res = this.doAddBoard(ss, res, data!=null ? new RecDataBoard().new AddBoard(data[3]) : new RecDataBoard().new AddBoard(jdata)); //O
			break;
		case delboard:
			res = this.doDelBoard(ss, res, data!=null ? new RecDataBoard().new DelBoard(data[3]) : new RecDataBoard().new DelBoard(jdata));//O
			break;
		case updatetitle:
			res = this.doUpdateBoardTitle(ss, res, data!=null ? new RecDataBoard().new UpdateBoardTitle(data[3]) : new RecDataBoard().new UpdateBoardTitle(jdata));//O
			break;
		case updatecontent:
			res = this.doUpdateBoardContent(ss, res, data!=null ? new RecDataBoard().new UpdateBoardContent(data[3]) : new RecDataBoard().new UpdateBoardContent(jdata));//O
			break;
		case updatecategory:
			res = this.doUpdateBoardCategory(ss, res, data!=null ? new RecDataBoard().new UpdateBoardCategory(data[3]) : new RecDataBoard().new UpdateBoardCategory(jdata));//O
			break;
		case boardlist:
			res = this.doGetBoardList(ss, res, data!=null ? new RecDataBoard().new BoardList(data[3]) : new RecDataBoard().new BoardList(jdata));//O
			break;
		case boardcontent:
			res = this.doGetBoardContent(ss, res, data!=null ? new RecDataBoard().new BoardContent(data[3]) : new RecDataBoard().new BoardContent(jdata));//O
			break;
		case like:
			res = this.incBoardLike(ss, res, data!=null ? new RecDataBoard().new BoardLike(data[3]) : new RecDataBoard().new BoardLike(jdata));//O
			break;
		case dislike:
			res = this.incBoardDislike(ss, res, data!=null ? new RecDataBoard().new BoardDislike(data[3]) : new RecDataBoard().new BoardDislike(jdata));//O
			break;
		case addreply:
			res = this.addReply(ss, res, data!=null ? new RecDataBoard().new AddReply(data[3]) : new RecDataBoard().new AddReply(jdata)); //O
			break;
		case delreply:
			res = this.delReply(ss, res, data!=null ? new RecDataBoard().new DelReply(data[3]) : new RecDataBoard().new DelReply(jdata));//O
			break;
		case replylist:
			res = this.getReplyList(ss, res, data!=null ? new RecDataBoard().new ReplyList(data[3]) : new RecDataBoard().new ReplyList(jdata));//O
			break;
		case vote:
			res = this.addVote(ss, res, data!=null ? new RecDataBoard().new AddVote(data[3]) : new RecDataBoard().new AddVote(jdata));//O
			break;
		case selvote:
			res = this.selectVoteItem(ss, res, data!=null ? new RecDataBoard().new SelectVote(data[3]) : new RecDataBoard().new SelectVote(jdata));//O
			break;
		case voteitemlist:
			res = this.getVoteItemList(ss, res, data!=null ? new RecDataBoard().new VoteItemList(data[3]) : new RecDataBoard().new VoteItemList(jdata));//O
			break;
		case voteupdate:
			res = this.updateVoteInfo(ss, res, data!=null ? new RecDataBoard().new VoteUpdate(data[3]) : new RecDataBoard().new VoteUpdate(jdata));//O
			break;
		case voteitem:
			res = this.updateVoteItem(ss, res, data!=null ? new RecDataBoard().new VoteItem(data[3]) : new RecDataBoard().new VoteItem(jdata));//O
			break;
		case changeselection:
			res = this.changeVoteSelection(ss, res, data!=null ? new RecDataBoard().new ChangeVoteSelection(data[3]) : new RecDataBoard().new ChangeVoteSelection(jdata));//O
			break;
		case voteinfolist:
			res = this.getVoteInfoList(ss, res, data!=null ? new RecDataBoard().new VoteInfoList(data[3]) : new RecDataBoard().new VoteInfoList(jdata));//O
			break;
		default:
			return false;
		}
		if(res != null)
			send(ch, res.toString());
		return true;
	}
	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return processBoardData(ch, data, null);
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		return processBoardData(ch, null, jdata);
	}

	/** 
	 * add new article
	 * @param ch
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode]
	 * 					item type => "board"
	 * @return 
	 */
	private ResponseData<EBoardError> doAddBoard(AuthSession ss, ResponseData<EBoardError> res, AddBoard rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		String boardid = StrUtil.getSha1Uuid("brd");
		if(DbAppManager.getInst().addBoardShort(ss.serviceCode, boardid, rec.itemtype, rec.title, getShortContent(rec.content), rec.hasimage,
				rec.hasfile, rec.category, rec.appcode, ss.getUserId(), ss.getUsername())==false) 		//insert content's shortcut
			return res.setError(EBoardError.eFailAddBoard);
		
		DbAppManager.getInst().addBoardContent(ss.serviceCode, boardid, rec.content);	//insert content
		DbAppManager.getInst().addBoardCount(ss.serviceCode, boardid);
		return res.setError(EBoardError.eOK).setParam(boardid);
	}
	
	private String getShortContent(String content) {
		if(content.length()<64)
			return content;
		return content.substring(0, 64);
	}
	
	private ResponseData<EBoardError> doDelBoard(AuthSession ss, ResponseData<EBoardError> res, DelBoard rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().delBoard(ss.serviceCode, ss.getUserId(), rec.boardid)==false)
			return res.setError(EBoardError.eFailDeleteBoard);
		return res.setError(EBoardError.eOK);
	}

	/** 
	 * update board's title
	 * @param ss: session handle
	 * @param res : response data
	 * @param eCmd : command enum
	 * @param userData, [board id], [title]
	 * @return eFailUpdate if failed, else eOK
	 */
	private ResponseData<EBoardError> doUpdateBoardTitle(AuthSession ss, ResponseData<EBoardError> res, UpdateBoardTitle rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardTitle(ss.serviceCode, ss.getUserId(), rec.boardid, rec.title)==false)
			return res.setError(EBoardError.eFailUpdate);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	}
	
	/** 
	 * update board's content
	 * @param ss
	 * @param res
	 * @param userData, [board id][content][hasimage][hasfile]
	 * @return
	 */
	private ResponseData<EBoardError> doUpdateBoardContent(AuthSession ss, ResponseData<EBoardError> res, UpdateBoardContent rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardShortContent(ss.serviceCode, ss.getUserId(), rec.boardid, getShortContent(rec.content), rec.hasimage, rec.hasfile)==false)
			return res.setError(EBoardError.eFailUpdate);
		DbAppManager.getInst().updateBoardContent(ss.serviceCode, rec.boardid, rec.content);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	}

	/** 
	 * update board category
	 * @param ss
	 * @param res
	 * @param userData, [board id][category]
	 * @return
	 */
	private ResponseData<EBoardError> doUpdateBoardCategory(AuthSession ss, ResponseData<EBoardError> res, UpdateBoardCategory rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().updateBoardCategory(ss.serviceCode, ss.getUserId(), rec.boardid, rec.category)==false)
			return res.setError(EBoardError.eFailUpdate);
		return res.setError(EBoardError.eOK);//.setParam(""+user.lasttime);
	}

	/** 
	 * get list of board
	 * @param ss
	 * @param res
	 * @param userData, [category][offset][count](userid) 
	 * @return {[board id][title][content][hasimage][hasfile][category][create user name][create time]}
	 */
	private ResponseData<EBoardError> doGetBoardList(AuthSession ss, ResponseData<EBoardError> res, BoardList rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecBoard> boardList = null;
		if(rec.userid==null)
			boardList = DbAppManager.getInst().getBoardList(ss.serviceCode, rec.category, rec.offset, rec.count);	//load all list
		else
			boardList = DbAppManager.getInst().getBoardList(ss.serviceCode, rec.userid, rec.category, rec.offset, rec.count); //load a specific user's list
		if(boardList.size()<1)
			return res.setError(EBoardError.eNoListData);
		
		String param = boardList.stream().map(e-> String.format("%s%s%s%s%s%s%s%s%b%s%b%s%s%s%s%s%s", e.boardid, ASS.UNIT, 
								   e.itemtype, ASS.UNIT, e.title, ASS.UNIT, e.content, ASS.UNIT, e.hasimage, ASS.UNIT, e.hasfile, ASS.UNIT, 
								   e.category, ASS.UNIT, e.createusername, ASS.UNIT, e.createtime)).
						   collect(Collectors.joining(ASS.RECORD));
		
		return res.setError(EBoardError.eOK).setParam(param);
	}

	/** 
	 * get a content of boards
	 * @param ss
	 * @param res
	 * @param userData, [board id]
	 * @return null if not board id, else [boardid][content text]
	 */
	private ResponseData<EBoardError> doGetBoardContent(AuthSession ss, ResponseData<EBoardError> res, BoardContent rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		//String[] usData = userData.split(ASS.UNIT, -1);
		String content = DbAppManager.getInst().getBoardContent(ss.serviceCode, rec.boardid);
		if(content==null)
			return res.setError(EBoardError.eNoData);
		DbAppManager.getInst().incBoardVisit(ss.serviceCode, rec.boardid);
		return res.setError(EBoardError.eOK).setParam(String.format("%s%s%s", rec.boardid, ASS.UNIT, content));
	}
	
	/** 
	 * increase/decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][add or del:boolean]
	 * @return like count and 
	 */
	private ResponseData<EBoardError> incBoardLike(AuthSession ss, ResponseData<EBoardError> res, BoardLike rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		//if add request
		if(rec.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.serviceCode, rec.boardid, ss.getUserId(), ss.getUsername(), rec.preference) == false)
			return res.setError(EBoardError.eAlreadyLiked);
		//else if del request
		else if(rec.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.serviceCode, rec.boardid, ss.getUserId(), rec.preference) == false)
			return res.setError(EBoardError.eNotExistLikedUser);
			
		DbAppManager.getInst().incBoardLike(ss.serviceCode, rec.boardid, rec.isadd);
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][inc if true, else dec]
	 * @return
	 */
	private ResponseData<EBoardError> incBoardDislike(AuthSession ss, ResponseData<EBoardError> res, BoardDislike rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		//if add request
		if(rec.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.serviceCode, rec.boardid, ss.getUserId(), ss.getUsername(), rec.preference) == false)
			return res.setError(EBoardError.eAlreadyDisliked);
		//else if del request
		else if(rec.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.serviceCode, rec.boardid, ss.getUserId(), rec.preference) == false)
			return res.setError(EBoardError.eNotExistDislikeUser);
		
		DbAppManager.getInst().incBoardDislike(ss.serviceCode, rec.boardid, rec.isadd);
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * add a reply message
	 * @param ss
	 * @param res
	 * @param userData. [board id][parent reply id][depth][msg]
	 *  	  parent reply id: 0 if no parent id
	 * @return
	 */
	private ResponseData<EBoardError> addReply(AuthSession ss, ResponseData<EBoardError> res, AddReply rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().addReply(ss.serviceCode, rec.boardid, rec.parentrepid, ss.getUserId(), ss.getUsername(), (short)rec.depth, rec.msg)==false)
			return res.setError(EBoardError.eFailAddReply);
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * delete a reply message
	 * @param ss
	 * @param res
	 * @param userData, [board id][reply id]
	 * @return
	 */
	private ResponseData<EBoardError> delReply(AuthSession ss, ResponseData<EBoardError> res, DelReply rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().delReply(ss.serviceCode, rec.boardid, rec.replyid, ss.getUserId())==false)
			return res.setError(EBoardError.eFailDeleteReply);
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * get reply list of board id
	 * @param ss
	 * @param res
	 * @param userData, [board id][offset][count]
	 * @return [board id]|[reply id][parent reply id][depth][msg][username][create time]
	 */
	private ResponseData<EBoardError> getReplyList(AuthSession ss, ResponseData<EBoardError> res, ReplyList rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecBoardReply> replyList = DbAppManager.getInst().getReplyList(ss.serviceCode, rec.boardid, rec.offset, rec.count);
		if(replyList.size() < 1)
			return res.setError(EBoardError.eNoListData);
		String param = replyList.stream().map(e->String.format("%s%s%s%s%d%s%s%s%s%s%s", 
				e.replyid, ASS.UNIT, e.parentid, ASS.UNIT, e.depth, ASS.UNIT, e.msg, ASS.UNIT, e.username, ASS.UNIT, e.replytime)).
				collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(rec.boardid+ASS.GROUP+param);
	}
	
	/** 
	 * add vote item
	 * @param ss
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode] | [expire time][isclose] | {[vote text][vote url]/..}
	 * 					item type=> "vote"
	 * @return
	 */
	private ResponseData<EBoardError> addVote(AuthSession ss, ResponseData<EBoardError> res, AddVote rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if( (res = this.doAddBoard(ss, res, rec.board)).getError() != EBoardError.eOK)	//1. board info of vote
			return res;
		if( DbAppManager.getInst().addVoteInfo(ss.serviceCode, res.getParam(), ss.getUserId(), rec.expiretime, rec.isclose) == false ) {
			DbAppManager.getInst().delBoard(ss.serviceCode, ss.getUserId(), res.getParam());
			return res.setError(EBoardError.eInvalidParameter);
		}
		if(rec.itemList.size() < 2) {
			DbAppManager.getInst().delBoard(ss.serviceCode, ss.getUserId(), res.getParam());
			DbAppManager.getInst().deleteVoteInfo(ss.serviceCode, res.getParam(), ss.getUserId());
			return res.setError(EBoardError.eInvalidParameter);
		}
		for(VoteText item : rec.itemList) {
			String voteitemid = StrUtil.getSha1Uuid("vid");
			DbAppManager.getInst().addVote(ss.serviceCode, res.getParam(), voteitemid, item.votetext, item.voteurl);
		}
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * select item of vote of board
	 * @param ss
	 * @param res
	 * @param userData, [board id][vote item id][is select or unselect,true or false]
	 * @return
	 */
	private ResponseData<EBoardError> selectVoteItem(AuthSession ss, ResponseData<EBoardError> res, SelectVote rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		RecVoteUser voteuser = DbAppManager.getInst().getVoteUser(ss.serviceCode, ss.getUserId(), rec.boardid);
		
		if(rec.isselect==true && voteuser!=DbRecord.Empty)
			return res.setError(EBoardError.eAlreadyVoteUser);
		else if(rec.isselect==false && voteuser==DbRecord.Empty)
			return res.setError(EBoardError.eNotExistVoteUser);
		if(rec.isselect==true && DbAppManager.getInst().addVoteUser(ss.serviceCode, ss.getUserId(), rec.boardid, rec.vitemid)==false)
			return res.setError(EBoardError.eFailAddVoteUser);
		else if(rec.isselect==false && DbAppManager.getInst().delVoteUser(ss.serviceCode, ss.getUserId(), rec.boardid)==false)
			return res.setError(EBoardError.eFailDelVoteUser);
		
		DbAppManager.getInst().updateVoteSelection(ss.serviceCode, rec.boardid, rec.vitemid, rec.isselect);
		return res.setError(EBoardError.eOK);
	}
	
	/** 
	 * select vote list
	 * @param userData, [board id]
	 * @return boardid | {[vote item id][select count][vote text][vote url] }
	 */
	private ResponseData<EBoardError> getVoteItemList(AuthSession ss, ResponseData<EBoardError> res, VoteItemList rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		List<RecVote> voteList = DbAppManager.getInst().getVoteItemList(ss.serviceCode, rec.boardid);
		String param = voteList.stream().map(e->String.format("%s%s%s%s%s%s%s", 
					   e.vitemid, ASS.UNIT, e.selectcount, ASS.UNIT, e.votetext, ASS.UNIT, e.voteurl)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(rec.boardid + ASS.GROUP + param);
	}

	/** 
	 * update vote info.. expire time or early close
	 * @param ss
	 * @param res
	 * @param userData, [board id][type][value]
	 * 		  type -> "expire", value = milliseconds
	 * 		  value -> "close", true or false
	 * @return [board id][type][value]
	 */
	private ResponseData<EBoardError> updateVoteInfo(AuthSession ss, ResponseData<EBoardError> res, VoteUpdate rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		RecVoteInfo vi = DbAppManager.getInst().getVoteInfo(ss.serviceCode, rec.boardid);
		if(vi == DbRecord.Empty)
			return res.setError(EBoardError.eNotExistVoteInfo);
		else if(vi.userid.equals(ss.getUserId())==false)
			return res.setError(EBoardError.ePermissionDeni);
		else if(vi.expiretime < System.currentTimeMillis() || vi.isclosed == true)
			return res.setError(EBoardError.eAlreadyExpired);
			
		if("expire".equals(rec.type))
			DbAppManager.getInst().updateVoteExpireTime(ss.serviceCode, rec.boardid, ss.getUserId(), Long.parseLong(rec.value));
		else if("close".equals(rec.type))
			DbAppManager.getInst().updateVoteClose(ss.serviceCode, rec.boardid, ss.getUserId(), Boolean.parseBoolean(rec.value));
		return res.setError(EBoardError.eOK).setParam(rec.boardid+ASS.UNIT+rec.type+ASS.UNIT+rec.value);
	}

	/** 
	 * update vote item
	 * @param ss
	 * @param res
	 * @param userData, [board id][vote item id][type][value]
	 * 		  type -> "text", "url"
	 * @return
	 */
	private ResponseData<EBoardError> updateVoteItem(AuthSession ss, ResponseData<EBoardError> res, VoteItem rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		if("text".equals(rec.type))
			DbAppManager.getInst().updateVoteitemText(ss.serviceCode, rec.boardid, rec.vitemid, rec.value);
		else if("url".equals(rec.type))
			DbAppManager.getInst().updateVoteitemUrl(ss.serviceCode, rec.boardid, rec.vitemid, rec.value);
		return res.setError(EBoardError.eOK).setParam(rec.boardid+ASS.UNIT+rec.vitemid+ASS.UNIT+rec.type+ASS.UNIT+rec.value);
	}

	/** 
	 * change vote item selection
	 * @param ss
	 * @param res
	 * @param userData, [board id][new vote item id]
	 * @return
	 */
	private ResponseData<EBoardError> changeVoteSelection(AuthSession ss, ResponseData<EBoardError> res, ChangeVoteSelection rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		if(DbAppManager.getInst().changeSelectItem(ss.serviceCode, ss.getUserId(), rec.boardid, rec.vitemid)==false)
			return res.setError(EBoardError.eNotExistVoteInfo);
		return res.setError(EBoardError.eOK).setParam(rec.boardid+ASS.UNIT+rec.vitemid);
	}
	
	/** 
	 * get vote info list
	 * @param ss
	 * @param res
	 * @param userData, {[board id]}
	 * @return {[board id][expire time][is closed]}, if exist
	 */
	private ResponseData<EBoardError> getVoteInfoList(AuthSession ss, ResponseData<EBoardError> res, VoteInfoList rec) {
		if(ss==null)
			return res.setError(EBoardError.eNoSession);
		
		List<RecVoteInfo> vinfolist = DbAppManager.getInst().getVoteInfoList(ss.serviceCode, rec.boardids);
		if(vinfolist.size()<1)
			return res.setError(EBoardError.eNoListData);

		String param = vinfolist.stream().map(e->String.format("%s%s%d%s%b", 
				   e.boardid, ASS.UNIT, e.expiretime, ASS.UNIT, e.isclosed)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EBoardError.eOK).setParam(param);
	}

}
