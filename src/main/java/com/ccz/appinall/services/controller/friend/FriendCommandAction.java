package com.ccz.appinall.services.controller.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.FileCommandAction;
import com.ccz.appinall.services.controller.friend.RecDataFriend.*;
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
	}

	private boolean processBoardData(Channel ch, String[] data, JsonNode jdata) {
		ResponseData<EFriendError> res = null;
		if(data != null)
			res = new ResponseData<EFriendError>(data[0], data[1], data[2]);
		else
			res = new ResponseData<EFriendError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		AuthSession ss = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		switch(EFriendCmd.getType(res.getCommand())) {
		case addfriend: 
			this.addFriend(ss, res, data != null ?new RecDataFriend().new AddFriend(data[3]) : new RecDataFriend().new AddFriend(jdata)); //O
			break;
		case delfriend:
			this.delFriend(ss, res, data != null ?new RecDataFriend().new DelFriend(data[3]) : new RecDataFriend().new DelFriend(jdata)); //O
			break;
		case changefriendstatus:
			this.changeFriendStatus(ss, res, data != null ?new RecDataFriend().new ChangeFriendStatus(data[3]) : new RecDataFriend().new ChangeFriendStatus(jdata)); //O
			break;
		case friendids:
			this.friendsIdList(ss, res, data != null ?new RecDataFriend().new Friendids(data[3]) : new RecDataFriend().new Friendids(jdata)); //O
			break;
		case friendcnt:
			this.friendsCount(ss, res, data != null ? data[3] : jdata.get("status").asText()); //O
			break;
		case friendinfos:
			this.friendsInfo(ss, res, data != null ?new RecDataFriend().new FriendInfos(data[3]) : new RecDataFriend().new FriendInfos(jdata)); //O
			break;
		case appendme:
			this.friendMeUser(ss, res, data != null ?new RecDataFriend().new AppendMe(data[3]) : new RecDataFriend().new AppendMe(jdata)); //O
			break;
		case blockme:
			this.friendMeUser(ss, res, data != null ?new RecDataFriend().new BlockMe(data[3]) : new RecDataFriend().new BlockMe(jdata)); //O
			break;
		case appendmecnt:
			this.appendMeCount(ss, res, null); //O
			break;
		case blockmecnt:
			this.blockMeCount(ss, res, null); //O
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
	 * add friend
	 * @param ss
	 * @param res
	 * @param userData, user type | { [userid][username] / .. }
	 * 		  user type=> 'u' means user
	 * @return added userid list delimited by RECORD
	 */
	private ResponseData<EFriendError> addFriend(AuthSession ss, ResponseData<EFriendError> res, AddFriend rec) {
		List<String> addedFriend = new ArrayList<>();
		for(IdName friend : rec.friendlist) {
			if(DbAppManager.getInst().addFriend(ss.scode, ss.getUserId(), friend.userid, friend.username, rec.friendtype)==true) //if wonder about performance, use preparedStatement for batch
				addedFriend.add(friend.userid+ASS.UNIT+friend.username);
		}
		String param = addedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}
	/** 
	 * del friend
	 * @param ss
	 * @param res
	 * @param userData, { userid / .. }
	 * @return deleted userid list delimited by RECORD
	 */
	private ResponseData<EFriendError> delFriend(AuthSession ss, ResponseData<EFriendError> res, DelFriend rec) {
		List<String> deletedFriend = new ArrayList<>();
		for(String friend : rec.friendids)
			if(DbAppManager.getInst().delFriend(ss.scode, ss.getUserId(), friend)==true)
				deletedFriend.add(friend);
		String param = deletedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}

	/** 
	 * change friend status
	 * @param ss
	 * @param res
	 * @param userData, { [userid][status] / .. }
	 * 		  status => 0 normal, 1 block, 2 blacklist
	 * @return updated userid list delimited by RECORD
	 */
	private ResponseData<EFriendError> changeFriendStatus(AuthSession ss, ResponseData<EFriendError> res, ChangeFriendStatus rec) {
		List<String> updatedFriend = new ArrayList<>();
		for(IdStatus friend : rec.friendstatus) {
			if(DbAppManager.getInst().updateFriendStatus(ss.scode, ss.getUserId(), friend.userid, friend.estatus)==true)
				updatedFriend.add(friend.userid);
		}
		String param = updatedFriend.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, [status][offset][count]
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	private ResponseData<EFriendError> friendsIdList(AuthSession ss, ResponseData<EFriendError> res, Friendids rec) {
		List<RecFriend> friendList = DbAppManager.getInst().getFriendList(ss.scode, ss.getUserId(), rec.estatus, rec.offset, rec.count);
		if(friendList.size()<1)
			return res.setError(EFriendError.eNoListData);
		
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus.getValue(), ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}
	
	/** 
	 * get friend count
	 * @param ss
	 * @param res
	 * @param userData, status
	 * 		  status => 0 normal, 1 block, 2 blacklist, 9 all user
	 * @return [status][count]
	 */
	private ResponseData<EFriendError> friendsCount(AuthSession ss, ResponseData<EFriendError> res, String userData) {
		EFriendStatus friendstatus = EFriendStatus.getType(userData);
		int count = DbAppManager.getInst().getFriendCount(ss.scode, ss.getUserId(), friendstatus);
		String param = String.format("%d%s%d", friendstatus.getValue(), ASS.UNIT, count);
		return res.setError(EFriendError.eOK).setParam(param);
	}
	
	/** 
	 * get friend id list
	 * @param ss
	 * @param res
	 * @param userData, { [userid] / .. }
	 * 		  user type => u , ..
	 * @return {[friend id][friend name][friend status][friend type]/...}
	 */
	private ResponseData<EFriendError> friendsInfo(AuthSession ss, ResponseData<EFriendError> res, FriendInfos rec) {
		List<RecFriend> friendList = DbAppManager.getInst().getFriendInfoList(ss.scode, ss.getUserId(), rec.friendids);
		if(friendList.size()<1)
			return res.setError(EFriendError.eNoListData);
		String param = friendList.stream()
				.map(e->String.format("%s%s%s%s%d%s%s", e.friendid, ASS.UNIT, e.friendname, ASS.UNIT, e.friendstatus.getValue(), ASS.UNIT, e.friendtype))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}
	
	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, [offset] [count]
	 * @return { [user id][user name[[user type][email]/ ... }
	 */
	private ResponseData<EFriendError> friendMeUser(AuthSession ss, ResponseData<EFriendError> res, AppendMe rec) {
		List<RecFriendInfo> infoList = DbAppManager.getInst().getFriendMeList(ss.scode, ss.getUserId(), 
									   rec.estatus, rec.offset, rec.count);
		if(infoList.size()<1)
			return res.setError(EFriendError.eNoListData);
		String param = infoList.stream()
				.map(e->String.format("%s%s%s%s%s%s%s", e.userid, ASS.UNIT, e.username, ASS.UNIT, e.usertype, ASS.UNIT, e.email))
				.collect(Collectors.joining(ASS.RECORD));
		return res.setError(EFriendError.eOK).setParam(param);
	}

	/** 
	 * user list of append me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return  count
	 */
	private ResponseData<EFriendError> appendMeCount(AuthSession ss, ResponseData<EFriendError> res, String userData) {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.friend);
		return res.setError(EFriendError.eOK).setParam(""+count);
	}

	/** 
	 * user list of block me by friend
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return count
	 */
	private ResponseData<EFriendError> blockMeCount(AuthSession ss, ResponseData<EFriendError> res, String userData) {
		int count = DbAppManager.getInst().getFriendMeCount(ss.scode, ss.getUserId(), EFriendStatus.block);
		return res.setError(EFriendError.eOK).setParam(""+count);
	}

}
