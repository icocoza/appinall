package com.ccz.appinall.services.action.address;

import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.services.action.CommonAction;
import com.ccz.appinall.services.action.address.RecDataAddr.*;
import com.ccz.appinall.services.type.enums.EAddrCmd;
import com.ccz.appinall.services.type.enums.EAuthError;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.Channel;

public class AddressCommandAction extends CommonAction {

	public AddressCommandAction(Object sessionKey) {
		super(sessionKey);
	}

	@Override
	public boolean processPacketData(Channel ch, String[] data) {
		return false;
	}

	@Override
	public boolean processJsonData(Channel ch, JsonNode jdata) {
		ResponseData<EAuthError> res = new ResponseData<EAuthError>(jdata.get("scode").asText(), jdata.get("rcode").asText(), jdata.get("cmd").asText());
		
		switch(EAddrCmd.getType(res.getCommand())) {
		case search:
			res = this.doSearch(res, new RecDataAddr().new DataSearchAddr(jdata));
			break;
		case orderrequest:
			res = this.doOrderRequest(res, new RecDataAddr().new DataOrderRequest(jdata));
			break;
		case orderlist:
			res = this.doOrderList(res, new RecDataAddr().new DataOrderList(jdata));
			break;
		case orderdetail:
			res = this.doOrderDetail(res, new RecDataAddr().new DataOrderDetail(jdata));
			break;
		case deliverselect:
			res = this.doDeliverSelect(res, new RecDataAddr().new DataDeliverSelectOnOrder(jdata));
			break;
		case ordersearch:
			res = this.doOrderSearch(res, new RecDataAddr().new DataOrderSearch(jdata));
			break;
		case orderselect:
			res = this.doOrderSelect(res, new RecDataAddr().new DataOrderSelectOnDelivers(jdata));
			break;
		case watchorder:
			res = this.doWatchOrder(res, new RecDataAddr().new DataOrderDetailOnDelivers(jdata));
			break;
		case ordercancel:
			res = this.doOrderCancel(res, new RecDataAddr().new DataOrderCancelOnDelivers(jdata));
			break;
		case ordercomplete:
			res = this.doOrderComplete(res, new RecDataAddr().new DataOrderCompleteOnDelivers(jdata));
			break;
		case deliverplan:
			res = this.doDeliverPlan(res, new RecDataAddr().new DataDeliverPlan(jdata));
			break;
		default:
			return false;
		}
		if(res != null)
			send(ch, res.toString());
		return true;
	}

	private ResponseData<EAuthError> doSearch(ResponseData<EAuthError> res, DataSearchAddr data) {
		AddressInference ai = new AddressInference(data.getSearch());
		System.out.println(ai.toFormat());
		System.out.println(ai.toString());
		
		
		return null;
	}
	private ResponseData<EAuthError> doOrderRequest(ResponseData<EAuthError> res, DataOrderRequest data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderList(ResponseData<EAuthError> res, DataOrderList data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderDetail(ResponseData<EAuthError> res, DataOrderDetail data) {
		return null;
	}
	private ResponseData<EAuthError> doDeliverSelect(ResponseData<EAuthError> res, DataDeliverSelectOnOrder data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderSearch(ResponseData<EAuthError> res, DataOrderSearch data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderSelect(ResponseData<EAuthError> res, DataOrderSelectOnDelivers data) {
		return null;
	}
	private ResponseData<EAuthError> doWatchOrder(ResponseData<EAuthError> res, DataOrderDetailOnDelivers data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderCancel(ResponseData<EAuthError> res, DataOrderCancelOnDelivers data) {
		return null;
	}
	private ResponseData<EAuthError> doOrderComplete(ResponseData<EAuthError> res, DataOrderCompleteOnDelivers data) {
		return null;
	}
	private ResponseData<EAuthError> doDeliverPlan(ResponseData<EAuthError> res, DataDeliverPlan data) {
		return null;
	}
}
