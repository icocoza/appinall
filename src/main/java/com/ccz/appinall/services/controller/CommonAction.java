package com.ccz.appinall.services.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ChAttributeKey;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.type.inf.ICommandProcess;
import com.ccz.appinall.library.util.ProtocolWriter.IWriteProtocol;

import groovy.util.logging.Slf4j;
import io.netty.channel.Channel;

@Slf4j
@Component
public abstract class CommonAction implements ICommandProcess {
	
	@Autowired
	protected ChAttributeKey chAttributeKey;
	
	private Map<String, ICommandFunction> cmdFuncMap = new ConcurrentHashMap<>();
	
	public void send(Channel ch, String data) {
		Log.info(data);
		IWriteProtocol wp = ch.attr(chAttributeKey.getWriteKey()).get();
		wp.write(ch, data);
	}
	
	public Map<String, ICommandFunction> getCommandFunctions() {
		return cmdFuncMap;
	}

	public ICommandFunction getCommandFunction(String cmd) {
		return cmdFuncMap.get(cmd);
	}
	
	public void setCommandFunction(String cmd, ICommandFunction func) {
		cmdFuncMap.put(cmd, func);
	}
}