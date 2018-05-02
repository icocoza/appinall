package com.ccz.appinall.services.controller.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.address.RecDataAddr.*;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.enums.EAddrCmd;
import com.ccz.appinall.services.enums.EAddrError;
import com.ccz.appinall.services.repository.redis.OrderGeoRepository;
import com.ccz.appinall.services.service.SendMessageManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AddressCommandAction extends CommonAction {
	
	final int MAX_LIST_COUNT = 20;
	
	public ResponseData<EAddrError> result;
	
	@Autowired
	OrderGeoRepository geoRepository;
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	SendMessageManager sendMessageManager;
	
	public AddressCommandAction() {
		super.setCommandFunction(EAddrCmd.addr_search.getValue(), doSearch);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean processCommand(Channel ch, JsonNode jdata) {
		String cmd = jdata.get("cmd").asText();
		ResponseData<EAddrError> res = new ResponseData<EAddrError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), cmd);
		AuthSession session = (AuthSession) ch.attr(chAttributeKey.getAuthSessionKey()).get();
		
		ICommandFunction cmdFunc = super.getCommandFunction(cmd);
		if(cmdFunc!=null) {
			res = (ResponseData<EAddrError>) cmdFunc.doAction(session, res, jdata);
			send(ch, res.toJsonString());
			return true;
		}
		return false;
	}
	/*
	 * 1. fail 된 검색어를 별도로 모아두어야 함
	 * 2. 한 사용자에 의해 반복적으로 검색어가 들어 올 경우 모아 두어야 함 
	 * */
	ICommandFunction<AuthSession, ResponseData<EAddrError>, JsonNode> doSearch = (AuthSession session, ResponseData<EAddrError> res, JsonNode jnode) -> {
		DataSearchAddr data = new RecDataAddr().new DataSearchAddr(jnode);
		JsonNode jsonNode =  null;
		AddressInference ai = new AddressInference(data.getSearch());
		try {
			jsonNode = AddressElasticSearch.getInst().searchAddresByRestJson(ai);
		} catch (Exception e) {
			e.printStackTrace();
			return res.setError(EAddrError.failed_search);
		}
		if(jsonNode == NullNode.instance)
			return res.setError(EAddrError.invalid_search);
		JsonNode hitsNode = jsonNode.get("hits");
		if(hitsNode == null )
			return res.setError(EAddrError.empty_search);
		JsonNode totalNode = hitsNode.get("total");
		if(totalNode==null || totalNode.asInt() == 0)
			return res.setError(EAddrError.no_search_result);
		
		ArrayNode arrNode = copySearshResultToResponse((ArrayNode) hitsNode.get("hits"));
		return res.setParam("data", arrNode).setError(EAddrError.ok);
	};
	

	private ArrayNode copySearshResultToResponse(ArrayNode arrNode) {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode copyArrNode = mapper.createArrayNode();
		
		for(int i=0; i<arrNode.size(); i++) {
			JsonNode srcNode = arrNode.get(i).get("_source");
			if(srcNode == NullNode.instance)
				continue;
			copyArrNode.add(srcNode);
		}
		return copyArrNode;
	}
	
}
