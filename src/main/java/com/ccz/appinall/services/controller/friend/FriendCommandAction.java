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
import com.ccz.appinall.services.controller.file.FileCommandAction;
import com.ccz.appinall.services.controller.friend.RecDataFriend.*;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.EFileError;
import com.ccz.appinall.services.enums.EFriendCmd;
import com.ccz.appinall.services.enums.EFriendError;
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
		super.setCommandFunction(EFriendCmd.addfriend.getValue(), addFriend); //O
		super.setCommandFunction(EFriendCmd.delfriend.getValue(), delFriend);
		super.setCommandFunction(EFriendCmd.changefriendstatus.getValue(),changeFriendStatus); //O
		super.setCommandFunction(EFriendCmd.friendids.getValue(), friendsIdList); //O
		super.setCommandFunction(EFriendCmd.friendcnt.getValue(), friendsCount); //O
		super.setCommandFunction(EFriendCmd.friendinfos.getValue(), friendsInfo); //O
		super.setCommandFunction(EFriendCmd.appendme.getValue(), friendMeUser); //O
		super.setCommandFunction(EFriendCmd.blockme.getValue(), friendMeUser); //O
		super.setCommandFunction(EFriendCmd.appendmecnt.getValue(), appendMeCount); //O
		super.setCommandFunction(EFriendCmd.blockmecnt.getValue(), blockMeCount); //O
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EFriendError> res = new ResponseData<EFriendError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EFriendError>) cmdFunc.doAction(session, res, jdata);
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
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> addFriend = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		AddFriend data = new RecDataFriend().new AddFriend(jnode);
		List<String> addedFriend = new ArrayList<>();
		for(IdName friend : data.friendlist) {
			if(DbAppManager.getInst().addFriend(ss.scode, ss.getUserId(), friend.userid, friend.username, data.friendtype)==true) //if wonder about performance, use preparedStatement for batch
				addedFriend.add(friend.userid+ASS.UNIT+friend.username);
		}
		String param = addedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};
	/** 
	 * del friend
	 * @param ss
	 * @param res
	 * @param userData, { userid / .. }
	 * @return deleted userid list delimited by RECORD
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> delFriend = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		DelFriend data = new RecDataFriend().new DelFriend(jnode);
		List<String> deletedFriend = new ArrayList<>();
		for(String friend : data.friendids)
			if(DbAppManager.getInst().delFriend(ss.scode, ss.getUserId(), friend)==true)
				deletedFriend.add(friend);
		String param = deletedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};

	/** 
	 * change friend status
	 * @param ss
	 * @param res
	 * @param userData, { [userid][status] / .. }
	 * 		  status => 0 normal, 1 block, 2 blacklist
	 * @return updated userid list delimited by RECORD
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> changeFriendStatus = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		ChangeFriendStatus data = new RecDataFriend().new ChangeFriendStatus(jnode);
		List<String> updatedFriend = new ArrayList<>();
		for(IdStatus friend : data.friendstatus) {
			if(DbAppManager.getInst().updateFriendStatus(ss.scode, ss.getUserId(), friend.userid, friend.estatus)==true)
				updatedFriend.add(friend.userid);
		}
		String param = updatedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, [status][offset][count]
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> friendsIdList = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		Friendids data = new RecDataFriend().new Friendids(jnode);
		List<RecFriend> friendList = DbAppManager.getInst().getFriendList(ss.scode, ss.getUserId(), data.estatus, data.offset, data.count);
		if(friendList.size()<1)
			return res.setError(EFriendError.eNoListData);
		
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus.getValue(), ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};
	
	/** 
	 * get friend count
	 * @param ss
	 * @param res
	 * @param userData, status
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return [status][count]
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> friendsCount = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		FriendCount data = new RecDataFriend().new FriendCount(jnode);
		int count = DbAppManager.getInst().getFriendCount(ss.scode, ss.getUserId(), data.estatus);
		String param = String.format("%d%s%d", data.estatus.getValue(), ASS.UNIT, count);
		return res.setError(EFriendError.eOK).setParam(param);
	};
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, { [userid] / .. }
	 * 		  user type => u , ..
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> friendsInfo = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		FriendInfos data = new RecDataFriend().new FriendInfos(jnode);
		List<RecFriend> friendList = DbAppManager.getInst().getFriendInfoList(ss.scode, ss.getUserId(), data.friendids);
		if(friendList.size()<1)
			return res.setError(EFriendError.eNoListData);
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus.getValue(), ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};
	
	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, [offset] [count]
	 * @return { [user id][user name[[user type][email]/ ... }
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> friendMeUser = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		AppendMe data = new RecDataFriend().new AppendMe(jnode);
		List<RecFriendInfo> infoList = DbAppManager.getInst().getFriendMeList(ss.scode, ss.getUserId(), 
									   data.estatus, data.offset, data.count);
		if(infoList.size()<1)
			return res.setError(EFriendError.eNoListData);
		String param = infoList.stream()
				.map(e->String.format("%s%s%s%s%s%s%s", e.userid, ASS.UNIT, e.username, ASS.UNIT, e.usertype, ASS.UNIT, e.email))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	};

	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return  count
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> appendMeCount = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.friend);
		return res.setError(EFriendError.eOK).setParam(""+count);
	};

	/** 
	 * user list of block me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return count
	 */
	ICommandFunction<AuthSession, ResponseData<EFriendError>, JsonNode> blockMeCount = (AuthSession ss, ResponseData<EFriendError> res, JsonNode jnode) -> {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.block);
		return res.setError(EFriendError.eOK).setParam(""+count);
	};

}
