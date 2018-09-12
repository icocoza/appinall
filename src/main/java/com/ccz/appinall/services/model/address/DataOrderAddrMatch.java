package com.ccz.appinall.services.model.address;

import java.io.IOException;

import org.bson.Document;

import com.ccz.appinall.services.model.db.RecDeliveryOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DataOrderAddrMatch {
	public DataOrderAddrMatch(RecDeliveryOrder order, Document from, Document to) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		ObjectNode jsonNode = (ObjectNode) mapper.readTree(json);
		ArrayNode arrNode = mapper.createArrayNode();
		arrNode.add(from.toJson());
		arrNode.add(to.toJson());
		jsonNode.put("address", arrNode.toString());
	}
}
