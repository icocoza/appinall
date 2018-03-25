package com.ccz.appinall.library.datastore;

import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.util.QueuedBuffer;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.buffer.ByteBuf;

public class WebsocketBinaryData implements IDataAccess {
	public static final String BINARY_DATA = "filedata";
	
	QueuedBuffer queuedBuffer;
	
	public WebsocketBinaryData(QueuedBuffer queuedBuffer) {
		this.queuedBuffer = queuedBuffer;
	}	
	
	@Override
	public byte[] getData() {
		if(queuedBuffer!=null)
			return queuedBuffer.readAll();
		return null;
	}
	
	@Override
	public void write(byte[] buf, int length) {
	}

	@Override
	public void write(ByteBuf buf) {
	}

	@Override
	public boolean flush() {
		return false;
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.file;
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public String getFilePath() {
		return null;
	}

	@Override
	public boolean split(String s) {
		return false;
	}

	@Override
	public String getAction() {
		return BINARY_DATA;	//fixed
	}

	@Override
	public String getCommand() {
		return null;
	}

	@Override
	public boolean isJson() {
		return false;
	}

	@Override
	public String[] getSplitData() {
		return null;
	}

	@Override
	public String getStringData() {
		return null;
	}

	@Override
	public JsonNode getJsonData() {
		return null;
	}

}
