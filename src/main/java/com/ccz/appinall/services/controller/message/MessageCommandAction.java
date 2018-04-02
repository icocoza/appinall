package com.ccz.appinall.services.controller.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.AsciiSplitter.ASS;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.FileCommandAction;
import com.ccz.appinall.services.controller.message.RecDataMessage.*;
import com.ccz.appinall.services.enums.EMessageCmd;
import com.ccz.appinall.services.enums.EMessageError;
import com.ccz.appinall.services.model.db.*;
import com.ccz.appinall.services.model.db.RecMessageDel.RecDelId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageCommandAction extends CommonAction {
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public MessageCommandAction() {
	}

	private boolean processBoardData(Channel ch, String[] data, JsonNode jdata) {
		ResponseData<EMessageError> res = null;
		if(data != null)
			res = new ResponseData<EMessageError>(data[0], data[1], data[2]);
		else
			res = new ResponseData<EMessageError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		AuthSession ss = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		switch(EMessageCmd.getType(res.getCommand())) {
		case msg: 
			res = this.message(ss, res, data != null ? new RecDataMessage().new Msg(data[3]) : new RecDataMessage().new Msg(jdata)); //O
			break;
		case syncmsg: 
			res = this.syncMessage(ss, res, data != null ? new RecDataMessage().new SyncMsg(data[3]) : new RecDataMessage().new SyncMsg(jdata)); //O
			break;
		case readmsg: 
			res = this.readMessage(ss, res, data != null ? new RecDataMessage().new ReadMsg(data[3]) : new RecDataMessage().new ReadMsg(jdata));
			break;
		case delmsg: 
			res = this.delMessage(ss, res, data != null ? new RecDataMessage().new DelMsg(data[3]) : new RecDataMessage().new DelMsg(jdata)); //O
			break;
		case online: //no service
			res = this.onlineOnlyMessage(ss, res, null);
			break;
		case push: //no service
			res = this.pushOnlyMessage(ss, res, null);
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
	private ResponseData<EMessageError> message(AuthSession ss, ResponseData<EMessageError> res, Msg rec) {
		String msgid = getMessageId();
		if(DbAppManager.getInst().addMessage(ss.scode, msgid, rec.chid, ss.getUserId(), rec.eMsgType, rec.msg)==false)
			return res.setError(EMessageError.eFailToSaveMessage);
		if(DbAppManager.getInst().updateLastMsgAndTime(ss.scode, rec.chid, obtainShortcut(rec.msg))==false)
			return res.setError(EMessageError.eFailToUpdateChannel);
		//[TODO] Send Message to Others in the same channel
		return res.setError(EMessageError.eOK).setParam(msgid + ASS.UNIT + rec.msg);
	}

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
	private ResponseData<EMessageError> syncMessage(AuthSession ss, ResponseData<EMessageError> res, SyncMsg rec) {
		RecChMime ch = DbAppManager.getInst().getMyChannel(ss.scode, ss.getUserId(), rec.chid);
		if(ch==RecChMime.Empty)
			return res.setError(EMessageError.eNoChannel);

		List<RecDelId> delList = DbAppManager.getInst().getDelMessageIdList(ss.scode, rec.chid, ss.getUserId(), ch.addtime);
		List<RecMessage> msgList = null;
		if(delList.size()<1)
			msgList = DbAppManager.getInst().getMessageList(ss.scode, rec.chid, ch.addtime, rec.offset, rec.count);
		else {
			String delIds = delList.stream().map(e->"'"+e.msgid+"'").collect(Collectors.joining(","));
			msgList = DbAppManager.getInst().getMessageListWithoutDeletion(ss.scode, rec.chid, ch.addtime, delIds, rec.offset, rec.count);
		}
		String param = msgList.stream().map(e-> String.format("%s%s%s%s%d%s%d%s%d%s%s", e.msgid, ASS.UNIT, e.senderid, ASS.UNIT, e.msgtype.getValue(), 
				ASS.UNIT, e.createtime.getTime(), ASS.UNIT, e.readcnt, ASS.UNIT, e.message)).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EMessageError.eOK).setParam(rec.chid + ASS.GROUP + param);
	}
	/** 
	 * send message to only online user in a channel
	 * @param ss
	 * @param res
	 * @param userData
	 * @return
	 */
	private ResponseData<EMessageError> onlineOnlyMessage(AuthSession ss, ResponseData<EMessageError> res, String userData) {
		return res.setError(EMessageError.eNoServiceCommand);
	}
	/** 
	 * send message to other by push in a channel
	 * @param ss
	 * @param res
	 * @param userData
	 * @return
	 */
	private ResponseData<EMessageError> pushOnlyMessage(AuthSession ss, ResponseData<EMessageError> res, String userData) {
		return res.setError(EMessageError.eNoServiceCommand);
	}
	/** 
	 * notify read message
	 * @param ss
	 * @param res
	 * @param userData, [channel id][message id]
	 * @return [channel id][message id]
	 */
	private ResponseData<EMessageError> readMessage(AuthSession ss, ResponseData<EMessageError> res, ReadMsg rec) {
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, rec.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EMessageError.eNoChannel);
		RecMessage msg = DbAppManager.getInst().getMessage(ss.scode, rec.chid, rec.msgid);
		if(msg==RecMessage.Empty)
			return res.setError(EMessageError.eNoMessage);
		if(DbAppManager.getInst().addReadMsg(ss.scode, rec.chid, ss.getUserId(), rec.msgid)==false)
			return res.setError(EMessageError.eAlreadyReadMessage);
		DbAppManager.getInst().incReadCount(ss.scode, rec.msgid);
		return res.setError(EMessageError.eOK).setParam(rec.chid+ASS.UNIT+rec.msgid);
	}
	/** 
	 * request to delete a message of the session user id
	 * @param ss
	 * @param res
	 * @param userData, [channel id] | {[message id] / ...}
	 * @return
	 */
	private ResponseData<EMessageError> delMessage(AuthSession ss, ResponseData<EMessageError> res, DelMsg rec) {
		List<String> delmsglist = new ArrayList<>();
		for(String msgid : rec.msgids) {
			if(DbAppManager.getInst().delMessage(ss.scode, rec.chid, ss.getUserId(), msgid)==true)
				delmsglist.add(msgid);
		}
		if(delmsglist.size()<1)
			return res.setError(EMessageError.eFailToDeleteMessage);
		String param = delmsglist.stream().collect(Collectors.joining(ASS.RECORD));
		return res.setError(EMessageError.eOK).setParam(rec.chid + ASS.GROUP + param);
	}
}
