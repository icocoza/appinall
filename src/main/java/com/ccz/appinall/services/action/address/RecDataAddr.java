package com.ccz.appinall.services.action.address;

import com.ccz.appinall.services.action.RecDataCommon;
import com.ccz.appinall.services.type.enums.EGoodsSize;
import com.ccz.appinall.services.type.enums.EGoodsType;
import com.ccz.appinall.services.type.enums.EGoodsWeight;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;

public class RecDataAddr {

	@Getter
	public class DataSearchAddr extends RecDataCommon {

		private String search;
		
		public DataSearchAddr(JsonNode jnode) {
			super(jnode);
			this.search = jnode.get("search").asText();
		}
	}
	
	@Getter
	public class DataOrderRequest extends RecDataCommon {
		
		private String to_addrid, from_addrid;
		private String name, notice;
		private EGoodsSize size;
		private EGoodsWeight weight;
		private EGoodsType type;
		private int price;
		private long begintime, endtime;
		private String photourl;
		
		public DataOrderRequest(JsonNode jnode) {
			super(jnode);
			this.to_addrid = jnode.get("to_addrid").asText();
			this.from_addrid = jnode.get("from_addrid").asText();
			this.name = jnode.get("name").asText();
			this.notice = jnode.get("notice").asText();
			this.size = EGoodsSize.getType(jnode.get("size").asText());
			this.weight = EGoodsWeight.getType(jnode.get("weight").asText());
			this.type = EGoodsType.getType(jnode.get("type").asText());
			this.price = jnode.get("price").asInt();
			this.begintime = jnode.get("begintime").asLong();
			this.endtime = jnode.get("endtime").asLong();
			this.photourl = jnode.get("photourl").asText();
		}
	}
	
	@Getter
	public class DataOrderList extends RecDataCommon {
		private int offset, count;
		public DataOrderList(JsonNode jnode) {
			super(jnode);
			
			this.offset = jnode.get("offset").asInt();
			this.count = jnode.get("count").asInt();
		}
		
	}
	
	@Getter
	public class DataOrderDetail extends RecDataCommon {
		private String orderid;
		
		public DataOrderDetail(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
		}
	}
	
	@Getter
	public class DataDeliverSelectOnOrder extends RecDataCommon {
		private String orderid, deliverid;
		
		public DataDeliverSelectOnOrder(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
			this.deliverid = jnode.get("deliverid").asText();
		}
	}

	@Getter
	public class DataOrderSearch extends RecDataCommon {
		private String to_addrid, from_addrid;
		private EGoodsSize size;
		private EGoodsWeight weight;
		private EGoodsType type;

		public DataOrderSearch(JsonNode jnode) {
			super(jnode);
			this.to_addrid = jnode.get("to_addrid").asText();
			this.from_addrid = jnode.get("from_addrid").asText();
			this.size = EGoodsSize.getType(jnode.get("size").asText());
			this.weight = EGoodsWeight.getType(jnode.get("weight").asText());
			this.type = EGoodsType.getType(jnode.get("type").asText());
		}
	}
	
	@Getter
	public class DataOrderSelectOnDelivers extends RecDataCommon {
		private String delivererid, orderid;
		private long begintime, endtime;	//deliver가 제안하는 시작시간, 끝시간
		
		public DataOrderSelectOnDelivers(JsonNode jnode) {
			super(jnode);
			this.delivererid = jnode.get("delivererid").asText();
			this.orderid = jnode.get("orderid").asText();
			this.begintime = jnode.get("begintime").asLong();
			this.endtime = jnode.get("endtime").asLong();
		}
	}
	
	@Getter
	public class DataOrderDetailOnDelivers extends RecDataCommon {
		private String orderid;
		
		public DataOrderDetailOnDelivers(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
		}
	}
	
	@Getter
	public class DataOrderCancelOnDelivers extends RecDataCommon {
		private String orderid, delivererid;
		
		public DataOrderCancelOnDelivers(JsonNode jnode) {
			super(jnode);
			this.delivererid = jnode.get("delivererid").asText();
			this.orderid = jnode.get("orderid").asText();
		}
	}

	@Getter
	public class DataOrderCompleteOnDelivers extends RecDataCommon {
		private String delivererid, orderid;
		private String photourl;
		
		public DataOrderCompleteOnDelivers(JsonNode jnode) {
			super(jnode);
			this.delivererid = jnode.get("delivererid").asText();
			this.orderid = jnode.get("orderid").asText();
			this.photourl = jnode.get("photourl").asText();
		}
	}
	
	@Getter
	public class DataDeliverPlan extends RecDataCommon {
		private String to_addrid, from_addrid;
		private EGoodsSize size;
		private EGoodsWeight weight;
		private EGoodsType type;
		private long begintime, endtime;
		
		public DataDeliverPlan(JsonNode jnode) {
			super(jnode);
			this.to_addrid = jnode.get("to_addrid").asText();
			this.from_addrid = jnode.get("from_addrid").asText();
			this.size = EGoodsSize.getType(jnode.get("size").asText());
			this.weight = EGoodsWeight.getType(jnode.get("weight").asText());
			this.type = EGoodsType.getType(jnode.get("type").asText());
			this.begintime = jnode.get("begintime").asLong();
			this.endtime = jnode.get("endtime").asLong();
		}
	}

}
