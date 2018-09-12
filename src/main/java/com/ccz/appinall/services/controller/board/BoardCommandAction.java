package com.ccz.appinall.services.controller.board;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.common.rdb.DbTransaction;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.library.module.scrap.HtmlNode;
import com.ccz.appinall.library.module.scrap.HtmlScrapper;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker.ImageResizerCallback;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.ImageUtil;
import com.ccz.appinall.library.util.ImageUtil.ImageSize;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.address.RecDataAddr;
import com.ccz.appinall.services.controller.address.RecDataAddr.DataSearchAddr;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.board.RecDataBoard.*;
import com.ccz.appinall.services.controller.file.UploadFile;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.model.db.RecBoard;
import com.ccz.appinall.services.model.db.RecBoardCount;
import com.ccz.appinall.services.model.db.RecBoardDetail;
import com.ccz.appinall.services.model.db.RecBoardReply;
import com.ccz.appinall.services.model.db.RecBoardUser;
import com.ccz.appinall.services.model.db.RecFile;
import com.ccz.appinall.services.model.db.RecScrap;
import com.ccz.appinall.services.model.db.RecScrapDetail;
import com.ccz.appinall.services.model.db.RecUserBoardTableList;
import com.ccz.appinall.services.model.db.RecVote;
import com.ccz.appinall.services.model.db.RecVoteInfo;
import com.ccz.appinall.services.model.db.RecVoteUser;
import com.ccz.appinall.services.repository.elasticsearch.BoardElasticSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BoardCommandAction extends CommonAction {
	
	@Autowired ImageResizeWorker imageResizeWorker;
	@Autowired ServicesConfig servicesConfig;
	@Autowired BoardElasticSearch boardElasticSearch;
	
	public BoardCommandAction() {
		super.setCommandFunction(EAllCmd.getcategorylist, doGetCategoryList);
		super.setCommandFunction(EAllCmd.addboard, doAddBoard); //O
		super.setCommandFunction(EAllCmd.delboard, doDelBoard);//O
		super.setCommandFunction(EAllCmd.updatetitle, doUpdateBoardTitle);//O
		super.setCommandFunction(EAllCmd.updatecontent, doUpdateBoardContent);//O
		super.setCommandFunction(EAllCmd.updatecategory, doUpdateBoardCategory);//O
		super.setCommandFunction(EAllCmd.updateboard, doUpdateBoard);//O
		super.setCommandFunction(EAllCmd.boardlist, doGetBoardList);//O
		super.setCommandFunction(EAllCmd.getcontent, doGetBoardContent);//O
		super.setCommandFunction(EAllCmd.like, incBoardLike);//O
		super.setCommandFunction(EAllCmd.dislike, incBoardDislike);//O
		super.setCommandFunction(EAllCmd.addreply, addReply); //O
		super.setCommandFunction(EAllCmd.delreply, delReply);//O
		super.setCommandFunction(EAllCmd.replylist, getReplyList);//O
		super.setCommandFunction(EAllCmd.addvote, addVote);//O
		super.setCommandFunction(EAllCmd.voteitemlist, getVoteItemList);//O
		super.setCommandFunction(EAllCmd.selvote, selectVoteItem);//O
		super.setCommandFunction(EAllCmd.voteupdate, updateVote);//O
		super.setCommandFunction(EAllCmd.changeselection, changeVoteSelection);//O
		super.setCommandFunction(EAllCmd.voteinfolist, getVoteInfoList);//O
		super.setCommandFunction(EAllCmd.boardsearch, boardSearch);
	}

	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doGetCategoryList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		if(ss==null)
			return res.setError(EAllError.NoSession);
		List<RecUserBoardTableList> tableList = DbAppManager.getInst().getUserTableList(ss.scode, ss.getUserId(), 3);
		if(tableList.size()<1)
			return res.setError(EAllError.NoData);
		res.setParam("categories", tableList);	
		return res.setError(EAllError.ok);
	};
	
	
	/** 
	 * add new article
	 * @param ch
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode]
	 * 					item type => "board"
	 * @return 
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doAddBoard = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		AddBoard data = new RecDataBoard().new AddBoard(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		List<String> scrapIds = findAndAddScrap(ss, data);
		return addBoard(ss, res, data, scrapIds);
	};
	
	private String getShortContent(String content) {
		if(content.length()<54)
			return content;
		return content.substring(0, 54) + "...(더보기)";
	}
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doDelBoard = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		DelBoard data = new RecDataBoard().new DelBoard(jnode); 
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), data.boardid)==false)
			return res.setError(EAllError.FailDeleteBoard);
		res.setParam("boardid", data.boardid);
		return res.setError(EAllError.ok);
	};

	/** 
	 * update board's title
	 * @param ss: session handle
	 * @param res : response data
	 * @param eCmd : command enum
	 * @param userData, [board id], [title]
	 * @return FailUpdate if failed, else OK
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doUpdateBoardTitle = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		UpdateBoardTitle data = new RecDataBoard().new UpdateBoardTitle(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().updateBoardTitle(ss.scode, ss.getUserId(), data.boardid, data.title)==false)
			return res.setError(EAllError.FailUpdate);
		return res.setError(EAllError.ok);//.setParam(""+user.lasttime);
	};
	
	/** 
	 * update board's content
	 * @param ss
	 * @param res
	 * @param userData, [board id][content][hasimage][hasfile]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doUpdateBoardContent = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		UpdateBoardContent data = new RecDataBoard().new UpdateBoardContent(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().updateBoardShortContent(ss.scode, ss.getUserId(), data.boardid, getShortContent(data.content), data.hasimage, data.hasfile)==false)
			return res.setError(EAllError.FailUpdate);
		DbAppManager.getInst().updateBoardContent(ss.scode, data.boardid, data.content);
		return res.setError(EAllError.ok);//.setParam(""+user.lasttime);
	};

	/** 
	 * update board category
	 * @param ss
	 * @param res
	 * @param userData, [board id][category]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doUpdateBoardCategory = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		UpdateBoardCategory data = new RecDataBoard().new UpdateBoardCategory(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().updateBoardCategory(ss.scode, ss.getUserId(), data.boardid, data.category)==false)
			return res.setError(EAllError.FailUpdate);
		return res.setError(EAllError.ok);//.setParam(""+user.lasttime);
	};
	/** 
	 * update board's content
	 * @param ss
	 * @param res
	 * @param userData, [board id][content][hasimage][hasfile]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doUpdateBoard = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		UpdateBoard data = new RecDataBoard().new UpdateBoard(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		List<String> scrapIds = findAndAddScrap(ss, data);
		return updateBoard(ss, res, data, scrapIds);
	};

	/** 
	 * get list of board
	 * @param ss
	 * @param res
	 * @param userData, [category][offset][count](userid) 
	 * @return {[board id][title][content][hasimage][hasfile][category][create user name][create time]}
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doGetBoardList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		BoardList data = new RecDataBoard().new BoardList(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		String categoryId = ss.getTableIdByCategoryIndex(data.getCategoryInex());
		if(categoryId==null)
			return res.setError(EAllError.InvalidCategoryId);

		List<RecBoardDetail> boardList = DbAppManager.getInst().getBoardDetailList(ss.scode, categoryId, data.getOffset(), data.getCount());	//load all list
		//boardList = DbAppManager.getInst().getBoardList(ss.scode, data.getUserid(), categoryId, data.getOffset(), data.getCount()); //load a specific user's list
		if(boardList.size()<1)
			return res.setError(EAllError.NoListData);
		
		res.setParam("category", data.getCategoryInex());
		res.setParam("data", boardList);
		return res.setError(EAllError.ok);
	};

	/** 
	 * get a content of boards
	 * @param ss
	 * @param res
	 * @param userData, [board id]
	 * @return null if not board id, else [boardid][content text]
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doGetBoardContent = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		BoardContent data = new RecDataBoard().new BoardContent(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		//String[] usData = userData.split(ASS.UNIT, -1);
		String content = DbAppManager.getInst().getBoardContent(ss.scode, data.boardid);
		if(content==null)
			return res.setError(EAllError.NoData);
		List<RecScrapDetail> scraps = DbAppManager.getInst().getScrapDetailList(ss.scode, data.boardid);
		DbAppManager.getInst().incBoardVisit(ss.scode, data.boardid);
		RecBoardUser recLike = DbAppManager.getInst().getBoardLikeDislike(ss.scode, data.boardid, ss.getUserId());
		if(recLike != DbRecord.Empty)
			res.setParam("like", recLike);
		res.setParam("files", DbAppManager.getInst().getFileList(ss.scode, data.boardid));
		res.setParam("vote", DbAppManager.getInst().getVoteItemList(ss.scode, data.boardid));
		if(scraps != null)
			res.setParam("scraps", scraps);
		return res.setError(EAllError.ok).setParam("content", content);
	};
	
	/** 
	 * increase/decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][add or del:boolean]
	 * @return like count and 
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> incBoardLike = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		BoardLike data = new RecDataBoard().new BoardLike(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		
		//if add request
		if(data.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), ss.getUsername(), data.preference) == false)
			return res.setError(EAllError.AlreadyLiked);
		//else if del request
		else if(data.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), data.preference) == false)
			return res.setError(EAllError.NotExistLikedUser);
			
		DbAppManager.getInst().incBoardLike(ss.scode, data.boardid, data.isadd);
		RecBoardCount count = DbAppManager.getInst().getBoardCount(ss.scode, data.boardid);
		res.setParam("preference", data.preference);
		res.setParam("isadd", data.isadd);
		if(count!=null)
			res.setParam("count", count);
		return res.setError(EAllError.ok);
	};
	
	/** 
	 * decrease the like count
	 * @param ss
	 * @param res
	 * @param userData, [board id][preference][inc if true, else dec]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> incBoardDislike = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		BoardDislike data = new RecDataBoard().new BoardDislike(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		
		//if add request
		if(data.isadd == true && DbAppManager.getInst().addBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), ss.getUsername(), data.preference) == false)
			return res.setError(EAllError.AlreadyDisliked);
		//else if del request
		else if(data.isadd==false && DbAppManager.getInst().delBoardLikeDislike(ss.scode, data.boardid, ss.getUserId(), data.preference) == false)
			return res.setError(EAllError.NotExistDislikeUser);
		
		DbAppManager.getInst().incBoardDislike(ss.scode, data.boardid, data.isadd);
		RecBoardCount count = DbAppManager.getInst().getBoardCount(ss.scode, data.boardid);
		res.setParam("preference", data.preference);
		res.setParam("isadd", data.isadd);
		if(count!=null)
			res.setParam("count", count);
		return res.setError(EAllError.ok);
	};
	
	/** 
	 * add a reply message
	 * @param ss
	 * @param res
	 * @param userData. [board id][parent reply id][depth][msg]
	 *  	  parent reply id: 0 if no parent id
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> addReply = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		AddReply data = new RecDataBoard().new AddReply(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		int replyId = DbAppManager.getInst().addReply(ss.scode, data.boardid, data.parentrepid, ss.getUserId(), ss.getUsername(), (short)data.depth, data.msg);
		if(replyId < 1)
			return res.setError(EAllError.FailAddReply);
		List<RecBoardReply> replyList = DbAppManager.getInst().getReplyList(ss.scode, data.boardid, 0, 15);	//Reply를 추가하면 0부터 15개를 전달한다.
		if(replyList.size() > 0)
			res.setParam("data", replyList);
		return res.setError(EAllError.ok).setParam("replyid", replyId);
	};
	
	/** 
	 * delete a reply message
	 * @param ss
	 * @param res
	 * @param userData, [board id][reply id]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> delReply = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		DelReply data = new RecDataBoard().new DelReply(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().delReply(ss.scode, data.boardid, data.replyid, ss.getUserId())==false)
			return res.setError(EAllError.FailDeleteReply);
		res.setParam("replyid", data.replyid);
		return res.setError(EAllError.ok);
	};
	
	/** 
	 * get reply list of board id
	 * @param ss
	 * @param res
	 * @param userData, [board id][offset][count]
	 * @return [board id]|[reply id][parent reply id][depth][msg][username][create time]
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> getReplyList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ReplyList data = new RecDataBoard().new ReplyList(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		List<RecBoardReply> replyList = DbAppManager.getInst().getReplyList(ss.scode, data.boardid, data.offset, data.count);
		if(replyList.size() < 1)
			return res.setError(EAllError.NoListData);
		res.setParam("data", replyList);
		return res.setError(EAllError.ok);
	};
	
	/** 
	 * add vote item
	 * @param ss
	 * @param res
	 * @param userData, [item type][title][content][hasimage][hasfile][category][aptcode] | [expire time][isclose] | {[vote text][vote url]/..}
	 * 					item type=> "vote"
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> addVote = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		AddVote data = new RecDataBoard().new AddVote(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if( (res = addBoard(ss, res, data.board, null)).getError() != EAllError.ok)	//1. board info of vote
			return res;
		
		String newBoardid = res.getDataParam("boardid");
		if( DbAppManager.getInst().addVoteInfo(ss.scode, newBoardid, ss.getUserId(), data.expiretime) == false ) {
			DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), newBoardid);
			return res.setError(EAllError.InvalidParameter);
		}
		if(data.itemList.size() < 2) {
			DbAppManager.getInst().delBoard(ss.scode, ss.getUserId(), newBoardid);
			DbAppManager.getInst().deleteVoteInfo(ss.scode, newBoardid, ss.getUserId());
			return res.setError(EAllError.InvalidParameter);
		}
		for(String votetext : data.itemList) {
			String voteitemid = StrUtil.getSha1Uuid("vid");
			DbAppManager.getInst().addVote(ss.scode, newBoardid, voteitemid, votetext);
		}
		return res.setError(EAllError.ok).setParam("boardid", newBoardid);
	};
	
	/** 
	 * select vote list
	 * @param userData, [board id]
	 * @return boardid | {[vote item id][select count][vote text][vote url] }
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> getVoteItemList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		VoteItemList data = new RecDataBoard().new VoteItemList(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		List<RecVote> voteList = DbAppManager.getInst().getVoteItemList(ss.scode, data.boardid);
		return res.setError(EAllError.ok).setParam("data", voteList);
	};


	/** 
	 * select item of vote of board
	 * @param ss
	 * @param res
	 * @param userData, [board id][vote item id][is select or unselect,true or false]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> selectVoteItem = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		SelectVote data = new RecDataBoard().new SelectVote(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		RecVoteUser voteuser = DbAppManager.getInst().getVoteUser(ss.scode, ss.getUserId(), data.boardid);
		
		if(data.isselect==true && voteuser!=DbRecord.Empty)
			return res.setError(EAllError.AlreadyVoteUser);
		else if(data.isselect==false && voteuser==DbRecord.Empty)
			return res.setError(EAllError.NotExistVoteUser);
		if(data.isselect==true && DbAppManager.getInst().addVoteUser(ss.scode, ss.getUserId(), data.boardid, data.vitemid)==false)
			return res.setError(EAllError.FailAddVoteUser);
		else if(data.isselect==false && DbAppManager.getInst().delVoteUser(ss.scode, ss.getUserId(), data.boardid)==false)
			return res.setError(EAllError.FailDelVoteUser);
		
		DbAppManager.getInst().updateVoteSelection(ss.scode, data.boardid, data.vitemid, data.isselect);
		return res.setError(EAllError.ok);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> updateVote = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		VoteUpdate data;
		try {
			data = new RecDataBoard().new VoteUpdate(jnode.toString());
		} catch (Exception e) {
			return res.setError(EAllError.unknown_error);
		}
		if(ss==null)
			return res.setError(EAllError.NoSession);
		RecVoteInfo vi = DbAppManager.getInst().getVoteInfo(ss.scode, data.getBoardid());
		if(vi == DbRecord.Empty)
			return res.setError(EAllError.NotExistVoteInfo);
		else if(vi.userid.equals(ss.getUserId())==false)
			return res.setError(EAllError.PermissionDeny);
		else if(vi.expiretime < System.currentTimeMillis() || vi.isclosed == true)
			return res.setError(EAllError.AlreadyExpired);
			
		if(data.getExpiretime()!=null && data.getExpiretime()>0)
			DbAppManager.getInst().updateVoteExpireTime(ss.scode, data.getBoardid(), ss.getUserId(), data.getExpiretime());
		if(data.getIsclose() != null)
			DbAppManager.getInst().updateVoteClose(ss.scode, data.getBoardid(), ss.getUserId(), data.getIsclose());
		if(data.getVoteitems()!=null) {
			List<VoteItemData> voteitems = data.getVoteitems();
			for(VoteItemData voteitem : voteitems)
				DbAppManager.getInst().updateVoteitemText(ss.scode, data.getBoardid(), voteitem.getVitemid(), voteitem.getVotetext());
		}
		return res.setError(EAllError.ok);
	};

	/** 
	 * change vote item selection
	 * @param ss
	 * @param res
	 * @param userData, [board id][new vote item id]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> changeVoteSelection = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChangeVoteSelection data = new RecDataBoard().new ChangeVoteSelection(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		if(DbAppManager.getInst().changeSelectItem(ss.scode, ss.getUserId(), data.boardid, data.vitemid)==false)
			return res.setError(EAllError.NotExistVoteInfo);
		return res.setError(EAllError.ok).setParam(data.boardid+ASS.UNIT+data.vitemid);
	};
	
	/** 
	 * get vote info list
	 * @param ss
	 * @param res
	 * @param userData, {[board id]}
	 * @return {[board id][expire time][is closed]}, if exist
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> getVoteInfoList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		VoteInfoList data = new RecDataBoard().new VoteInfoList(jnode);
		if(ss==null)
			return res.setError(EAllError.NoSession);
		
		List<RecVoteInfo> vinfolist = DbAppManager.getInst().getVoteInfoList(ss.scode, data.boardids);
		if(vinfolist.size()<1)
			return res.setError(EAllError.NoListData);

		return res.setError(EAllError.ok).setParam("data", vinfolist);
	};
	
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> boardSearch = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		SearchBoard data = new RecDataBoard().new SearchBoard(jnode);	//SearchBoard.count value is fixed 20.
		String categoryId = ss.getTableIdByCategoryIndex(data.getCategoryIndex());
		//ElkBoard elkBoard = new ElkBoard(categoryIndex, null, data.getSearch(), data.getSearch()); 
		try {
			String jsonResult = this.boardElasticSearch.searchBoardByRest(getBoardSearchQuery(categoryId, data));
			if(jsonResult == null || jsonResult.length() < 1)
				return res.setError(EAllError.invalid_search);
			JsonNode jsonNode = new ObjectMapper().readTree(jsonResult);
			if(jsonNode == NullNode.instance)
				return res.setError(EAllError.invalid_search);
			JsonNode hitsNode = jsonNode.get("hits");
			if(hitsNode == null )
				return res.setError(EAllError.empty_search);
			JsonNode totalNode = hitsNode.get("total");
			if(totalNode==null || totalNode.asInt() == 0)
				return res.setError(EAllError.no_search_result);
			
			List<String> boardids = this.boardElasticSearch.copySearshResultToResponse((ArrayNode) hitsNode.get("hits"));
			List<RecBoardDetail> boardList = DbAppManager.getInst().getBoardDetailList(ss.scode, boardids);	//load all list
			if(boardList.size()<1)
				return res.setError(EAllError.NoListData);
			
			res.setParam("category", data.getCategoryIndex());
			res.setParam("data", boardList);
			return res.setError(EAllError.ok);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.setError(EAllError.unknown_error);
	};
	
	private ResponseData<EAllError> addBoard(AuthSession ss, ResponseData<EAllError> res, AddBoard data, List<String> scrapIds) {
		String categoryId = ss.getTableIdByCategoryIndex(data.getCategoryInex());
		if(categoryId==null)
			return res.setError(EAllError.InvalidCategoryId);
		String boardId = StrUtil.getSha1Uuid("brd");
		if(DbAppManager.getInst().addBoardShort(ss.scode, boardId, data.itemtype, data.title, getShortContent(data.content), data.hasimage,
				data.hasfile, categoryId, "", ss.getUserId(), ss.getUsername())==false) 		//insert content's shortcut
			return res.setError(EAllError.FailAddBoard);
		
		DbAppManager.getInst().addBoardContent(ss.scode, boardId, data.content);	//insert content
		DbAppManager.getInst().addBoardCount(ss.scode, boardId);
			
		if(data.getFileids().size() > 0) {
			DbAppManager.getInst().updateFilesEnabled(ss.scode, data.getFileids(), boardId, true);	//업로딩된 파일을 enabled 시킴. enabled=false은 주기적으로 삭제 필요
			RecFile recfile = DbAppManager.getInst().getFileInfo(ss.scode, data.getFileids().get(0));
			if(makeCrop(ss.scode, boardId, recfile) != null)
				DbAppManager.getInst().addCropFile(ss.scode, boardId, recfile.fileserver, UploadFile.CROP_PATH, boardId);
		}
		if(scrapIds != null) {
			List<String> queries = new ArrayList<>();
			for(String scrapid : scrapIds)
				queries.add(DbTransaction.getInst().queryInsertScrapId(boardId, scrapid));
			if(queries.size()>0)
				DbTransaction.getInst().transactionQuery(ss.scode, queries);
		}
		this.boardElasticSearch.addBoard(boardId, categoryId, ss.getUsername(), data);
		return res.setError(EAllError.ok).setParam("boardid", boardId);
	}

	private List<String> findAndAddScrap(AuthSession ss, AddBoard data) {
		List<String> list = StrUtil.extractUrls(data.content);
		if(list.size()<1)
			return null;
		List<HtmlNode> htmls = list.stream().map(x -> HtmlScrapper.doScrap(x)).filter(x -> x.isEmpty()==false).collect(Collectors.toList());
		List<String> urls = htmls.stream().map(x -> x.getUrl()).collect(Collectors.toList());
		
		List<String> dbUrls = DbAppManager.getInst().getScrapListByUrl(ss.scode, urls).stream().map(x->x.getUrl()).collect(Collectors.toList());
		htmls.removeIf(x -> dbUrls.contains(x.getUrl()));
		
		for(HtmlNode node : htmls) {
			DbTransaction.getInst().insertTransactionalScrap(ss.scode, node.getScrapid(), node.getUrl(), node.getMainTitle(), node.getSubTitle(), node.getShortBody());
			makeScrapImage(ss.scode, node);
		}
		return DbAppManager.getInst().getScrapListByUrl(ss.scode, urls).stream().map(x->x.getScrapid()).collect(Collectors.toList());
	}
	
	private ResponseData<EAllError> updateBoard(AuthSession ss, ResponseData<EAllError> res, UpdateBoard data, List<String> scrapIds) {
		String categoryId = ss.getTableIdByCategoryIndex(data.getCategoryInex());
		if(categoryId==null)
			return res.setError(EAllError.InvalidCategoryId);
	
		if(DbAppManager.getInst().updateBoard(ss.scode, ss.getUserId(), data.boardid, data.title, getShortContent(data.content), data.hasimage, data.hasfile, categoryId)==false)
			return res.setError(EAllError.FailUpdate);
		DbAppManager.getInst().updateBoardContent(ss.scode, data.boardid, data.content);
		DbAppManager.getInst().updateDeleteFile(ss.scode, data.boardid);
		DbAppManager.getInst().updateFilesEnabled(ss.scode, data.getFileids(), data.boardid, true);
		{
			DbAppManager.getInst().deleteBoardScrap(ss.scode, data.boardid);
			List<String> queries = new ArrayList<>();
			for(String scrapid : scrapIds)
				queries.add(DbTransaction.getInst().queryInsertScrapId(data.boardid, scrapid));
			if(queries.size()>0)
				DbTransaction.getInst().transactionQuery(ss.scode, queries);
		}	

		return res.setError(EAllError.ok).setParam("boardid", data.boardid);
	}
	
	public String makeCrop(String scode, String boardId, RecFile recfile) {
		if(recfile==null || recfile.thumbname==null)
			return null;
		String thumbPath = UploadFile.getThumbPath(scode, servicesConfig.getFileUploadDir());
		String cropPath = UploadFile.getCropPath(scode, servicesConfig.getFileUploadDir());
		File cropDir = new File(cropPath);
		if(cropDir.exists()==false)
			cropDir.mkdirs();
		
		imageResizeWorker.doCrop(thumbPath + recfile.thumbname, cropPath + boardId, recfile.thumbwidth/2, recfile.thumbheight/2, new ImageResizerCallback() {
			@Override
			public void onCompleted(Object dest) {
				System.out.println(dest);
			}
			@Override
			public void onFailed(Object src) {
				System.out.println(src);
			}
		});
		return cropPath + boardId;
	}

	Executor executor = Executors.newFixedThreadPool(20);
	private final float MAX_SCRAP_SIZE = 256f;
	private void makeScrapImage(final String scode, final HtmlNode htmlNode) {
		Runnable runnable = () -> {
			try {
				URL url = new URL(htmlNode.getImageUrl());
				String localSrc = servicesConfig.getFileUploadDir() + "/scrap/" + htmlNode.getScrapid();
				String localDest = servicesConfig.getFileUploadDir() + "/scrapcrop/" + htmlNode.getScrapid();
				
		        InputStream in = url.openStream();
		        OutputStream out = new BufferedOutputStream(new FileOutputStream(localSrc));

		        int n = 0;
				byte[] buf = new byte[4096];
		        while( -1 != (n=in.read(buf))) {
		        	out.write(buf);
		        }
		        out.close();
		        in.close();
		        
		        ImageSize imgSize = ImageUtil.getImageSize(localSrc);
		        float rate = MAX_SCRAP_SIZE / (imgSize.width > imgSize.height ? (float)imgSize.width : (float)imgSize.height);
		        imageResizeWorker.doCrop(localSrc, localDest, (int)(imgSize.width * rate), (int)(imgSize.height * rate), new ImageResizerCallback() {
					@Override
					public void onCompleted(Object dest) {
						DbAppManager.getInst().updateScrap(scode, htmlNode.getScrapid(), StrUtil.getHostIp(), "scrapcrop");
						new File(localSrc).delete();
					}
					@Override
					public void onFailed(Object src) {
						System.out.println(src);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		executor.execute(runnable);
	}
	
/*	public String getBoardSearchQuery(ElkBoard elkBoard, int offset, int count) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrNode = mapper.createArrayNode();
		
		List<ObjectNode> nodeList = new ArrayList<>();
		if(elkBoard.getWriter() != null)
			nodeList.add(mapper.createObjectNode().put("writer", elkBoard.getWriter()));
		if(elkBoard.getVillage() != null) 
			nodeList.add(mapper.createObjectNode().put("village", elkBoard.getVillage()));
		if(elkBoard.getTitle() != null) 
			nodeList.add(mapper.createObjectNode().put("title", elkBoard.getTitle()));
		if(elkBoard.getContent() != null) 
			nodeList.add(mapper.createObjectNode().put("content", elkBoard.getContent()));
		
		nodeList.stream().map(node -> mapper.createObjectNode().set("match", node)).forEach(node->arrNode.add(node));
		if(arrNode.size()<1)
			return null;
		ObjectNode rootNode = mapper.createObjectNode();
//		JsonNode resultNode = rootNode.set("query", mapper.createObjectNode().set("bool", mapper.createObjectNode().set("should", arrNode)));
		rootNode.set("query", mapper.createObjectNode().set("bool", mapper.createObjectNode().set("should", arrNode)));
		rootNode.put("from", offset);
		rootNode.put("size", count);
		rootNode.set("_source", getSources(mapper));
		String json = mapper.writeValueAsString(rootNode);
		return json;
	}*/
	
	public String getBoardSearchQuery(String categoryId, SearchBoard search) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", search.getOffset());
		rootNode.put("size", search.getCount());
		rootNode.set("_source", getSources(mapper));
		rootNode.set("query", mapper.createObjectNode().set("bool", getQuery(mapper, categoryId, search, false)));
		return mapper.writeValueAsString(rootNode);
	}
	
	public ArrayNode getSources(ObjectMapper mapper) {
		ArrayNode arrNode = mapper.createArrayNode();
		return arrNode.add("boardid");
	}
	
	public JsonNode getQuery(ObjectMapper mapper, String categoryId, SearchBoard search, boolean compareBody) {
		ObjectNode queryNode = mapper.createObjectNode();
		queryNode.set("must", getMust(mapper, categoryId));
		queryNode.set("should", getShould(mapper, search, compareBody));
		return queryNode;
	}
	
	public ArrayNode getMust(ObjectMapper mapper, String categoryId) {
		ArrayNode arrNode = mapper.createArrayNode();
		arrNode.add(mapper.createObjectNode().set("match", mapper.createObjectNode().put("category", categoryId)));
		return arrNode;
	}
	
	public ArrayNode getShould(ObjectMapper mapper, SearchBoard search, boolean compareBody) {
		ArrayNode arrNode = mapper.createArrayNode();
		arrNode.add(mapper.createObjectNode().set("match", mapper.createObjectNode().put("title", search.getSearch())));
		if(compareBody == true)
			arrNode.add(mapper.createObjectNode().set("match", mapper.createObjectNode().put("content", search.getSearch())));
		return arrNode;
	}
	
}
