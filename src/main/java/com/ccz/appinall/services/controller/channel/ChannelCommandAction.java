package com.ccz.appinall.services.controller.channel;

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
import com.ccz.appinall.services.controller.channel.RecDataChannel.*;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
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
	
	public ChannelCommandAction() {
		super.setCommandFunction(EAllCmd.chcreate, channelCreate); //O
		super.setCommandFunction(EAllCmd.chexit, channelExit); //O
		super.setCommandFunction(EAllCmd.chenter, channelEnter); //O
		super.setCommandFunction(EAllCmd.chinvite, channelInvite); //O
		super.setCommandFunction(EAllCmd.chmime, channelMime); //O
		super.setCommandFunction(EAllCmd.chcount, channelCount); //O
		super.setCommandFunction(EAllCmd.chlastmsg, channelLastMessage); //O
		super.setCommandFunction(EAllCmd.chinfo, channelInfos); //O
	}

	/** 
	 * create channel
	 * @param ss
	 * @param res
	 * @param userData, { user id/ .. }
	 * @return channel id
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelCreate = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChCreate data = new RecDataChannel().new ChCreate(jnode);
		if(data.attendees.size()>1)		
			data.attendees.add(ss.getUserId());
		else {
			RecChannel ch = DbAppManager.getInst().findChannel(ss.scode, ss.getUserId(), data.attendees.get(0));
			if(ch!=RecChannel.Empty) {
				DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), ch.chid);
				return res.setError(EAllError.ok).setParam(ch.chid);
			}
		}
		String chid = StrUtil.getSha1Uuid("ch");
		String strattendees = data.attendees.stream().collect(Collectors.joining(ASS.RECORD));
		DbAppManager.getInst().addChannel(ss.scode, chid, ss.getUserId(), strattendees, data.attendees.size()+1);	//attendee
		DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), chid);
		return res.setError(EAllError.ok).setParam(chid);
	};
	
	/** 
	 * exit channel
	 * @param ss
	 * @param res
	 * @param userData, channel id
	 * @return channel id
	 * [TODO] broadcast exit message to others
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelExit = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChExit data = new RecDataChannel().new ChExit(jnode);
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, data.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EAllError.eNoChannel);
		DbAppManager.getInst().delMyChannel(ss.scode, ss.getUserId(), data.chid);
		if(ch.type > 2){	//type is original attendee count. this value is not changed.
			if(ch.attendees.contains(ss.getUserId())) {
				ch.attendees = ch.attendees.replace(ss.getUserId()+ASS.RECORD, ""); //try to delete id with delimiter
				ch.attendees = ch.attendees.replace(ss.getUserId(), "");	//try to delete id without delimiter for the last one
				//[TODO] broadcast exit message to others
			}
			if(ch.attendees.length()==0)
				DbAppManager.getInst().delChannel(ss.scode, data.chid);
			else
				DbAppManager.getInst().updateChannelAttendees(ss.scode, data.chid, ch.attendees, --ch.attendeecnt);
		}
		String param = String.format("%s%s%s", data.chid, ASS.GROUP, ch.attendees);
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * enter channel
	 * @param ss
	 * @param res
	 * @param userData, channel id
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelEnter = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChEnter data = new RecDataChannel().new ChEnter(jnode);
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, data.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EAllError.eNoChannel);
		
		DbAppManager.getInst().addMyChannel(ss.scode, ss.getUserId(), data.chid);
		return res.setError(EAllError.ok).setParam(data.chid);
	};
	
	/** 
	 * invite friends
	 * @param ss
	 * @param res
	 * @param userData, channel id | { user id / ... }
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelInvite = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChInvite data = new RecDataChannel().new ChInvite(jnode);
		RecChannel ch = DbAppManager.getInst().getChannel(ss.scode, data.chid);
		if(ch==RecChannel.Empty)
			return res.setError(EAllError.eNoChannel);
		
		for(String userid : data.attendees) {
			if(ch.attendees.contains(userid)==false) {
				ch.attendees = ch.attendees + ASS.RECORD + userid;
				ch.attendeecnt++;
				ch.type++;
			}
		}
		if(ch.type>2 && ch.attendees.contains(ss.getUserId())==false)
			ch.attendees = ch.attendees + ASS.RECORD + ss.getUserId();
		if(DbAppManager.getInst().updateChannelAttendees(ss.scode, data.chid, ch.attendees, ch.attendeecnt, ch.type)==false)
			return res.setError(EAllError.eFailToUpdate);
		String param = String.format("%s%s%s", data.chid, ASS.GROUP, ch.attendees);
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * my channel list
	 * @param ss
	 * @param res
	 * @param userData, [offset][count]
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelMime = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChMime data = new RecDataChannel().new ChMime(jnode);
		List<RecChMime> chList = DbAppManager.getInst().getMyChannelList(ss.scode, ss.getUserId(), data.offset, data.count);
		if(chList.size() < 1)
			return res.setError(EAllError.eNoListData);
		String param = chList.stream().map(e->e.chid).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * my channel count
	 * @param ss
	 * @param res
	 * @param userData, 
	 * @return
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelCount = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		int count = DbAppManager.getInst().getMyChannelCount(ss.scode, ss.getUserId());
		return res.setError(EAllError.ok).setParam(count+"");
	};
	
	/** 
	 * channel last message
	 * @param ss
	 * @param res
	 * @param userData, { channel id / ... }
	 * @return { [channel id][last message][last time] / ... }
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelLastMessage = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChLastMsg data = new RecDataChannel().new ChLastMsg(jnode);
		List<RecChLastMsg> chList = DbAppManager.getInst().getChannelLastMsg(ss.scode, data.chids);
		String param = chList.stream().map(e->String.format("%s%s%s%s%d", e.chid, ASS.UNIT, e.lastmsg, ASS.UNIT, e.lasttime.getTime())).collect(Collectors.joining(ASS.RECORD));
		return res.setError(EAllError.ok).setParam(param);
	};
	
	/** 
	 * channel last message
	 * @param ss
	 * @param res
	 * @param userData, [offset][count]
	 * @return { [channel id]|[user id / user id / ...]|[last message]|[last time] ! ...}
	 * [TODO] Consider the divider
	 */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> channelInfos = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		ChInfo data = new RecDataChannel().new ChInfo(jnode);
		List<RecChMimeExt> chList = DbAppManager.getInst().getMyChannelInfoList(ss.scode, ss.getUserId(), data.offset, data.count);
		String param = chList.stream().map(e->String.format("%s%s%s%s%s%s%d", e.chid, ASS.GROUP, e.userid2, ASS.GROUP, e.lastmsg, ASS.GROUP, e.lasttime.getTime())).collect(Collectors.joining(ASS.FILE));
		return res.setError(EAllError.ok).setParam(param);
	};
	

}
