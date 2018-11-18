package com.ccz.appinall.services.domain.stock;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DailySiseParser {
	public int code;//, 코드, string
	public String name;
	public int current;//, 현재가,int
	public int diff;//변화량 
	public float fluctuation;//, 등락률,float
	public long volumeRate;//, 거래량, long
	public long volumeWon;//, 거래총액 * 1,000,000, long
	public int faceValue;//, 액면가(‘원’), int
	public float per;//, PER(‘배’), float
	public int eps;//, EPS(‘원’), int
	public long marketTotal;//, 시가총액(‘억원’) * 100,000,000, long
	public long stockCount;//, 상장주식수, long
	public long foreigner;//, 외국인현재(‘천주’) * 1,000, long
	public long capital=0;//, 자본금(‘백만’) * 1,000,000, long
	
	public int max52w, min52w;//52주 최대, 최소
	public float pbr;//, PBR(‘배’), float
	public int bps;//, BPS(‘원’), int
	public float dividendRate;//, 배당수익률(‘%’), float
	public float sameLinePer;//, 동종업종PER(‘배’), float
	public float sameLinePerRate;//동종업계 등락
	public Timestamp stockAt;
	
	public DailySiseParser(int code, String name, String url) throws IOException {
		Document doc = Jsoup.connect(url).get();	//thread-safe
		Elements elements = doc.select("div.section").first().select("tbody").first().children();
		
		this.code = code;
		this.name = name;
		this.current = getInt(elements.get(0).select("td").first().text());//115,500
		this.diff = getUpDown(elements.get(1).select("td").first().text());//하락 4,000 -> -4000
		this.fluctuation = getFloatPercent(elements.get(2).select("td").first().text());////-3.35%
		this.volumeRate = getLong(elements.get(3).select("td").first().text());//613,508
		this.volumeWon = getLong(elements.get(4).select("td").first().text()) * 1000000l;
		this.faceValue = getIntWon(elements.get(5).select("td").first().text()); //500원 
		this.per = getFloat(elements.get(9).getElementById("_sise_per").text()); //32.22
		this.eps = getInt(elements.get(9).getElementById("_sise_eps").text()); //1,602
		this.marketTotal = getIntWon(elements.get(11).select("td").first().text()) * 100000000l; //96,309억원
		this.stockCount = getLong(elements.get(11).select("td").next().next().text()) * 1000l;//83,384,673
		this.foreigner = getIntStock(elements.get(12).select("td").first().text()) * 1000l;//20,507천주
		this.capital = getIntStock(elements.get(12).select("td").next().next().text()) * 1000000l;//41,742백만
		MinMax minmax = getMinMax(doc);
		this.max52w = minmax.max;
		this.min52w = minmax.min;
		this.bps = getIntBps(getBps(doc));
		this.pbr = bps != 0 ? (float)current / (float)this.bps : 0f;
		this. dividendRate = getFloat(getDividendRate(doc));
		this.sameLinePer = getFloat(getSameLinePer(doc));
		this.sameLinePerRate = getFloatPercent(getSameLinePerRate(doc));
		this.stockAt = getDate(doc.select("div.description").first().select("em.date").first().text());
	}
	
	public String getDividendRate(Document doc) {
		try {
			return doc.select("div.aside_invest_info").first().getElementById("_dvr").text();
		}catch(Exception e) {
			
		}
		return "0";
	}
	
	public String getSameLinePer(Document doc) {
		try {
			Elements elements = doc.select("div.aside_invest_info").first().select("div");
			if(elements.size()>21)
				return elements.get(21).select("tbody").select("tr").first().select("em").text();
		}catch(Exception e) {
		}
		return "0";
	}
	public String getSameLinePerRate(Document doc) {
		try {
			Elements elements = doc.select("div.aside_invest_info").first().select("div");
			if(elements.size()>21)
				return elements.get(21).select("tbody").select("tr").next().select("em").text();
		}catch(Exception e) {
		}
		return "";
	}

	public String getBps(Document doc) {
		try {
			Elements elements = doc.select("table.per_table").first().select("tbody").next().select("tr");
			if(elements.size()>2)
				return elements.get(2).select("em").text();
		}catch(Exception e) {
		}
		return "0";
	}
	
	private MinMax getMinMax(Document doc) {
		try {
			Elements elements = doc.select("div.aside_invest_info").first().select("div[id=tab_con1]").first().select("table.rwidth");
			return new MinMax(elements.first().select("tbody").select("tr").next().text());//52주최고l최저 168,000 l 103,000
		}catch(Exception e) {
		}
		return new MinMax();
	}
	private Timestamp getDate(String s) {
		try {
			String[] split = s.split(" ", -1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		    Date parsedDate = dateFormat.parse(split[0]);
		    return new java.sql.Timestamp(parsedDate.getTime());
		}catch(Exception e) {
			return null;
		}
	}
	public static int getInt(String s) {
		try {
			return Integer.parseInt(s.replaceAll(",", ""));
		}catch(Exception e) {
			return 0;
		}
	}
	private int getIntWon(String s) {
		return getInt(s.replace("억원", "").replace("원", ""));
	}
	private int getIntStock(String s) {
		return getInt(s.replace("천주", "").replaceAll("백만", ""));
	}
	private int getIntBps(String s) {
		return getInt(s.substring(s.indexOf(' ')+1));
	}
	
	private int getUpDown(String s) {
		return getInt(s.replace("하락", "-").replace("상승", "").replaceAll(" ","").trim());
	}
	
	private float getFloatPercent(String s) {
		try {
			return Float.parseFloat(s.replace("%", ""));
		}catch(Exception e) {
			return 0;
		}
	}

	public static float getFloat(String s) {
		try {
			return Float.parseFloat(s);
		}catch(Exception e) {
			return 0;
		}
	}

	public static long getLong(String s) {
		try {
			return Long.parseLong(s.replaceAll(",", ""));
		}catch(Exception e) {
			return 0;
		}
	}
	
	class MinMax {
		public int min=0, max=0;
		public MinMax() {}
		
		public MinMax(String s) {
			//52주최고l최저 168,000 l 103,000
			try {
				int pos = s.indexOf(' ');
				String minmax = s.substring(pos+1);
				String[] split = minmax.split("l", -1);
				max = getInt(split[0].trim());
				min = getInt(split[1].trim());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
