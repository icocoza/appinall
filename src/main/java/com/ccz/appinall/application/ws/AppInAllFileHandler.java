package com.ccz.appinall.application.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.datastore.WebsocketBinaryData;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceHandler;
import com.ccz.appinall.library.util.ProtocolWriter;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.library.util.ProtocolWriter.WriteWebsocket;
import com.ccz.appinall.services.controller.file.FileSession;
import com.ccz.appinall.services.enums.EAllError;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInAllFileHandler implements IServiceHandler {
	public final AttributeKey<FileSession> attrFileSessionKey = AttributeKey.valueOf(FileSession.class.getSimpleName());
	private final AttributeKey<WebsocketPacketData> attrWebsocketData = AttributeKey.valueOf(WebsocketPacketData.class.getSimpleName());
	
	@Autowired
	ServicesConfig servicesConfig;
	private final String serviceCode = WebsocketBinaryData.BINARY_DATA;

	private ImageResizeWorker imageResizeWorker = new ImageResizeWorker();
	
	public IServiceHandler init() {
		return this;
	}
	
	@Override
	public void send(Channel ch, String data) {
		WriteWebsocket writeWebsocket = new ProtocolWriter().new WriteWebsocket();
		writeWebsocket.write(ch, data);
	}

	@Override
	public boolean isService(String serviceType) {
		return this.serviceCode.equals(serviceType);
	}

	@Override
	public boolean process(Channel ch, IDataAccess da) {
		FileSession fileSession = ch.attr(attrFileSessionKey).get();
		if(fileSession==null) {
			response(ch, fileSession, EAllError.invalid_file_session);
			ch.close();
			return false;
		}
		try {
			fileSession.write(da.getData());
			if(fileSession.isOverSize()) {
				if(fileSession.commit(imageResizeWorker) == false) {
					response(ch, fileSession, EAllError.commit_error);
					return false;
				}
				ch.attr(attrFileSessionKey).set(null);
				ch.attr(attrWebsocketData).get().setFilemode(false);
				//NAS가 없는 환경에서는 Upload된 서버가 파일 호스트가 될 것임. 상용시에는 아래와 같이 FULL URL 생성을 최대한 지양해야 함.
				String downUrl = String.format("http://%s:8080/download?fileid=%s&scode=%s", StrUtil.getHostIp(), fileSession.getKey(), fileSession.getScode());
				Map<String, String> objectMap = new HashMap<>();
				objectMap.put("hostip", StrUtil.getHostIp());
				objectMap.put("hostport", servicesConfig.getFileDownPort()+"");
				objectMap.put("fileid", fileSession.getKey());
				objectMap.put("scode", fileSession.getScode());
				response(ch, fileSession, EAllError.complete, objectMap);
			}
			return true;
		} catch (IOException e) {
			response(ch, fileSession, EAllError.exception);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onClose(Channel ch) {
		// TODO Auto-generated method stub
		FileSession fileSession = ch.attr(attrFileSessionKey).get();
		if(fileSession==null)
			return;
		try {
			fileSession.discard();
			ch.attr(attrFileSessionKey).set(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void response(Channel ch, FileSession fileSession, EAllError error) {
		ResponseData<EAllError> res = new ResponseData<EAllError>(fileSession.getScode(), "0000", "uploadfile");
		res.setError(error);
		this.send(ch, res.toString());
	}
	
	private void response(Channel ch, FileSession fileSession, EAllError error, Map<String, String> objectMap) {
		ResponseData<EAllError> res = new ResponseData<EAllError>(fileSession.getScode(), "0000", "uploadfile");
		for(Entry<String, String> entry : objectMap.entrySet())
			res.setParam(entry.getKey(), entry.getValue());
		res.setError(error);
		this.send(ch, res.toString());
	}

}
