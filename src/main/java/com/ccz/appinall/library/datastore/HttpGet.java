package com.ccz.appinall.library.datastore;

import java.util.HashMap;
import java.util.Map;

import com.ccz.appinall.library.type.HttpPacketData;
import com.ccz.appinall.library.type.enums.EDataStoreType;
import com.ccz.appinall.library.type.inf.IDataAccess;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.buffer.ByteBuf;

public class HttpGet implements IDataAccess{
	String method, uri, version;
	long   contentLength;
	String contentType, contentBoundary;
	String action, command; 	//user defined header
	
	Map<String, String> params = new HashMap<>();
	Map<String, String> httpHeaders = new HashMap<>();
	
	public HttpGet(HttpPacketData httpdata) {
		this.method = httpdata.method;
		this.uri = httpdata.uri;
		this.version = httpdata.version;
		this.contentLength = httpdata.contentLength;
		this.contentType = httpdata.contentType;
		this.contentBoundary = httpdata.contentBoundary;
		this.action = httpdata.action;
		this.command = httpdata.command;
		this.params = httpdata.params;
		this.httpHeaders = httpdata.httpHeaders;
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
		return EDataStoreType.httpget;
	}

	@Override
	public long size() {
		return contentLength;
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
		return action;
	}
	
	@Override
	public String getCommand() {
		return command;
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

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub
		return null;
	}

}

