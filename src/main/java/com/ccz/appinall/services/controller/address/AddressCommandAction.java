package com.ccz.appinall.services.controller.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.address.RecDataAddr.*;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
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
	
	public ResponseData<EAllError> result;
	
	@Autowired
	OrderGeoRepository geoRepository;
	@Autowired
	ServicesConfig servicesConfig;
	@Autowired
	SendMessageManager sendMessageManager;
	
	public AddressCommandAction() {
		super.setCommandFunction(EAllCmd.addr_search, doSearch);
	}

	/*
	 * 1. fail 된 검색어를 별도로 모아두어야 함
	 * 2. 한 사용자에 의해 반복적으로 검색어가 들어 올 경우 모아 두어야 함 
	 * */
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doSearch = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataSearchAddr data = new RecDataAddr().new DataSearchAddr(jnode);
		JsonNode jsonNode =  null;
		AddressInference ai = new AddressInference(data.getSearch());
		try {
			jsonNode = AddressElasticSearch.getInst().searchAddresByRestJson(ai);
		} catch (Exception e) {
			e.printStackTrace();
			return res.setError(EAllError.failed_search);
		}
		if(jsonNode == NullNode.instance)
			return res.setError(EAllError.invalid_search);
		JsonNode hitsNode = jsonNode.get("hits");
		if(hitsNode == null )
			return res.setError(EAllError.empty_search);
		JsonNode totalNode = hitsNode.get("total");
		if(totalNode==null || totalNode.asInt() == 0)
			return res.setError(EAllError.no_search_result);
		
		ArrayNode arrNode = copySearshResultToResponse((ArrayNode) hitsNode.get("hits"));
		return res.setParam("data", arrNode).setError(EAllError.ok);
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
