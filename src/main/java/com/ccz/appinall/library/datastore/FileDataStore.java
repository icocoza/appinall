package com.ccz.appinall.library.datastore;

import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.buffer.ByteBuf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDataStore implements IDataAccess {

	FileOutputStream fos;
	String path;
	long storedSize;
	
	public FileDataStore(String path) throws FileNotFoundException {
		this.path = path;
		fos = new FileOutputStream(path); 
	}
	
	@Override
	public void write(byte[] buf, int length) {
		try {
			fos.write(buf, 0, length);
			storedSize += length;
		} catch (IOException e) {
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
		try{
			fos.close();
			return true;
		}catch(Exception e) {
			return false;
		}
	}

	@Override
	public EDataStoreType dataType() {
		return EDataStoreType.file;
	}

	@Override
	public String getFilePath() {
		return path;
	}

	@Override
	public long size() {
		return storedSize;
	}

	@Override
	public boolean split(String s) {
		return false;
	}

	@Override
	public String getAction() {
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
