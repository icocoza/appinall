package com.ccz.appinall.services.controller.address;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.Document;

import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.enums.EAddrType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;


public class AddressInference {
	@Getter private String sido, sigu, eub;
	@Getter private String roadname, dongname;
	@Getter private String buildname;
	@Getter private int addrNo=0, addrSubNo=0;
	@Getter private EAddrType addrType;

	static Map<String, String> sidoMap = new HashMap<>();
	static Set<String> siguSet;
	static Map<String, String> siguMap = new HashMap<>();
	static Set<String> overlapSet;
	
	public AddressInference(String words) {
		String[] addrs = words.trim().split(" ", -1);
		parseAddress(addrs);
	}
	
	public String toFormat() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s-%s" , "sido", "sigu", "eub", "roadname", "roadname", "buildname", "NNN", "NNN");
	}
	public String toString() {
		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%d-%d" , sido, sigu, eub, roadname, dongname, buildname, addrNo, addrSubNo);
	}
	
/*
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
		doc.put("buildno", sp[11]);	//건물번호본번 
		doc.put("buildsubno", sp[12]);	//건물번호부번 
		doc.put("dongcode", sp[16]);	//법정동코드 
		doc.put("jino", sp[21]);	//지번본번 
		doc.put("eubseq", sp[22]);	//읍면동일련번호 
		doc.put("jisubno", sp[23]);	//지번부번 
		doc.put("base", sp[10]); //지하유무 
		doc.put("mnt", sp[20]);	//산유무 

 * */
	static public Document getMongoAddrId(String addrid) {
		Document doc = new Document();
		doc.put("buildid", addrid);
		return doc;
	}
	public Document getMongoDbFindDoc(boolean addrOnly) {
		Document doc = new Document();
		if(this.sido != null) doc.put("sido", this.sido);
		if(this.sigu != null) doc.put("sigu", this.sigu);
		if(this.eub != null) doc.put("eub", this.eub);
		if(EAddrType.road == addrType && this.roadname != null) 
			doc.put("roadname", this.roadname);
		else if(EAddrType.jibun == addrType && this.dongname != null) {
			if(this.dongname.endsWith("리") == false) 
				doc.put("dongname", this.dongname);
			else
				doc.put("liname", this.dongname);
		}
		if(EAddrType.road == addrType && this.addrNo>0) {
			doc.put("buildno", this.addrNo);
			if(this.addrSubNo>0)
				doc.put("buildsubno", this.addrSubNo);
		}
		else if(EAddrType.jibun == addrType && this.addrNo>0) {
			doc.put("jino", this.addrNo);
			if(this.addrSubNo>0)
				doc.put("jisubno", this.addrSubNo);
		}
		if(this.buildname != null && addrOnly == false) 
			doc.put("buildname", this.buildname);
		return doc;
	}
	
	public ObjectNode getAnd(ObjectMapper mapper, String value) {
		ObjectNode node = mapper.createObjectNode().put("query", value).put("operator", "and");
		return node;
	}
	
	public String getElasticSearchQuery() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrNode = mapper.createArrayNode();
		
		List<ObjectNode> nodeList = new ArrayList<>();
		if(this.sido != null)
			nodeList.add(mapper.createObjectNode().put("sido", this.sido));
		if(this.sigu != null) 
			nodeList.add(mapper.createObjectNode().put("sigu", this.sigu));
		if(this.eub != null) 
			nodeList.add(mapper.createObjectNode().put("eub", this.eub));
		if(EAddrType.road == addrType && this.roadname != null) 
			nodeList.add(mapper.createObjectNode().put("roadname", this.roadname));
		else if(EAddrType.jibun == addrType && this.dongname != null) {
			if(this.dongname.endsWith("리") == false) 
				nodeList.add(mapper.createObjectNode().put("dongname", this.dongname));
			else
				nodeList.add(mapper.createObjectNode().put("liname", this.dongname));
		}
		if(EAddrType.road == addrType && this.addrNo>0) {
			nodeList.add(mapper.createObjectNode().put("buildno", this.addrNo));
			if(this.addrSubNo>0)
				nodeList.add(mapper.createObjectNode().put("buildsubno", this.addrSubNo));
		}
		else if(EAddrType.jibun == addrType && this.addrNo>0) {
			nodeList.add(mapper.createObjectNode().put("jino", this.addrNo));
			if(this.addrSubNo>0)
				nodeList.add(mapper.createObjectNode().put("jisubno", this.addrSubNo));
		}
		if(this.buildname != null) 
			nodeList.add(mapper.createObjectNode().put("buildname", this.buildname));
		
		nodeList.stream().map(node -> mapper.createObjectNode().set("match", node)).forEach(node->arrNode.add(node));
		if(arrNode.size()<1)
			return null;
		JsonNode resultNode = mapper.createObjectNode().set("query", mapper.createObjectNode().set("bool", mapper.createObjectNode().set("should", arrNode)));
		String json = mapper.writeValueAsString(resultNode);
		return json;
	}
	
	public boolean hasAddress() {
		return ((roadname!=null || dongname!=null) && addrNo>0);
	}
	
	public boolean hasBuildNameOnly() {
		return hasAddress()==false && buildname!=null;
	}
	
	private boolean parseAddress(String[] addrs) {
		for(int i=0; i<addrs.length; i++) {
			if(addrs[i].equals("번지") || addrs[i].equals("번"))
				continue;
			if(sidoMap.containsKey(addrs[i]) == true) {
				sido = sidoMap.get(addrs[i]);
				continue;
			}
			if(siguSet.contains(addrs[i]) == true || overlapSet.contains(addrs[i]) == true) {
				sigu = addrs[i];	//sigu = siguSet.stream().filter(item -> item.equals(addrs[idx])).findFirst().get();
				continue;
			}else if(siguMap.containsKey(addrs[i])) {
				sigu = siguMap.get(addrs[i]);
				continue;
			}
			if(addrs[i].endsWith("로") || addrs[i].endsWith("길")) {
				roadname = (roadname==null) ? addrs[i] : roadname + addrs[i];
				addrType = EAddrType.road;
				continue;
			}
			if(addrs[i].endsWith("동") || addrs[i].endsWith("리") || addrs[i].endsWith("가")) {
				dongname = addrs[i];
				addrType = EAddrType.jibun;
				continue;
			}
			if(addrs[i].endsWith("읍") || addrs[i].endsWith("면")) {
				eub = addrs[i];
				addrType = EAddrType.jibun;
				continue;
			}
			List<String> addrList = StrUtil.splitKoreanNumeric(addrs[i]);
			if(addrList.size()>1) {
				if(parseAddress(addrList.toArray(new String[addrList.size()]))==true)
					return true;
				continue;
			}
			if(StrUtil.isNumericDash(addrs[i]) == true) {
				if(addrs[i].contains("-")) {
					String[] nums =  addrs[i].split("-", -1);
					addrNo = Integer.parseInt(nums[0]);
					addrSubNo = Integer.parseInt(nums[1]);
				}else
					addrNo = Integer.parseInt(addrs[i]);
				continue;
			}
			buildname = addrs[i];	//done when setting the buildname
			return true;
		}
		//^[ㄱ-ㅎ가-힣0-9]*$
		return false;
	}
	
	static private String[][] SIDO = { 
		{"경기도", "경기도"},
		{"강원도", "강원도"},
		{"충청북도", "충청북도", "충북"},
		{"충청남도", "충청남도", "충남"},
		{"경상북도", "경상북도", "경북"},
		{"경상남도", "경상남도", "경남"},
		{"전라북도", "전라북도", "전북"},
		{"전라남도", "전라남도", "전남"},
		{"제주도", "제주도", "제주특별자치도", "제주도시"},
		{"서울특별시", "서울", "서울시"},
		{"인천광역시", "인천", "인천시"},
		{"대전광역시", "대전", "대전시"},
		{"대구광역시", "대구", "대구시"},
		{"광주광역시", "광주", "광주시"},
		{"울산광역시", "울산", "울산시"},
		{"부산광역시", "부산", "부산시"},
		{"세종특별자치시", "세종", "세종시"}
	};
	static private String SIGUGUN = "가평군 고양시 고양시덕양구 고양시일산동구 고양시일산서구 과천시 광명시 광주시 구리시 군포시 김포시 남양주시 동두천시 부천시 성남시 성남시분당구 성남시수정구 성남시중원구 수원시 수원시권선구 수원시영통구 수원시장안구 "
			+ "수원시팔달구 시흥시 안산시 안산시단원구 안산시상록구 안성시 안양시 안양시동안구 안양시만안구 양주시 양평군 여주시 연천군 오산시 용인시 용인시기흥구 용인시수지구 용인시처인구 의왕시 의정부시 이천시 파주시 평택시 포천시 하남시 화성시 "
			+ "강릉시 고성군 동해시 삼척시 속초시 양구군 양양군 영월군 원주시 인제군 정선군 철원군 춘천시 태백시 평창군 홍천군 화천군 횡성군 "
			+ "계룡시 공주시 금산군 논산시 당진시 보령시 부여군 서산시 서천군 아산시 예산군 천안시 천안시동남구 천안시서북구 청양군 태안군 홍성군 "
			+ "괴산군 단양군 보은군 영동군 옥천군 음성군 제천시 증평군 진천군 청주시 청주시상당구 청주시서원구 청주시청원구 청주시흥덕구 충주시 "
			+ "경산시 경주시 고령군 구미시 군위군 김천시 문경시 봉화군 상주시 성주군 안동시 영덕군 영양군 영주시 영천시 예천군 울릉군 울진군 의성군 청도군 청송군 칠곡군 포항시 포항시남구 포항시북구 "
			+ "거제시 거창군 고성군 김해시 남해군 밀양시 사천시 산청군 양산시 의령군 진주시 창녕군 창원시 창원시마산합포구 창원시마산회원구 창원시성산구 창원시의창구 창원시진해구 통영시 하동군 함안군 함양군 합천군 "
			+ "고창군 군산시 김제시 남원시 무주군 부안군 순창군 완주군 익산시 임실군 장수군 전주시 전주시덕진구 전주시완산구 정읍시 진안군 "
			+ "강진군 고흥군 곡성군 광양시 구례군 나주시 담양군 목포시 무안군 보성군 순천시 신안군 여수시 영광군 영암군 완도군 장성군 장흥군 진도군 함평군 해남군 화순군 "
			+ "서귀포시 제주시 "
			+ "강남구 강동구 강북구 관악구 광진구 구로구 금천구 노원구 도봉구 동대문구 동작구 마포구 서대문구 서초구 성동구 성북구 송파구 양천구 영등포구 용산구 은평구 종로구 중랑구 " //중구 강서구
			+ "강화군 계양구 남동구 부평구 연수구 옹진군 " //남구 동구 서구 중구
			+ "대덕구 유성구 " //동구 서구 중구 
			+ "달서구 달성군 수성구 " //남구 동구 북구 서구 중구
			+ "울주군 " //남구 동구 북구 중구 
			+ "광산구 " //남구 동구 북구 서구 
			+ "금정구 기장군 동래구 부산진구 북구사상구 사하구 수영구 연제구 영도구 해운대구"; //남구 동구 서구 중구 강서구 

	static private String[][] SIGUGUN2 = {
			{"고양시 덕양구", "덕양구"}, {"고양시 일산동구", "일산동구"/*, "일산 동구"*/}, {"고양시 일산서구", "일산서구"/*, "일산 서구"*/},
			{"성남시 분당구", "분당구"}, {"성남시 수정구", "수정구"}, {"성남시 중원구", "중원구"}, 
			{"수원시 권선구", "권선구"}, {"수원시 영통구", "영통구"}, {"수원시 장안구"},
			{"수원시 팔달구"}, {"안산시 단원구"}, {"안산시 상록구"}, 
			{"안양시 동안구"}, {"안양시 만안구"},
			{"용인시 기흥구"}, {"용인시 수지구"}, {"용인시 처인구"},
			{"천안시 동남구", "동남구"}, {"천안시 서북구", "서북구"},
			{"청주시 상당구", "상당구"}, {"청주시 서원구", "서원구"}, {"청주시 청원구", "청원구"}, {"청주시 흥덕구", "흥덕구"},
			//{"포항시 남구", ""}, {"포항시 북구", ""},
			{"창원시 마산합포구", "마산합포구", "합포구"}, {"창원시 마산회원구", "마산회원구", "회원구"}, {"창원시 성산구", "성산구"}, {"창원시 의창구", "의창구"}, {"창원시 진해구", "진해구"},
			{"전주시 덕진구", "덕진구"}, {"전주시 완산구", "완산구"}
	};
	
	static private String[] OVERLAPPED = {"중구", "북구", "동구", "남구", "서구"};

	static {
		//init sido
		for(int i=0; i < SIDO.length; i++) {
			String groupName = SIDO[i][0]; 
			for(int j=0; j< SIDO[i].length; j++)
				sidoMap.put(SIDO[i][j], groupName);
		}
		
		String[] siguGroup = SIGUGUN.split(" ", -1);
		siguSet = new HashSet<String>(Arrays.asList(siguGroup));
		
		for(int i=0; i < SIGUGUN2.length; i++) {
			String groupName = SIGUGUN2[i][0]; 
			for(int j=0; j< SIGUGUN2[i].length; j++)
				siguMap.put(SIGUGUN2[i][j], groupName);
		}
		
		overlapSet = new HashSet<String>(Arrays.asList(OVERLAPPED));
	}

}
