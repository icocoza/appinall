package com.ccz.appinall.services.controller.address;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.geo.Point;

import com.ccz.appinall.services.controller.RecDataCommon;
import com.ccz.appinall.services.enums.EDeliverType;
import com.ccz.appinall.services.enums.EDeliveryType;
import com.ccz.appinall.services.enums.EGoodsSize;
import com.ccz.appinall.services.enums.EGoodsType;
import com.ccz.appinall.services.enums.EGoodsWeight;
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
		//private String senderid;
		private String to_addrid, from_addrid; /*not null*/
		private String name, notice;
		private EGoodsSize size;		/*not null*/
		private EGoodsWeight weight;	/*not null*/
		private EGoodsType type;	/*not null*/
		private int price;	/*not null*/
		private long begintime, endtime;	/*not null*/
		private String photourl;
		
		public DataOrderRequest(JsonNode jnode) {
			super(jnode);
			//this.senderid = jnode.get("senderid").asText();
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
			if(jnode.has("photourl"))
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
	public class DataSelectDeliverBySender extends RecDataCommon {
		private String orderid, deliverid;
		
		public DataSelectDeliverBySender(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
			this.deliverid = jnode.get("deliverid").asText();
		}
	}

	@Getter
	public class DataCancelDeliverBySender extends RecDataCommon {
		private String orderid, deliverid;
		
		public DataCancelDeliverBySender(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
			this.deliverid = jnode.get("deliverid").asText();
		}
	}

	@Getter
	public class DataOrderSearch extends RecDataCommon {
		private String to_addrid, from_addrid;
		private List<Point> gpsList;
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
	public class DataOrderSelectByDelivers extends RecDataCommon {
		private String orderid, deliverid;
		private long begintime, endtime;	//deliver가 제안하는 시작시간, 끝시간
		private int price;
		private EDeliverType delivertype;
		private EDeliveryType deliverytype;

		public DataOrderSelectByDelivers(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
			this.deliverid = jnode.get("userid").asText();
			this.begintime = jnode.get("begintime").asLong();
			this.endtime = jnode.get("endtime").asLong();
			this.price = jnode.get("price").asInt();
			this.delivertype = EDeliverType.valueOf(jnode.get("delivertype").asText());
			this.deliverytype = EDeliveryType.valueOf(jnode.get("deliverytype").asText());
		}
	}

	@Getter
	public class DataOrderCheckInByDelivers extends RecDataCommon {
		private String orderid;

		public DataOrderCheckInByDelivers(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
		}
	}
	
	public class DataDeliverMoving extends DataOrderDetail {

		public DataDeliverMoving(JsonNode jnode) {
			super(jnode);
		}
	}

	@Getter
	public class DataDeliverGotcha extends DataOrderDetail {
		public String startcode;
		
		public DataDeliverGotcha(JsonNode jnode) {
			super(jnode);
			if(jnode.has("startcode"))
				this.startcode = jnode.get("startcode").asText();
		}
	}

	@Getter
	public class DataDeliverDelivering extends DataOrderDetail {
		public DataDeliverDelivering(JsonNode jnode) {
			super(jnode);
		}
	}

	@Getter
	public class DataDeliveryCompleteByDelivers extends RecDataCommon {
		private String orderid;
		private List<String> photourl;
		private String endcode;
		private String message;
		
		public DataDeliveryCompleteByDelivers(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
			if(jnode.has("endcode"))
				this.endcode = jnode.get("endcode").asText();
			//this.photourl = jnode.get("photourl").asText();
			if(jnode.has("message"))
				this.message = jnode.get("message").asText();
		}
	}

	@Getter
	public class DataDeliveryConfirmBySender extends DataOrderDetail {
		
		private String deliverid;
		private String message;
		
		public DataDeliveryConfirmBySender(JsonNode jnode) {
			super(jnode);
			this.deliverid = jnode.get("deliverid").asText();
			this.message = jnode.get("message").asText();
		}
	}

	@Getter
	public class DataOrderDetailByDelivers extends RecDataCommon {
		private String orderid;
		
		public DataOrderDetailByDelivers(JsonNode jnode) {
			super(jnode);
			this.orderid = jnode.get("orderid").asText();
		}
	}
	
	@Getter
	public class DataOrderCancelByDelivers extends RecDataCommon {
		private String orderid, delivererid;
		
		public DataOrderCancelByDelivers(JsonNode jnode) {
			super(jnode);
			this.delivererid = jnode.get("delivererid").asText();
			this.orderid = jnode.get("orderid").asText();
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
