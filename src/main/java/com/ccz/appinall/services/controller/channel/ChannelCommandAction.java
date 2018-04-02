package com.ccz.appinall.services.controller.channel;

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
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.channel.RecDataChannel.*;
import com.ccz.appinall.services.enums.EChannelCmd;
import com.ccz.appinall.services.enums.EChannelError;
import com.ccz.appinall.services.model.db.RecChMime;
import com.ccz.appinall.services.model.db.RecChannel;
import com.ccz.appinall.services.model.db.RecChMime.RecChMimeExt;
import com.ccz.appinall.services.model.db.RecChannel.RecChLastMsg;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class ChannelCommandAction extends CommonAction{
	@Autowired
	ChAttributeKey chAttributeKey;
	
	public ChannelCommandAction() {
	}

	private boolean processBoardData(Channel ch, String[] data, JsonNode jdata) {
		ResponseData<EChannelError> res = null;
		if(data != null)
			res = new ResponseData<EChannelError>(data[0], data[1], data[2]);
		else
			res = new ResponseData<EChannelError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		AuthSession ss = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		switch(EChannelCmd.getType(res.getCommand())) {
		case chcreate:
			res = this.channelCreate(ss, res, data != null? new RecDataChannel().new ChCreate(data[3]) : new RecDataChannel().new ChCreate(jdata)); //O
			break;
		case chexit:
			res = this.channelExit(ss, res, data != null? new RecDataChannel().new ChExit(data[3]) : new RecDataChannel().new ChExit(jdata)); //O
			break;
		case chenter:
			res = this.channelEnter(ss, res, data != null? new RecDataChannel().new ChEnter(data[3]) : new RecDataChannel().new ChEnter(jdata)); //O
			break;
		case chinvite:
			res = this.channelInvite(ss, res, data != null? new RecDataChannel().new ChInvite(data[3]): new RecDataChannel().new ChInvite(jdata)); //O
			break;
		case chmime:
			res = this.channelMime(ss, res, data != null? new RecDataChannel().new ChMime(data[3]) : new RecDataChannel().new ChMime(jdata)); //O
			break;
		case chcount:
			res = this.channelCount(ss, res, null); //O
			break;
		case chlastmsg:
			res = this.channelLastMessage(ss, res, data != null? new RecDataChannel().new ChLastMsg(data[3]) : new RecDataChannel().new ChLastMsg(jdata)); //O
			break;
		case chinfo:
			res = this.channelInfos(ss, res, data != null? new RecDataChannel().new ChInfo(data[3]) : new RecDataChannel().new ChInfo(jdata)); //O
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
	 * create channel
	 * @param ss
	 * @param res
	 * @param userData, { user id/ .. }
	 * @return channel id
	 */
	private ResponseData<EChannelError> channelCreate(AuthSession ss, ResponseData<EChannelError> res, ChCreate rec) {
		if(rec.attendees.size()>1)		
			rec.attendees.add(ss.getUserId());
		else {
			RecChannel ch = DbAppManager.getInst().findChannel(ss.scode, ss.getUserId(), rec.attendees.get(0));
			if(ch!=RecChannel.Empty) {
				DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), ch.chid);
				return res.setError(EChannelError.eOK).setParam(ch.chid);
			}
		}
		String chid = StrUtil.getSha1Uuid("ch");
		String strattendees = rec.attendees.stream().collect(Collectors.joining(ASS.RECORD));
		DbAppManager.getInst().addChannel(ss.scode, chid, ss.getUserId(), strattendees, rec.attendees.size()+1);	//attendee
		DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), chid);
		return res.setError(EChannelError.eOK).setParam(chid);
	}
	
	/** 
	 * exit channel
	 * @param ss
	 * @param res
	 * @param userData, channel id
	 * @return channel id
	 * [TODO] broadcast exit message to others
	 */
	private ResponseData<EChannelError> channelExit(AuthSession ss, ResponseData<EChannelError> res, ChExit rec) {
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, rec.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EChannelError.eNoChannel);
		DbAppManager.getInst().delMyChannel(ss.scode, ss.getUserId(), rec.chid);
		if(ch.type > 2){	//type is original attendee count. this value is not changed.
			if(ch.attendees.contains(ss.getUserId())) {
				ch.attendees = ch.attendees.replace(ss.getUserId()+ASS.RECORD, ""); //try to delete id with delimiter
				ch.attendees = ch.attendees.replace(ss.getUserId(), "");	//try to delete id without delimiter for the last one
				//[TODO] broadcast exit message to others
			}
			if(ch.attendees.length()==0)
				DbAppManager.getInst().delChannel(ss.scode, rec.chid);
			else
				DbAppManager.getInst().updateChannelAttendees(ss.scode, rec.chid, ch.attendees, --ch.attendeecnt);
		}
		String param = String.format("%s%s%s", rec.chid, ASS.GROUP, ch.attendees);
		return res.setError(EChannelError.eOK).setParam(param);
	}
	
	/** 
	 * enter channel
	 * @param ss
	 * @param res
	 * @param userData, channel id
	 * @return
	 */
	private ResponseData<EChannelError> channelEnter(AuthSession ss, ResponseData<EChannelError> res, ChEnter rec) {
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, rec.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EChannelError.eNoChannel);
		
		DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), rec.chid);
		return res.setError(EChannelError.eOK).setParam(rec.chid);
	}
	
	/** 
	 * invite friends
	 * @param ss
	 * @param res
	 * @param userData, channel id | { user id / ... }
	 * @return
	 */
	private ResponseData<EChannelError> channelInvite(AuthSession ss, ResponseData<EChannelError> res, ChInvite rec) {
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, rec.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EChannelError.eNoChannel);
		
		for(String userid : rec.attendees) {
			if(ch.attendees.contains(userid)==false) {
				ch.attendees = ch.attendees + ASS.RECORD + userid;
				ch.attendeecnt++;
				ch.type++;
			}
		}
		if(ch.type>2 && ch.attendees.contains(ss.getUserId())==false)
			ch.attendees = ch.attendees + ASS.RECORD + ss.getUserId();
		if(DbAppManager.getInst().updateChannelAttendees(ss.scode, rec.chid, ch.attendees, ch.attendeecnt, ch.type)==false)
			return res.setError(EChannelError.eFailToUpdate);
		String param = String.format("%s%s%s", rec.chid, ASS.GROUP, ch.attendees);
		return res.setError(EChannelError.eOK).setParam(param);
	}
	
	/** 
	 * my channel list
	 * @param ss
	 * @param res
	 * @param userData, [offset][count]
	 * @return
	 */
	private ResponseData<EChannelError> channelMime(AuthSession ss, ResponseData<EChannelError> res, ChMime rec) {
		List<RecChMime> chList = DbAppManager.getInst().getMyChannelList(ss.scode, ss.getUserId(), rec.offset, rec.count);
		if(chList.size() < 1)
			return res.setError(EChannelError.eNoListData);
		String param = chList.stream().map(e->e.chid).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EChannelError.eOK).setParam(param);
	}
	
	/** 
	 * my channel count
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return
	 */
	private ResponseData<EChannelError> channelCount(AuthSession ss, ResponseData<EChannelError> res, String userData) {
		int count = DbAppManager.getInst().getMyChannelCount(ss.scode, ss.getUserId());
		return res.setError(EChannelError.eOK).setParam(count+"");
	}
	
	/** 
	 * channel last message
	 * @param ss
	 * @param res
	 * @param userData, { channel id / ... }
	 * @return { [channel id][last message][last time] / ... }
	 */
	private ResponseData<EChannelError> channelLastMessage(AuthSession ss, ResponseData<EChannelError> res, ChLastMsg rec) {
		List<RecChLastMsg> chList = DbAppManager.getInst().getChannelLastMsg(ss.scode, rec.chids);
		String param = chList.stream().map(e->String.format("%s%s%s%s%d", e.chid, ASS.UNIT, e.lastmsg, ASS.UNIT, e.lasttime.getTime())).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EChannelError.eOK).setParam(param);
	}
	
	/** 
	 * channel last message
	 * @param ss
	 * @param res
	 * @param userData, [offset][count]
	 * @return { [channel id]|[user id / user id / ...]|[last message]|[last time] ! ...}
	 * [TODO] Consider the divider
	 */
	private ResponseData<EChannelError> channelInfos(AuthSession ss, ResponseData<EChannelError> res, ChInfo rec) {
		List<RecChMimeExt> chList = DbAppManager.getInst().getMyChannelInfoList(ss.scode, ss.getUserId(), rec.offset, rec.count);
		String param = chList.stream().map(e->String.format("%s%s%s%s%s%s%d", e.chid, ASS.GROUP, e.userid2, ASS.GROUP, e.lastmsg, ASS.GROUP, e.lasttime.getTime())).collect(Collectors.joining(ASS.FILE));
		return res.setError(EChannelError.eOK).setParam(param);
	}
	

}
