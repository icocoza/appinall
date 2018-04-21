package com.ccz.appinall.common.rdb;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;

import com.ccz.appinall.library.dbhelper.DbConnMgr;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.enums.*;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.services.model.db.RecChMime.RecChMimeExt;
import com.ccz.appinall.services.model.db.RecChannel.RecChLastMsg;
import com.ccz.appinall.services.model.db.RecFriend.RecFriendInfo;
import com.ccz.appinall.services.model.db.RecMessageDel.RecDelId;

public class DbAppManager {
	public static DbAppManager s_pThis;
	
	public static DbAppManager getInst() {
		return s_pThis = (s_pThis == null ? new DbAppManager() : s_pThis);
	}
	public static void freeInst() {		s_pThis = null; 	}
	
	//// DbManager methods
	String dbUrl, dbUser, dbPw, dbOptions;
	String poolName;
	public boolean createAdminDatabase(String url, String dbName, String options, String user, String pw) {
		this.dbUser = user;
		this.dbUrl = url;
		this.dbPw = pw;
		return new DatabaseMaker().createDatabase(url, dbName, options, user, pw);
	}
	
	public boolean initAdmin(String poolName, String url, String dbName, String options, String user, String pw, int initPool, int maxPool) {
		try {
			this.poolName = poolName;
			this.dbUrl = url;
			this.dbUser = user;
			this.dbOptions = options;
			this.dbPw = pw;
			DbConnMgr.getInst().createConnectionPool(poolName, "jdbc:mysql://"+url+"/"+dbName+"?"+options, user, pw, initPool, maxPool);
			new RecAdminUser(poolName).createTable();
			new RecAdminToken(poolName).createTable();
			new RecAdminApp(poolName).createTable();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean initAdminApp() {
		List<RecAdminApp> list = this.getAppList(EAdminAppStatus.all, 0, Integer.MAX_VALUE);
		for(RecAdminApp item: list)
			if(this.initApp(item.scode, 2, 4) == false)
				return false;
		return true;
	}
	
	public void freeAdmin() {
		DbConnMgr.getInst().removeConnectionPool(poolName);
	}
	
	//for admin user
	public boolean addAdminUser(String email, String password, EAdminStatus adminstatus, EUserRole userrole, String username, String nationality) {
		return new RecAdminUser(poolName).insert(email, password, adminstatus, userrole, username, nationality);
	}

	public boolean addAdminUser(String email, String password, EAdminStatus adminstatus, EUserRole userrole, String username, Date birthday, String nationality, int sex) {
		return new RecAdminUser(poolName).insert(email, password, adminstatus, userrole, username, birthday, nationality, sex);
	}
	public RecAdminUser getAdminUser(String email) {
		return new RecAdminUser(poolName).getUser(email);
	}
	public boolean updateAdminLastVisit(String email) {
		return new RecAdminUser(poolName).updateLastVisit(email);
	}
	public boolean updateAdminLeave(String email) {
		return new RecAdminUser(poolName).updateLeave(email);
	}

	//for admin token
	public boolean upsertAdminToken(String email, String token, String remoteip) {
		return new RecAdminToken(poolName).upsert(email, token, remoteip);
	}
	
	public boolean delAdminToken(String email) {
		return new RecAdminToken(poolName).delete(email);
	}
	public RecAdminToken getToken(String email) {
		return new RecAdminToken(poolName).getToken(email);
	}
	public boolean updateToken(String email, String token) {
		return new RecAdminToken(poolName).update(email, token);
	}
	public boolean updateTokenLasttime(String email) {
		return new RecAdminToken(poolName).updateLasttime(email);
	}
	
	//for app
	public boolean addApp(String appid, String email, String scode, String title, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String apptoken, String fcmid, String fcmkey) {
		return new RecAdminApp(poolName).insert(appid, email, scode, title, version, updateforce, storeurl, description, status, apptoken, fcmid, fcmkey);
	}
	
	//[TODO] need to consider about same scode collision between others email
	public RecAdminApp getApp(String email, String scode) {
		return new RecAdminApp(poolName).getApp(email, scode);
	}
	
	public RecAdminApp getApp(String appid) {
		return new RecAdminApp(poolName).getApp(appid);
	}
	public boolean updateApp(String email, String appid, String title, String version, boolean updateforce, String storeurl, 
			String description, EAdminAppStatus status, String fcmid, String fcmkey) {
		return new RecAdminApp(poolName).updateApp(email, appid, title, version, updateforce, storeurl, description, status, fcmid, fcmkey);
	}
	public boolean updateAppStatus(String email, String appid, EAdminAppStatus status) {
		return new RecAdminApp(poolName).updateStatus(email, appid, status);
	}
	public List<RecAdminApp> getAppList(EAdminAppStatus status, int offset, int count) {
		return new RecAdminApp(poolName).getList(status, offset, count);
	}
	public List<RecAdminApp> getAppList(String email, EAdminAppStatus status, int offset, int count) {
		return new RecAdminApp(poolName).getList(email, status, offset, count);
	}
	public int getAppCount(String email, EAdminAppStatus status) {
		return new RecAdminApp(poolName).getAppCount(email, status);
	}

	public boolean hasSCode(String scode) {
		return new RecAdminApp(poolName).hasSCode(scode);
	}

	public boolean createAppDatabase(String scode) {
		return new DatabaseMaker().createDatabase(dbUrl, scode, dbOptions, dbUser, dbPw);
	}
	
	public boolean initApp(String scode, int initPool, int maxPool) {
		try {
			DbConnMgr.getInst().createConnectionPool(scode, "jdbc:mysql://"+dbUrl+"/"+scode+"?"+dbOptions, dbUser, dbPw, initPool, maxPool);
			new RecBoard(scode).createTable();
			new RecBoardContent(scode).createTable();
			new RecBoardCount(scode).createTable();
			new RecBoardReply(scode).createTable();
			new RecBoardUser(scode).createTable();
			new RecChannel(scode).createTable();
			new RecChMime(scode).createTable();
			new RecMessage(scode).createTable();
			new RecRead(scode).createTable();
			new RecUser(scode).createTable();
			new RecUserAuth(scode).createTable();
			new RecUserToken(scode).createTable();
			new RecUserVoter(scode).createTable();
			new RecVote(scode).createTable();
			new RecVoteInfo(scode).createTable();
			new RecVoteUser(scode).createTable();
			new RecFriend(scode).createTable();
			new RecMessageDel(scode).createTable();
			new RecPushToken(scode).createTable();
			new RecFile(scode).createTable();
			new RecWebScrab(scode).createTable();
			new RecDeliveryApply(scode).createTable();
			new RecDeliveryHistory(scode).createTable();
			new RecDeliveryOrder(scode).createTable();
			new RecDeliveryPhoto(scode).createTable();
			new RecDeliveryStatus(scode).createTable();
			new RecAddress(scode).createTable();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void freeApp(String scode) {
		DbConnMgr.getInst().removeConnectionPool(scode);
	}
	
	/** 
	 * add user information
	 * @param userid : generated by some rule, server-side
	 * @param uuid : unique id generated by client-side using mac or uuid
	 * @param username 
	 * @param ostype : aos, ios, pc, etc.
	 * @param osversion : 1.2.xxx following os rule
	 * @param appversion : 0.9.0
	 * @param email
	 * @return true if success
	 */
	public RecUser addUser(String scode, String userid, String username, boolean anonymous, String ostype, String osversion, String appversion) {
		return (RecUser) new RecUser(scode).insert(userid, username, anonymous, ostype, osversion, appversion);
	}

	public RecUser getUser(String scode, String userid) {
		return (RecUser) new RecUser(scode).getUser(userid);
	}
	
	public boolean updateAppCode(String scode, String userid, String appcode) {
		return new RecUser(scode).updateAppCode(userid, appcode);
	}
	public boolean updateLasttime(String scode, String userid) {
		return new RecUser(scode).updateLastVisit(userid);
	}
	public boolean updateUserInfo(String scode, String userid, String ostype, String osversion, String appversion) {
		return new RecUser(scode).updateUser(userid, ostype, osversion, appversion);
	}
	
	//for user token
	public RecUserToken getUserTokenByUserId(String scode, String userid) {
		return new RecUserToken(scode).getTokenByUserId(userid);
	}

	public RecUserToken getUserTokenByTokenId(String scode, String tokenid) {
		return new RecUserToken(scode).getTokenByTokenId(tokenid);
	}

	public RecUserToken getUserTokenByUserTokenId(String scode, String userid, String tokenid) {
		return new RecUserToken(scode).getTokenByUserTokenId(userid, tokenid);
	}
	
	public boolean addUserToken(String scode, String userid, String uuid, String tokenid, String token) {
		return new RecUserToken(scode).insertToken(userid, uuid, tokenid, token);
	}
	
	public boolean delUserToken(String scode, String userid) {
		return new RecUserToken(scode).delete(userid);
	}
	
	public boolean enableToken(String scode, String userid, String tokenid, boolean enabled) {
		return new RecUserToken(scode).enableToken(userid, tokenid, enabled);
	}

	public boolean updateToken(String scode, String userid, String tokenid, String token, boolean enabled) {
		return new RecUserToken(scode).updateToken(userid, tokenid, token, enabled);
	}

	//for user authentication
	public RecUserAuth insertUID(String scode, String userid, String uid, String pw) {
		return (RecUserAuth) new RecUserAuth(scode).insertUID(userid, uid, pw);
	}
	
	public RecUserAuth insertEmail(String scode, String userid, String email, String pw) {
		return (RecUserAuth) new RecUserAuth(scode).insertEmail(userid, email);
	}

	public RecUserAuth insertPhoneNo(String scode, String userid, String phoneno, String pw) {
		return (RecUserAuth) new RecUserAuth(scode).insertPhoneNo(userid, phoneno);
	}
	
	public RecUserAuth getUserAuth(String scode, String userid) {
		return new RecUserAuth(scode).getUser(userid);
	}

	public RecUserAuth getUserAuthByUid(String scode, String uid) {
		return new RecUserAuth(scode).getUserByUid(uid);
	}

	public RecUserAuth getUserAuthByEmail(String scode, String email) {
		return new RecUserAuth(scode).getUserByEmail(email);
	}

	public RecUserAuth getUserAuthByPhone(String scode, String phoneno) {
		return new RecUserAuth(scode).getUserByPhone(phoneno);
	}

	public EUserAuthType findUserAuth(String scode, String uid, String email, String phoneno) {
		return new RecUserAuth(scode).findUserAuth(uid, email, phoneno);
	}
	
	public boolean findUid(String scode, String uid) {
		return new RecUserAuth(scode).findUid(uid);
	}
	
	public boolean findEmail(String scode, String email) {
		return new RecUserAuth(scode).findEmail(email);
	}
	
	public boolean findPhoneno(String scode, String phoneno) {
		return new RecUserAuth(scode).findPhoneno(phoneno);
	}
	
	public boolean updatePw(String scode, String uid, String pw) {
		return new RecUserAuth(scode).updatePw(uid, pw);
	}

	public boolean updateEmailCode(String scode, String email, String pw) {
		return new RecUserAuth(scode).updateEmailCode(email, pw);
	}
	
	public boolean updateSMSCode(String scode, String phoneno, String emailcode) {
		return new RecUserAuth(scode).updateSMSCode(phoneno, emailcode);
	}

	public boolean updateUserQuit(String scode, String userid) {
		return new RecUserAuth(scode).updateUserQuit(userid);
	}
	
	public boolean deleteUserId(String scode, String userid) {
		return new RecUserAuth(scode).deleteUserId(userid);
	}
	
	//for user epid for fcm push
	public DbRecord addEpid(String scode, String userid, String devuuid, String epid) {
		return new RecPushToken(scode).insert(devuuid, userid, scode, epid);
	}
	public boolean delEpid(String scode, String devuuid) {
		return new RecPushToken(scode).delete(devuuid);
	}
	public RecPushToken getEpid(String scode, String devuuid) {
		return new RecPushToken(scode).getEpid(devuuid);
	}
	public boolean updateEpid(String scode, String devuuid, String epid) {
		return new RecPushToken(scode).updateEpid(devuuid, epid);
	}
	
	//for push message which be failed
	public DbRecord addFailedPushMsg(String scode, String msgid, String devuuid, String userid, String epid, String msg) {
		return new RecPushFailMsg(scode).insert(msgid, devuuid, userid, epid, msg);
	}
	public boolean delete(String scode, String msgid) {
		return new RecPushFailMsg(scode).delete(msgid);
	}
	public List<RecPushFailMsg> getMsgs(String scode, int offset, int count) {
		return new RecPushFailMsg(scode).getMsgs(offset, count);
	}
	
	/** 
	 * 
	 * @param boardid
	 * @param boardtype
	 * @param title
	 * @param content
	 * @param hasimage
	 * @param hasfile
	 * @param category
	 * @param aptcode
	 * @param createuserid
	 * @return
	 */
	public boolean addBoardShort(String scode, String boardid, EBoardItemType itemtype, String title, String content, boolean hasimage, boolean hasfile, 
			String category, String aptcode, String createuserid, String createusername) {
		return new RecBoard(scode).insert(boardid, itemtype, title, content, hasimage, hasfile, category, aptcode, createuserid, createusername);
	}
	public List<RecBoard> getBoardList(String scode, String category, int offset, int count) {
		return new RecBoard(scode).getList(category, offset, count);
	}
	public List<RecBoard> getBoardList(String scode, String userid, String category, int offset, int count) {
		return new RecBoard(scode).getList(userid, category, offset, count);
	}
	public boolean updateBoardTitle(String scode, String userid, String boardid, String title) {
		return new RecBoard(scode).updateTitle(boardid, userid, title);
	}
	public boolean updateBoardShortContent(String scode, String userid, String boardid, String content, boolean hasimage, boolean hasfile) {
		return new RecBoard(scode).updateContent(boardid, userid, content, hasimage, hasfile);
	}
	public boolean updateBoardCategory(String scode, String userid, String boardid, String category) {
		return new RecBoard(scode).updateCategory(boardid, userid, category);
	}
	public boolean delBoard(String scode, String userid, String boardid) {
		return new RecBoard(scode).delete(userid, boardid);
	}
	public boolean updateBoard(String scode, String userid, String boardid, String title, String content, boolean hasimage, boolean hasfile, String category) {
		return new RecBoard(scode).updateBoard(boardid, userid, title, content, hasimage, hasfile, category);
	}

	// for board content
	public boolean addBoardContent(String scode, String boardid, String content) {
		return new RecBoardContent(scode).insert(boardid, content);
	}
	public String  getBoardContent(String scode, String boardid) {
		return new RecBoardContent(scode).getContent(boardid);
	}
	public boolean updateBoardContent(String scode, String boardid, String content) {
		return new RecBoardContent(scode).updateContent(boardid, content);
	}

	// for board count
	public boolean addBoardCount(String scode, String boardid) {
		return new RecBoardCount(scode).insert(boardid);
	}
	/** 
	 * @param islike, like type if true, else dislike type
	 * @return
	 */
	public boolean addBoardLikeDislike(String scode, String boardid, String userid, String username, EBoardPreference preference) {
		return new RecBoardUser(scode).insert(boardid, userid, username, preference);
	}
	public boolean delBoardLikeDislike(String scode, String boardid, String userid, EBoardPreference preference) {
		return new RecBoardUser(scode).delete(boardid, userid, preference);
	}
	
	public boolean incBoardLike(String scode, String boardid, boolean bInc) {
		return new RecBoardCount(scode).incLike(boardid, bInc);
	}
	public boolean incBoardDislike(String scode, String boardid, boolean bInc) {
		return new RecBoardCount(scode).incDislike(boardid, bInc);
	}
	public boolean incBoardVisit(String scode, String boardid) {
		return new RecBoardCount(scode).incVisit(boardid);
	}
	public boolean incBoardReply(String scode, String boardid) {
		return new RecBoardCount(scode).incReply(boardid);
	}
	
	//for board reply
	public boolean addReply(String scode, String boardid, String parentid, String userid, String username, short depth, String msg) {
		incBoardReply(scode, boardid);
		return new RecBoardReply(scode).insert(boardid, parentid, userid, username, depth, msg);
	}
	public boolean delReply(String scode, String boardid, String replyid, String userid) {
		return new RecBoardReply(scode, replyid).deleteIfNoChild(boardid, userid);
	}
	public List<RecBoardReply> getReplyList(String scode, String boardid, int offset, int count) {
		return new RecBoardReply(scode).getList(boardid, offset, count);
	}
	
	//for voteinfo
	public boolean addVoteInfo(String scode, String boardid, String userid, long expiretime) {
		return new RecVoteInfo(scode).insert(boardid, userid, expiretime);
	}
	public boolean updateVoteExpireTime(String scode, String boardid, String userid, long expiretime) {
		return new RecVoteInfo(scode).updateExpireTime(boardid, userid, expiretime);
	}
	public boolean updateVoteClose(String scode, String boardid, String userid, boolean isclosed) {
		return new RecVoteInfo(scode).updateClose(boardid, userid, isclosed);
	}
	public boolean deleteVoteInfo(String scode, String boardid, String userid) {
		return new RecVoteInfo(scode).delete(boardid, userid);
	}
	public RecVoteInfo getVoteInfo(String scode, String boardid) {
		return new RecVoteInfo(scode).getVoteInfo(boardid);
	}

	public List<RecVoteInfo> getVoteInfoList(String scode, List<String> boardids) {
		return new RecVoteInfo(scode).getVoteInfoList(boardids);
	}
	
	//for vote item
	public boolean addVote(String scode, String boardid, String vitemid, String votetext) {
		return new RecVote(scode).insert(boardid, vitemid, votetext);
	}
	public List<RecVote> getVoteItemList(String scode, String boardid) {
		return new RecVote(scode).getVoteItemList(boardid);
	}
	public boolean updateVoteSelection(String scode, String boardid, String vitemid, boolean bInc) {
		if(bInc == true)
			return new RecVote(scode).incVote(boardid, vitemid);
		return new RecVote(scode).decVote(boardid, vitemid);
	}
	public boolean updateVoteitemText(String scode, String boardid, String vitemid, String votetext) {
		return new RecVote(scode).updateVoteText(boardid, vitemid, votetext);
	}
//	public boolean updateVoteitemUrl(String scode, String boardid, String vitemid, String voteurl) {
//		return new RecVote(scode).updateVoteUrl(boardid, vitemid, voteurl);
//	}
	public boolean deleteVoteitem(String scode, String boardid, String vitemid) {
		if(vitemid==null)
			return new RecVote(scode).delete(boardid);
		return new RecVote(scode).delete(boardid, vitemid);
	}
	
	//for vote user
	public boolean addVoteUser(String scode, String userid, String boardid, String vitemid) {
		return new RecVoteUser(scode).insert(userid, boardid, vitemid);
	}
	public RecVoteUser getVoteUser(String scode, String userid, String boardid) {
		return new RecVoteUser(scode).getVoteUser(userid, boardid);
	}
	public boolean changeSelectItem(String scode, String userid, String boardid, String vitemid) {
		return new RecVoteUser(scode).updateSelectItem(userid, boardid, vitemid);
	}
	public boolean delVoteUser(String scode, String userid, String boardid) {
		return new RecVoteUser(scode).delete(userid, boardid);
	}
	
	//for friend
	public boolean addFriend(String scode, String userid, String friendid, String friendname, String friendtype) {	//default normal = 0
		return new RecFriend(scode).insert(userid, friendid, friendname, friendtype);
	}
	public boolean delFriend(String scode, String userid, String friendid) {
		return new RecFriend(scode).delete(userid, friendid);
	}
	public boolean updateFriendStatus(String scode, String userid, String friendid, EFriendStatus friendstatus) {
		return new RecFriend(scode).updateFriendStatus(userid, friendid, friendstatus);
	}
	public List<RecFriend> getFriendList(String scode, String userid, EFriendStatus friendstatus, int offset, int count) {
		return new RecFriend(scode).getList(userid, friendstatus, offset, count);
	}
	public List<RecFriend> getFriendInfoList(String scode, String userid, List<String> friendids) {
		return new RecFriend(scode).getList(userid, friendids);
	}
	public int getFriendCount(String scode, String userid, EFriendStatus friendstatus) {
		return new RecFriend(scode).getCount(userid, friendstatus);
	}
	public List<RecFriendInfo> getFriendMeList(String scode, String userid, EFriendStatus friendstatus, int offset, int count) {
		return new RecFriend(scode).getFriendMeList(userid, friendstatus, offset, count);
	}
	public int getFriendMeCount(String scode, String userid, EFriendStatus friendstatus) {
		return new RecFriend(scode).getFriendMeCount(userid, friendstatus);
	}
	
	//for channel
	public boolean addChannel(String scode, String chid, String userid1, String attendees, int attendeecnt) {
		return new RecChannel(scode).insert(chid, userid1, attendees, (short)attendeecnt);
	}
	public boolean delChannel(String scode, String chid) {
		return new RecChannel(scode).delete(chid);
	}
	public boolean updateChannelAttendees(String scode, String chid, String attendees, int attendeecnt) { //for 1:N channel
		return new RecChannel(scode).updateAttendee(chid, attendees, (short)attendeecnt);
	}
	public boolean updateChannelAttendees(String scode, String chid, String attendees, int attendeecnt, int type) { //for 1:N channel
		return new RecChannel(scode).updateAttendee(chid, attendees, (short)attendeecnt, (short)type);
	}
	public boolean updateChannelLasttime(String scode, String chid) {
		return new RecChannel(scode).updateLasttime(chid);
	}
	public boolean updateLastMsgAndTime(String scode, String chid, String lastmsg) {
		return new RecChannel(scode).updateLastMsgAndTime(chid, lastmsg);
	}
	public RecChannel getChannel(String scode, String chid) {
		return new RecChannel(scode).getChannel(chid);
	}
	public RecChannel findChannel(String scode, String userid1, String userid2) {
		return new RecChannel(scode).findChannel(userid1, userid2);
	}

	public List<RecChLastMsg> getChannelLastMsg(String scode, List<String> chids) {
		return new RecChannel(scode).getChannelLastMsg(chids);
	}
	
	//for my channel
	public boolean addMyChannel(String scode, String userid, String chid) {
		return new RecChMime(scode).insert(userid, chid);
	}
	public boolean delMyChannel(String scode, String userid, String chid) {
		return new RecChMime(scode).delete(userid, chid);
	}
	public boolean updateMyChannelLastTime(String scode, String chid) {
		return new RecChMime(scode).updateLastTime(chid);
	}
	public RecChMime getMyChannel(String scode, String userid, String chid) {
		return new RecChMime(scode).getChannel(userid, chid);
	}
	public List<RecChMime> getMyChannelList(String scode, String userid, int offset, int count) {
		return new RecChMime(scode).getChannelList(userid, offset, count);
	}
	public List<RecChMimeExt> getMyChannelInfoList(String scode, String userid, int offset, int count) {
		return new RecChMime(scode).getChannelInfoList(userid, offset, count);
	}
	public int getMyChannelCount(String scode, String userid) {
		return new RecChMime(scode).getChannelCount(userid);
	}
	
	//for message
	public boolean addMessage(String scode, String msgid, String chid, String senderid, EMessageType msgtype, String msg) {
		return new RecMessage(scode).insert(msgid, chid, senderid, msgtype, msg);
	}
	public boolean delMessage(String scode, String msgid) {
		return new RecMessage(scode).delete(msgid);
	}
	public boolean delChannelMessage(String scode, String chid) {
		return new RecMessage(scode).deleteChMsg(chid);
	}
	public RecMessage getMessage(String scode, String chid, String msgid) {
		return new RecMessage(scode).getMessage(chid, msgid);
	}
	public List<RecMessage> getMessageList(String scode, String chid, Timestamp joinTime, int offset, int count) {
		return new RecMessage(scode).getMessageList(chid, joinTime, offset, count);
	}
	public List<RecMessage> getMessageListWithoutDeletion(String scode, String chid, Timestamp joinTime, String deleteIdsWithComma, int offset, int count) {
		return new RecMessage(scode).getMessageListWithoutDeletion(chid, joinTime, deleteIdsWithComma, offset, count);
	}
	public boolean incReadCount(String scode, String msgid) {
		return new RecMessage(scode).incReadCount(msgid);
	}
	
	//for read count
	public boolean addReadMsg(String scode, String chid, String userid, String msgid) {
		return new RecRead(scode).insert(chid, userid, msgid);
	}
	
	//for del message
	public boolean delMessage(String scode, String chid, String userid, String msgid) {
		return new RecMessageDel(scode).insert(chid, userid, msgid);
	}
	public List<RecDelId> getDelMessageIdList(String scode, String chid, String userid, Timestamp joinTime) {
		return new RecMessageDel(scode).getDelMessageIdList(chid, userid, joinTime);
	}

	//for images info
	public boolean addFileInit(String scode, String fileid, String userid, String filename, String filetype, long size) {
		return new RecFile(scode).insertFileInit(fileid, userid, filename, filetype, size) != DbRecord.Empty;
	}
	public boolean updateFileInfo(String scode, String fileid, int width, int height, long size, String fileserver) {
		return new RecFile(scode).updateFileInfo(fileid, width, height, size, fileserver) != DbRecord.Empty;
	}
	public boolean updateThumbnail(String scode, String fileid, String thumbname, int thumbwidth, int thumbheight) {
		return new RecFile(scode).updateThumbnail(fileid, thumbname, thumbwidth, thumbheight);
	}
	public boolean delFileInfo(String scode, String fileid) {
		return new RecFile(scode).delete(fileid);
	}
	public RecFile getFileInfo(String scode, String fileid) {
		return new RecFile(scode).getFile(fileid);
	}
	public boolean updateFileEnabled(String scode, String fileid, boolean enabled) {
		return new RecFile(scode).updateFileEnabled(fileid, enabled);
	}
	public boolean updateFilesEnabled(String scode, List<String> fileids, boolean enabled) {
		return new RecFile(scode).updateFilesEnabled(fileids, enabled);
	}
	
	//for webscrab
	public DbRecord addWebScrab(String scode, String webid, String url, String title, String scrabpath, int width, int height) {
		return new RecWebScrab(scode).insert(webid, url, title, scrabpath, width, height);
	}
	public boolean delWebScrab(String scode, String imgid) {
		return new RecWebScrab(scode).delete(imgid);
	}
	public RecWebScrab getWebScrab(String scode, String imgid) {
		return new RecWebScrab(scode).getScrab(imgid);
	}
	
	public boolean incAccessUrl(String scode, String imgid) {
		return new RecWebScrab(scode).inc(imgid);
	}
	
	//for delivery order
	public boolean addOrder(String scode, String orderid, String userid, String from, String to, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType type, int price, long begintime, long endtime, String photourl) {
		return new RecDeliveryOrder(scode).insert(orderid, userid, from, to, name, notice, size, weight, type, price, begintime, endtime, photourl);
	}
	public RecDeliveryOrder getOrder(String scode, String orderid) {
		return new RecDeliveryOrder(scode).getOrder(orderid);
	}
	public boolean updateOrder(String scode, String orderid, String from, String to, String name, String notice,
			EGoodsSize size, EGoodsWeight weight, EGoodsType type, int price, long begintime, long endtime, String photourl ) {
		return new RecDeliveryOrder(scode).update(orderid, from, to, name, notice, size, weight, type, price, begintime, endtime, photourl);
	}
	public List<RecDeliveryOrder> getOrderList(String scode, String userid, int offset, int count) {
		return new RecDeliveryOrder(scode).getListOrder(userid, offset, count);
	}
	public List<RecDeliveryOrder> getOrderBeginList(String scode, String userid, int offset, int count) {
		return new RecDeliveryOrder(scode).getListBegin(userid, offset, count);
	}
	public List<RecDeliveryOrder> getOrderEndList(String scode, String userid, int offset, int count) {
		return new RecDeliveryOrder(scode).getListEnd(userid, offset, count);
	}

	public List<RecDeliveryOrder> getOrderListByIds(String scode, String[] orderids) {
		return new RecDeliveryOrder(scode).getListByIds(orderids);
	}
	
	public boolean updateOrderDisabled(String scode, String orderid) {
		return new RecDeliveryOrder(scode).updateDisabled(orderid);
	}

	public Map<String, Integer> getDeliverCountByOrderId(String scode, String[] orderids) {
		return new RecDeliverCount(scode).getDeliverCount(orderids);
	}
	//for delivery apply
	public boolean addDeliveryApply(String scode, String orderid, String deliverid, String username) {
		return new RecDeliveryApply(scode).insert(orderid, deliverid, username);
	}
	public boolean addDeliveryApply(String scode, String orderid, String deliverid, String username, long begintime, long endtime, int price, EDeliverType delivertype, EDeliverMethod deliverytype) {
		return new RecDeliveryApply(scode).insert(orderid, deliverid, username, begintime, endtime, price, delivertype, deliverytype);
	}
	public List<RecDeliveryApply> getDeliverList(String scode, String orderid) {
		return new RecDeliveryApply(scode).getDeliverList(orderid);
	}
	public boolean updateDeliveryEnabled(String scode, String orderid, String deliverid, boolean enabled) {
		return new RecDeliveryApply(scode).updateEnabled(orderid, deliverid, enabled);
	}

	//for delivery status management
	public boolean addDeliveryStatus(String scode, String orderid, String deliverid, EDeliveryStatus status) {
		return new RecDeliveryStatus(scode).insert(orderid, deliverid, status);
	}
	public boolean updateDeliveryStatus(String scode, String orderid, String deliverid, EDeliveryStatus status) {
		return new RecDeliveryStatus(scode).updateStatus(orderid, deliverid, status);
	}
	public boolean updateDeliveryStartCode(String scode, String orderid, String deliverid, EDeliveryStatus status, String passcode) {
		return new RecDeliveryStatus(scode).updateStartStatus(orderid, deliverid, status, passcode);
	}
	public boolean updateDeliveryEndCode(String scode, String orderid, String deliverid, EDeliveryStatus status, String passcode) {
		return new RecDeliveryStatus(scode).updateEndStatus(orderid, deliverid, status, passcode);
	}

	public RecDeliveryStatus getDeliveryStatus(String scode, String orderid) {
		return new RecDeliveryStatus(scode).getStatus(orderid);
	}
	public RecDeliveryStatus getDeliveryStatus(String scode, String orderid, String deliverid) {
		return new RecDeliveryStatus(scode).getStatus(orderid, deliverid);
	}
	public boolean delDeliveryStatus(String scode, String orderid) {
		return new RecDeliveryStatus(scode).deleteStatus(orderid);
	}

	//for delivery history
	public boolean addDeliveryHistory(String scode, String orderid, String deliverid, EDeliveryStatus status, String message) {
		return new RecDeliveryHistory(scode).insert(orderid, deliverid, status, message);
	}
	public List<RecDeliveryHistory> getDeliveryHistoryList(String scode, String orderid) {
		return new RecDeliveryHistory(scode).getHistoryList(orderid);
	}
	
	public boolean addAddress(String scode, String buildid, String zip, String sido, String sigu, String eub, String roadname, String delivery, 
			 String buildname, String dongname, String liname, String hjdongname,
			 int buildno, int buildsubno, int jino, int jisubno, double lon, double lat) {
		return new RecAddress(scode).insertAddress(buildid, zip, sido, sigu, eub, roadname, delivery, buildname, dongname, liname, hjdongname, buildno, buildsubno, jino, jisubno, lon, lat);
	}

	public RecAddress getAddress(String scode, String buildid) {
		return new RecAddress(scode).getAddress(buildid);
	}

	public List<RecAddress> getAddressList(String scode, List<String> buildids) {
		return new RecAddress(scode).getAddressList(buildids);
	}
	
	//for deliver voter
	public boolean addUserVoter(String scode, String userid, String voterid, String voteitem, int point, boolean like, String comments) {
		return new RecUserVoter(scode).insert(userid, voterid, voteitem, point, like, comments);
	}
	
	public boolean delUserVoter(String scode, String userid, String voterid, String voteitem) {
		return new RecUserVoter(scode).delete(userid, voterid, voteitem);
	}
	
	public List<RecUserVoter> getUserVoterUsers(String scode, String userid, int offset, int count) {
		return new RecUserVoter(scode).getVoterUsers(userid, offset, count);
	}

	public List<RecUserVoterView> getUserVoterUserList(String scode, String userid, int offset, int count) {
		return new RecUserVoterView(scode).getVoterUserList(userid, offset, count);
	}

	public boolean addRouteHistory(String scode, String deliverid, List<Point> routeList, int ordercount) {
		String routestr = routeList.stream().map(x -> x.getX()+","+x.getY()).collect(Collectors.joining("/"));
		return new RecDeliveryRouteHistory(scode).insert(deliverid, routestr, ordercount);
	}
	
	public List<RecDeliveryRouteHistory> getRouteList(String scode, String deliverid, int offset, int count) {
		return new RecDeliveryRouteHistory(scode).getRouteList(deliverid, offset, count);
	}

	public boolean insertOrderPhoto(String scode, String fileid, String orderid, String userid, EUserType usertype) {
		return new RecDeliveryPhoto(scode).insert(fileid, orderid, userid, usertype);
	}
	
	public boolean deleteOrderFilesByOrderId(String scode, String orderid) {
		return new RecDeliveryPhoto(scode).deleteOrder(orderid);
	}

	public boolean deleteOrderFiles(String scode, String orderid, List<String> fileids) {
		return new RecDeliveryPhoto(scode).deleteOrderFile(orderid, fileids);
	}
	
	public List<RecDeliveryPhoto> getDeliveryPhotoList(String scode, String orderid) {
		return new RecDeliveryPhoto(scode).getDeliveryPhotoList(orderid);
	}
	public List<RecDeliveryPhoto> getDeliveryPhotoList(String scode, String orderid, EUserType usertype) {
		return new RecDeliveryPhoto(scode).getDeliveryPhotoList(orderid, usertype);
	}
}
