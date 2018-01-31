package com.ccz.appinall.library.type;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ccz.appinall.library.util.AsciiSplitter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;

public class ResponseData<T> {
	public String serviceCode, code, cmd;
	
	@Getter 
	T error;
	@Getter 
	String param;
	
	@Getter @Setter
	String token;
	
	boolean bjson = true;
	Map<String, String> mapParam = new HashMap<>();
	
	@Getter
	JsonNode jsonData;
	
	public ResponseData(String serviceCode, String code, String cmd) {
		this.serviceCode = serviceCode;
		this.code = code;
		this.cmd = cmd;
	}
	
	public ResponseData(String serviceCode, String code, String cmd, boolean bjson) {
		this(serviceCode, code, cmd);
		this.bjson = bjson;
	}

	public String getCommand() {
		return cmd;
	}
	
	public ResponseData<T> setError(T error) {
		this.error = error;
		return this;
	}
	
	public ResponseData<T> setParam(String p) {
		this.param = p;
		return this;
	}
	
	@SuppressWarnings("resource")
	public ResponseData<T> setParam(String format, Object... args) {
		this.param = new Formatter().format(format, args).toString();
		return this;
    }
	
	public ResponseData<T> setParam(String k, String v) {
		mapParam.put(k, v);
		return this;
	}
	
	public ResponseData<T> setData(JsonNode jdata) {
		this.jsonData = jdata;
		return this;
	}
	
	public Map<String, String> getParams() {
		return mapParam;
	}
	
	public String toString() {
		if(bjson)
			return toJsonString();
		return toAscString();
	}
	
	private String toAscString() {
		StringBuilder sb = new StringBuilder();
		sb.append(serviceCode);
		sb.append(AsciiSplitter.CHUNK);
		sb.append(code);
		sb.append(AsciiSplitter.CHUNK);
		sb.append(cmd);
		sb.append(AsciiSplitter.CHUNK);
		sb.append(error.toString());
		sb.append(AsciiSplitter.CHUNK);
		sb.append(param);
		return sb.toString();
	}
	
	private String toJsonString() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objNode = objectMapper.createObjectNode();
		objNode.put("service", serviceCode);
		objNode.put("cmd", cmd);
		objNode.put("result", error.toString());
		Set<Entry<String, String>> sets = mapParam.entrySet();
		for(Entry<String,String> item : sets)
			objNode.put(item.getKey(), item.getValue());
		if(jsonData != null)
			objNode.set("data", jsonData);
		return objNode.toString();
	}
}
