package com.ccz.appinall.library.datastore;

import java.io.UnsupportedEncodingException;

import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;

public class TextDataStore implements IDataAccess {
	
	StringBuilder txtStore = new StringBuilder();
	String[] splittedData = null;
	
	@Override
	public void write(byte[] buf, int length) {
		try {
			txtStore.append(new String(buf, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(ByteBuf buf) {
		byte[] data = new byte[buf.writerIndex()];
		buf.readBytes(data);
		this.write(data, data.length);
	}

	@Override
	public boolean flush() {
		return true;
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.string;
	}

	@Override
	public String getFilePath() {
		return "";
	}

	@Override
	public long size() {
		return txtStore.length();
	}

	@Override
	public boolean split(String s) {
		splittedData = txtStore.toString().split(s);
		return splittedData.length>1;
	}

	@Override
	public String getAction() {
		if(splittedData.length<2)	//if size 1, meaningless data
			return null;
		return splittedData[0];
	}

	@Override
	public boolean isJson() {
		return txtStore.toString().startsWith("{");
	}

	@Override
	public String[] getSplitData() {
		return splittedData;
	}

	@Override
	public String getStringData() {
		return txtStore.toString();
	}

	@Override
	public JsonNode getJsonData() {
		// TODO Auto-generated method stub
		return null;
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
