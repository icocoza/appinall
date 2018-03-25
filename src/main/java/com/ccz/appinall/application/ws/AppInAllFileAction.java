package com.ccz.appinall.application.ws;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.ccz.appinall.library.datastore.WebsocketBinaryData;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.type.inf.IServiceAction;
import com.ccz.appinall.library.util.ProtocolWriter;
import com.ccz.appinall.library.util.ProtocolWriter.WriteWebsocket;
import com.ccz.appinall.services.controller.file.FileSession;
import com.ccz.appinall.services.enums.EFileError;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInAllFileAction implements IServiceAction {
	public final AttributeKey<FileSession> attrFileSessionKey = AttributeKey.valueOf(FileSession.class.getSimpleName());

	private final String serviceCode = WebsocketBinaryData.BINARY_DATA;

	private ImageResizeWorker imageResizeWorker = new ImageResizeWorker();
	
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
			response(ch, fileSession, EFileError.invalid_file_session);
			ch.close();
			return false;
		}
		try {
			fileSession.write(da.getData());
			if(fileSession.isOverSize()) {
				fileSession.commit(imageResizeWorker);
				ch.attr(attrFileSessionKey).set(null);
				response(ch, fileSession, EFileError.complete);
			}
			return true;
		} catch (IOException e) {
			response(ch, fileSession, EFileError.exception);
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
	
	private void response(Channel ch, FileSession fileSession, EFileError error) {
		ResponseData<EFileError> res = new ResponseData<EFileError>(fileSession.getScode(), "0000", "uploadfile");
		res.setError(error);
		this.send(ch, res.toString());
	}
}
