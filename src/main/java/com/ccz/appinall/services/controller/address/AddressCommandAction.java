package com.ccz.appinall.services.controller.address;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.type.inf.ICommandFunction;
import com.ccz.appinall.library.util.GeoUtil;
import com.ccz.appinall.services.controller.CommonAction;
import com.ccz.appinall.services.controller.address.RecDataAddr.*;
import com.ccz.appinall.services.controller.auth.AuthSession;
import com.ccz.appinall.services.enums.EAllCmd;
import com.ccz.appinall.services.enums.EAllError;
import com.ccz.appinall.services.model.db.RecAddress;
import com.ccz.appinall.services.repository.elasticsearch.AddressElasticSearch;
import com.ccz.appinall.services.repository.redis.GeoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

@Component
public class AddressCommandAction extends CommonAction {
	
	final int MAX_LIST_COUNT = 20;
	
	public ResponseData<EAllError> result;
	
	@Autowired ServicesConfig servicesConfig;
	@Autowired GeoRepository geoRepository;
	@Autowired AddressElasticSearch addressElasticSearch;
	
	public AddressCommandAction() {
		super.setCommandFunction(EAllCmd.addr_search, doSearch);
		super.setCommandFunction(EAllCmd.gps_search, doGpsSearch);
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
			jsonNode = addressElasticSearch.searchAddrByRestJson(ai);
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
	
	//Google gps -> WGS84
	//But GeoRadius is searched by DMS
	ICommandFunction<AuthSession, ResponseData<EAllError>, JsonNode> doGpsSearch = (AuthSession session, ResponseData<EAllError> res, JsonNode jnode) -> {
		DataGpsSearchAddr data = new RecDataAddr().new DataGpsSearchAddr(jnode);
		List<String> buildIds = geoRepository.searchOrder(GeoUtil.toDMS(data.getLon()), GeoUtil.toDMS(data.getLat()), 300, 15);
		List<RecAddress> addrList = DbAppManager.getInst().getAddressList(data.getScode(), buildIds);
		Set<String> hashSet = new HashSet<>();
		List<BuildingInfo> buildList = addrList.stream()
									   .filter(x -> hashSet.contains(x.buildname)==false)
									   .map(x -> {
										   hashSet.add(x.buildname);
										   return new BuildingInfo(x.buildid, x.buildname);
										}).collect(Collectors.toList());
		res.setParam("buildings", buildList);
		return buildList.size() > 0 ? res.setError(EAllError.ok) : res.setError(EAllError.empty_search);
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
