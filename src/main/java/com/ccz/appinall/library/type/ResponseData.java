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
	
	@Getter @Setter
	String token;
	@Getter @Setter
	String userid;
	
	Map<String, Object> mapParam = new HashMap<>();
	
	public ResponseData(String serviceCode, String code, String cmd) {
		this.serviceCode = serviceCode;
		this.code = code;
		this.cmd = cmd;
	}
	
	public String getCommand() {
		return cmd;
	}
	
	public ResponseData<T> setError(T error) {
		this.error = error;
		return this;
	}
	
	public ResponseData<T> setParam(String p) {
		mapParam.put("data", p);
		return this;
	}
	public ResponseData<T> setParam(int p) {
		mapParam.put("data", p);
		return this;
	}
	
	@SuppressWarnings("resource")
	public ResponseData<T> setParamFormat(String format, Object... args) {
		setParam(new Formatter().format(format, args).toString());
		return this;
    }
	
	public ResponseData<T> setParam(String k, Object v) {
		mapParam.put(k, v);
		return this;
	}
	
	public String getDataParam() {
		if(mapParam.containsKey("data"))
			return (String)mapParam.get("data");
		return "";
	}
	public String getDataParam(String key) {
		if(mapParam.containsKey(key))
			return (String)mapParam.get(key);
		return "";
	}

	public Map<String, Object> getParams() {
		return mapParam;
	}
	
	public String toJsonString() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objNode = (ObjectNode) objectMapper.convertValue(mapParam, JsonNode.class);
		objNode.put("scode", serviceCode);
		objNode.put("cmd", cmd);
		objNode.put("rcode", code);
		objNode.put("result", error.toString());
		//if(jsonData != null)
		//	objNode.set("data", jsonData);
		return objNode.toString();
	}
	@Override
	public String toString() {
		return toJsonString();
	}
}
