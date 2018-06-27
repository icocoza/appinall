package com.ccz.appinall.services.controller.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.message.RecDataMessage.*;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.services.model.db.RecMessageDel.RecDelId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageCommandAction extends CommonAction {
	
	public MessageCommandAction() {
		super.setCommandFunction(EAllCmd.msg, message); //O
		super.setCommandFunction(EAllCmd.syncmsg, syncMessage); //O
		super.setCommandFunction(EAllCmd.readmsg, readMessage);
		super.setCommandFunction(EAllCmd.delmsg, delMessage); //O
		super.setCommandFunction(EAllCmd.online, onlineOnlyMessage);
		super.setCommandFunction(EAllCmd.push, pushOnlyMessage);
	}
	
	int updateTime = 0;
	String msgKey=null;
	private String getMessageId() {
		if(msgKey==null || ++updateTime % 1017==0) {
			msgKey = StrUtil.getSha1Uuid("msg");
			updateTime=0;
		}
		return msgKey+updateTime;
	}
	/** 
	 * send message to others in channel
	 * @param ss
	 * @param res
	 * @param userData, [channel id][msg type][message]
	 * @return [message id][message]
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> message = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		Msg data = new RecDataMessage().new Msg(jnode);
		String msgid = getMessageId();
		if(DbAppManager.getInst().addMessage(ss.scode, msgid, data.chid, ss.getUserId(), data.eMsgType, data.msg)==false)
			return res.setError(EAllError.eFailToSaveMessage);
		if(DbAppManager.getInst().updateLastMsgAndTime(ss.scode, data.chid, obtainShortcut(data.msg))==false)
			return res.setError(EAllError.eFailToUpdateChannel);
		//[TODO] Send Message to Others in the same channel
		return res.setError(EAllError.ok).setParam(msgid + ASS.UNIT + data.msg);
	};

	private String obtainShortcut(String msg) {	//content if json, else cut 32
		if(msg.startsWith("{")) {
			try {
				JsonNode jObj = new ObjectMapper().readTree(msg);
				if(jObj.has("content"))
					msg = jObj.get("content").asText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg.length()>32 ? msg.substring(0, 31) : msg;
	}
	/**
	 * request sync message in a channel
	 * @param ss
	 * @param res
	 * @param userData, [channel id][offset][count]
	 * @return chid | { [message id][sender id][msg type][create time][read count][message] / ... }
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> syncMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		SyncMsg data = new RecDataMessage().new SyncMsg(jnode);
		RecChMime ch = DbAppManager.getInst().getMyChannel(ss.scode, ss.getUserId(), data.chid);
		if(ch==RecChMime.Empty)
			return res.setError(EAllError.eNoChannel);

		List<RecDelId> delList = DbAppManager.getInst().getDelMessageIdList(ss.scode, data.chid, ss.getUserId(), ch.addtime);
		List<RecMessage> msgList = null;
		if(delList.size()<1)
			msgList = DbAppManager.getInst().getMessageList(ss.scode, data.chid, ch.addtime, data.offset, data.count);
		else {
			String delIds = delList.stream().map(e->"'"+e.msgid+"'").collect(Collectors.joining(","));
			msgList = DbAppManager.getInst().getMessageListWithoutDeletion(ss.scode, data.chid, ch.addtime, delIds, data.offset, data.count);
		}
		String param = msgList.stream().map(e-> String.format("%s%s%s%s%d%s%d%s%d%s%s", e.msgid, ASS.UNIT, e.senderid, ASS.UNIT, e.msgtype, 
				ASS.UNIT, e.createtime.getTime(), ASS.UNIT, e.readcnt, ASS.UNIT, e.message)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(data.chid + ASS.GROUP + param);
	};
	/** 
	 * send message to only online user in a channel
	 * @param ss
	 * @param res
	 * @param userData
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> onlineOnlyMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		return res.setError(EAllError.eNoServiceCommand);
	};
	/** 
	 * send message to other by push in a channel
	 * @param ss
	 * @param res
	 * @param userData
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> pushOnlyMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		return res.setError(EAllError.eNoServiceCommand);
	};
	/** 
	 * notify read message
	 * @param ss
	 * @param res
	 * @param userData, [channel id][message id]
	 * @return [channel id][message id]
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> readMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ReadMsg data = new RecDataMessage().new ReadMsg(jnode);
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, data.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EAllError.eNoChannel);
		RecMessage msg = DbAppManager.getInst().getMessage(ss.scode, data.chid, data.msgid);
		if(msg==RecMessage.Empty)
			return res.setError(EAllError.eNoMessage);
		if(DbAppManager.getInst().addReadMsg(ss.scode, data.chid, ss.getUserId(), data.msgid)==false)
			return res.setError(EAllError.eAlreadyReadMessage);
		DbAppManager.getInst().incReadCount(ss.scode, data.msgid);
		return res.setError(EAllError.ok).setParam(data.chid+ASS.UNIT+data.msgid);
	};
	/** 
	 * request to delete a message of the session user id
	 * @param ss
	 * @param res
	 * @param userData, [channel id] | {[message id] / ...}
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> delMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		DelMsg data = new RecDataMessage().new DelMsg(jnode);
		List<String> delmsglist = new ArrayList<>();
		for(String msgid : data.msgids) {
			if(DbAppManager.getInst().delMessage(ss.scode, data.chid, ss.getUserId(), msgid)==true)
				delmsglist.add(msgid);
		}
		if(delmsglist.size()<1)
			return res.setError(EAllError.eFailToDeleteMessage);
		String param = delmsglist.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(data.chid + ASS.GROUP + param);
	};
}
