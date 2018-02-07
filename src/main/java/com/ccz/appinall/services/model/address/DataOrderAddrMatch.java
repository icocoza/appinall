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

/*
 	static public Document makeDocument(String[] sp) {
		Document doc = new Document();
		doc.put("zip", sp[0]);	//우편번호 
		doc.put("sido", sp[1]);	//시
		doc.put("sigu", sp[3]);	//시군구
		doc.put("eub", sp[5]);	//읍면 
		doc.put("rcode", sp[7]); //도로명코드 
		doc.put("roadname", sp[8]);	//도로명 
		doc.put("buildid", sp[13]);	//건물관리번호 
		doc.put("delivery", sp[14]);	//다량배달처명 
		doc.put("buildname", sp[15]);	//시군구용건물명 
		doc.put("dongname", sp[17]);	//법정동명 
		doc.put("liname", sp[18]);	//리명 
		doc.put("hjdongname", sp[19]);	//행정동명 
		doc.put("buildno", Integer.parseInt(sp[11]));	//건물번호본번 
		doc.put("buildsubno", Integer.parseInt(sp[12]));	//건물번호부번 
		doc.put("dongcode", sp[16]);	//법정동코드 
		doc.put("jino", Integer.parseInt(sp[21]));	//지번본번 
		doc.put("eubseq", Integer.parseInt(sp[22]));	//읍면동일련번호 
		doc.put("jisubno", Integer.parseInt(sp[23]));	//지번부번 
		doc.put("base", Integer.parseInt(sp[10])); //지하유무 
		doc.put("mnt", Integer.parseInt(sp[20]));	//산유무 
		doc.put("id", sp[16] + sp[7] +"-"+ sp[11] +"-"+ sp[12] +"-"+ sp[10]);
		return doc;
	}
		  doc.put("x", ent.x);
		  doc.put("y", ent.y);
		  doc.put("lac", ent.getLac());
		  doc.put("lon", ent.getLon());
		  doc.put("lactitude", ent.getLactitude());
		  doc.put("longitude", ent.getLongitude());

 */