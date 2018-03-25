package com.ccz.appinall.library.datastore;

import java.io.IOException;

import com.ccz.appinall.library.type.WebsocketPacketData;
import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.ccz.appinall.library.util.AsciiSplitter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;

public class WebsocketTextData implements IDataAccess {
	
	JsonNode jcmd;
	String[] splitCommand;
	String rawString;
	
	public WebsocketTextData(WebsocketPacketData wsdata) throws JsonProcessingException, IOException {
		rawString = wsdata.toString();
		if(rawString==null)
			return;
		if(rawString.startsWith("{")) {
			ObjectMapper mapper = new ObjectMapper();
		    jcmd = mapper.readTree(rawString);
		}else
			splitCommand = AsciiSplitter.splitChunk(rawString);
		wsdata.discardBuf();
	}

	@Override
	public void write(byte[] buf, int length) {
		//wsdata.write(buf);
	}

	@Override
	public void write(ByteBuf buf) {
		//wsdata.write(buf);
	}

	@Override
	public boolean flush() {
		return false;
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.wstext;
	}

	@Override
	public long size() {
		return -1;//wsdata.size();
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
		if(jcmd != null) {
			if(jcmd.has("action"))	
				return jcmd.get("action").asText();
		}else
			return splitCommand[0];
		return null;
	}

	@Override
	public boolean isJson() {
		return jcmd != null;
	}

	@Override
	public String[] getSplitData() {
		return splitCommand;
	}

	@Override
	public String getStringData() {
		return rawString;
	}

	@Override
	public JsonNode getJsonData() {
		return jcmd;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
