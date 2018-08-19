package com.ccz.appinall.services.controller.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.controller.file.RecDataFile.*;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
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
		super.setCommandFunction(EAllCmd.fileinit, doFileInit);
		super.setCommandFunction(EAllCmd.filesstart, doFileStart);
	}
		
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doFileInit = (AuthSession ss, ResponseData<EAllError> res, JsonNode jnode) -> {
		FileInit data = new RecDataFile().new FileInit(jnode);
		if(data.getFilesize() < 1)
			return res.setError(EAllError.invalid_file_size);
		if(data.getFilesize() > servicesConfig.getFileUploadMax())
			return res.setError(EAllError.too_large_file);
		if(StrUtil.isFileName(data.getFilename())==false)
			return res.setError(EAllError.invalid_file_name);
		
		String fileid = StrUtil.getUuid("file");//Crypto.AES256Cipher.getInst().enc(data.getScode()+"/"+session.getUserId()+"/"+System.currentTimeMillis()+"/"+seq);
		
		//[TODO][Notide] 웹소켓으로 접속된 IP로 파일을 업로딩 해야 함. 성능, 비용 절감 위함. StrUtil.getHostIp()는 필수적임.
		if(DbAppManager.getInst().addFileInit(data.getScode(), fileid, ss.getUserId(), StrUtil.getHostIp(), data.getFilename(), data.getFiletype(), data.getFilesize(), data.getComment()) == false)
			return res.setError(EAllError.fail_to_uploadfile);
		
		return res.setError(EAllError.ok).setParam("fileid", fileid).setParam("ip", StrUtil.getHostIp()).setParam("port", servicesConfig.getFileUploadPort()+"");
	};
	
	ICommandFunction<Channel, ResponseData<EAllError>, JsonNode> doFileStart = (Channel ch, ResponseData<EAllError> res, JsonNode jnode) -> {
		FileStart data = new RecDataFile().new FileStart(jnode);
		if(data.getFileid()==null || data.getFileid().length()<1)
			return res.setError(EAllError.invalid_fileid);
		RecFile recfile = DbAppManager.getInst().getFileInfo(data.getScode(), data.getFileid());
		if(recfile==null)
			return res.setError(EAllError.not_exist_fileinfo);
		
		UploadFile uploadFile = new UploadFile(recfile);
		if(uploadFile.open(data.getScode(), StrUtil.getHostIp(), servicesConfig.getFileUploadDir())==false)
			return res.setError(EAllError.fail_to_createfile);
		
		FileSession session = new FileSession(ch, 2).putSession(uploadFile, data.getScode());	//consider the sessionid to find instance when close
		ch.attr(chAttributeKey.getFileSessionKey()).set(session);
		ch.attr(attrWebsocketData).get().setFilemode(true);
		
		return res.setError(EAllError.ok);
	};

}
