package com.ccz.appinall.services.controller.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.friend.RecDataFriend.*;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.enums.EFriendStatus;
import com.ccz.appinall.services.model.db.RecFriend;
import com.ccz.appinall.services.model.db.RecFriend.RecFriendInfo;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FriendCommandAction extends CommonAction {
	@Autowired
	ChAttributeKey chAttributeKey;
	public FriendCommandAction() {
		super.setCommandFunction(EAllCmd.addfriend, addFriend); //O
		super.setCommandFunction(EAllCmd.delfriend, delFriend);
		super.setCommandFunction(EAllCmd.changefriendstatus,changeFriendStatus); //O
		super.setCommandFunction(EAllCmd.friendids, friendsIdList); //O
		super.setCommandFunction(EAllCmd.friendcnt, friendsCount); //O
		super.setCommandFunction(EAllCmd.friendinfos, friendsInfo); //O
		super.setCommandFunction(EAllCmd.appendme, friendMeUser); //O
		super.setCommandFunction(EAllCmd.blockme, friendMeUser); //O
		super.setCommandFunction(EAllCmd.appendmecnt, appendMeCount); //O
		super.setCommandFunction(EAllCmd.blockmecnt, blockMeCount); //O
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EAllError> res = new ResponseData<EAllError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EAllError>) cmdFunc.doAction(session, res, jdata);
			send(ch, res.toJsonString());
			return true;
		}
		return false;
	}

	
	/** 
	 * add friend
	 * @param ss
	 * @param res
	 * @param userData, user type | { [userid][username] / .. }
	 * 		  user type=> 'u' means user
	 * @return added userid list delimited by RECORD
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> addFriend = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		AddFriend data = new RecDataFriend().new AddFriend(jnode);
		List<String> addedFriend = new ArrayList<>();
		for(IdName friend : data.friendlist) {
			if(DbAppManager.getInst().addFriend(ss.scode, ss.getUserId(), friend.userid, friend.username, data.friendtype)==true) //if wonder about performance, use preparedStatement for batch
				addedFriend.add(friend.userid+ASS.UNIT+friend.username);
		}
		String param = addedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	/** 
	 * del friend
	 * @param ss
	 * @param res
	 * @param userData, { userid / .. }
	 * @return deleted userid list delimited by RECORD
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> delFriend = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		DelFriend data = new RecDataFriend().new DelFriend(jnode);
		List<String> deletedFriend = new ArrayList<>();
		for(String friend : data.friendids)
			if(DbAppManager.getInst().delFriend(ss.scode, ss.getUserId(), friend)==true)
				deletedFriend.add(friend);
		String param = deletedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};

	/** 
	 * change friend status
	 * @param ss
	 * @param res
	 * @param userData, { [userid][status] / .. }
	 * 		  status => 0 normal, 1 block, 2 blacklist
	 * @return updated userid list delimited by RECORD
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> changeFriendStatus = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChangeFriendStatus data = new RecDataFriend().new ChangeFriendStatus(jnode);
		List<String> updatedFriend = new ArrayList<>();
		for(IdStatus friend : data.friendstatus) {
			if(DbAppManager.getInst().updateFriendStatus(ss.scode, ss.getUserId(), friend.userid, friend.estatus)==true)
				updatedFriend.add(friend.userid);
		}
		String param = updatedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, [status][offset][count]
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> friendsIdList = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		Friendids data = new RecDataFriend().new Friendids(jnode);
		List<RecFriend> friendList = DbAppManager.getInst().getFriendList(ss.scode, ss.getUserId(), data.estatus, data.offset, data.count);
		if(friendList.size()<1)
			return res.setError(EAllError.eNoListData);
		
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus, ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * get friend count
	 * @param ss
	 * @param res
	 * @param userData, status
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return [status][count]
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> friendsCount = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		FriendCount data = new RecDataFriend().new FriendCount(jnode);
		int count = DbAppManager.getInst().getFriendCount(ss.scode, ss.getUserId(), data.estatus);
		String param = String.format("%d%s%d", data.estatus, ASS.UNIT, count);
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, { [userid] / .. }
	 * 		  user type => u , ..
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> friendsInfo = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		FriendInfos data = new RecDataFriend().new FriendInfos(jnode);
		List<RecFriend> friendList = DbAppManager.getInst().getFriendInfoList(ss.scode, ss.getUserId(), data.friendids);
		if(friendList.size()<1)
			return res.setError(EAllError.eNoListData);
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus, ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, [offset] [count]
	 * @return { [user id][user name[[user type][email]/ ... }
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> friendMeUser = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		AppendMe data = new RecDataFriend().new AppendMe(jnode);
		List<RecFriendInfo> infoList = DbAppManager.getInst().getFriendMeList(ss.scode, ss.getUserId(), 
									   data.estatus, data.offset, data.count);
		if(infoList.size()<1)
			return res.setError(EAllError.eNoListData);
		String param = infoList.stream()
				.map(e->String.format("%s%s%s%s%s%s%s", e.userid, ASS.UNIT, e.username, ASS.UNIT, e.usertype, ASS.UNIT, e.email))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};

	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return  count
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> appendMeCount = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.friend);
		return res.setError(EAllError.ok).setParam(""+count);
	};

	/** 
	 * user list of block me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return count
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> blockMeCount = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.block);
		return res.setError(EAllError.ok).setParam(""+count);
	};

}
