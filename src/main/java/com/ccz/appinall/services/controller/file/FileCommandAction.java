package com.ccz.appinall.services.controller.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.datastore.HttpMultipart;
import com.ccz.appinall.library.server.session.SessionManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.Crypto;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthCommandAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.RecDataFile.*;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.enums.EBoardCmd;
import com.ccz.appinall.services.enums.EBoardError;
import com.ccz.appinall.services.enums.EChannelError;
import com.ccz.appinall.services.enums.EFileCmd;
import com.ccz.appinall.services.enums.EFileError;
import com.ccz.appinall.services.model.db.RecFile;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class FileCommandAction extends CommonAction {
	private final AttributeKey<WebsocketPacketData> attrWebsocketData = AttributeKey.valueOf(WebsocketPacketData.class.getSimpleName());
	
	@Autowired	ServicesConfig servicesConfig;

	@Autowired	ChAttributeKey chAttributeKey;
	
	public FileCommandAction() {
		super.setCommandFunction(EFileCmd.fileinit.getValue(), doFileInit);
		super.setCommandFunction(EFileCmd.filesstart.getValue(), doFileStart);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EFileError> res = new ResponseData<EFileError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EFileError>) cmdFunc.doAction(EFileCmd.getType(cmd).isAuth()?session : ch, res, jdata);
			send(ch, res.toString());
			return true;
		}
		return false;
	}

	
	ICommandFunction<AuthSession, ResponseData<EFileError>, JsonNode> doFileInit = (AuthSession ss, ResponseData<EFileError> res, JsonNode jnode) -> {
		FileInit data = new RecDataFile().new FileInit(jnode);
		if(data.getFilesize() < 1)
			return res.setError(EFileError.invalid_file_size);
		if(data.getFilesize() > servicesConfig.getFileUploadMax())
			return res.setError(EFileError.too_large_file);
		if(StrUtil.isFileName(data.getFilename())==false)
			return res.setError(EFileError.invalid_file_name);
		
		String fileid = StrUtil.getUuid("file");//Crypto.AES256Cipher.getInst().enc(data.getScode()+"/"+session.getUserId()+"/"+System.currentTimeMillis()+"/"+seq);
		
		if(DbAppManager.getInst().addFileInit(data.getScode(), fileid, ss.getUserId(), data.getFilename(), data.getFiletype(), data.getFilesize()) == false)
			return res.setError(EFileError.fail_to_uploadfile);
		
		return res.setError(EFileError.ok).setParam("fileid", fileid).setParam("ip", servicesConfig.getFileUploadIp()).setParam("port", servicesConfig.getFileUploadPort()+"");
	};
	
	ICommandFunction<Channel, ResponseData<EFileError>, JsonNode> doFileStart = (Channel ch, ResponseData<EFileError> res, JsonNode jnode) -> {
		FileStart data = new RecDataFile().new FileStart(jnode);
		if(data.getFileid()==null || data.getFileid().length()<1)
			return res.setError(EFileError.invalid_fileid);
		RecFile recfile = DbAppManager.getInst().getFileInfo(data.getScode(), data.getFileid());
		if(recfile==null)
			return res.setError(EFileError.not_exist_fileinfo);
		
		UploadFile uploadFile = new UploadFile(recfile);
		if(uploadFile.open(data.getScode(), StrUtil.getHostIp(), servicesConfig.getFileUploadDir())==false)
			return res.setError(EFileError.fail_to_createfile);
		
		FileSession session = new FileSession(ch, 2).putSession(uploadFile, data.getScode());	//consider the sessionid to find instance when close
		ch.attr(chAttributeKey.getFileSessionKey()).set(session);
		ch.attr(attrWebsocketData).get().setFilemode(true);
		
		return res.setError(EFileError.ok);
	};

}
