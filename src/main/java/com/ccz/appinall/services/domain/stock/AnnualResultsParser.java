package com.ccz.appinall.services.domain.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnualResultsParser {
	public int code;//, 코드, string
	public AnnualResult[] annualResult;
	
	public AnnualResultsParser(int code, String url) throws Exception {
		Document doc = Jsoup.connect(url).get();	//thread-safe
		Elements ftable = doc.select("table.f_table");
		Elements periods = ftable.first().select("tr.period").first().select("span.time");
		if(periods.size() < 1)
			return;
		annualResult = new AnnualResult[periods.size()];
		for(int i=0; i<periods.size(); i++)
			annualResult[i] = new AnnualResult(periods.get(i).text());
		
		Elements stockData = ftable.first().select("tbody").first().select("tr");
		for(int i=0; i<11; i++) {
			Elements items = stockData.get(i).select("td.stock_up");	//sales
			for(int j=0; j<items.size(); j++) {
				annualResult[i].setData(i, items.get(j).text());
			}
		}
		this.code = code;
	}
	
	public class AnnualResult {
		private String[] datas = new String[11];
		
		public int year=0;	//yyyy.mm
		public long sales;	//long * 100000000
		public long bizProfit;//long * 100000000
		public long netProfit;//long * 100000000
		public float bizProfitRate;
		public float netProfitRate;
		public float roeRate;
		public float debtRate;
		public float quickRate;
		public float reserveRate;
		public int eps, bps;
		
		public AnnualResult(String s) {
			try {
				year = Integer.parseInt(s.split(".", -1)[0]);
			}catch(Exception e) {
				log.error(e.getMessage());
			}
		}
		
		public void setData(int i, String data) {
			datas[i] = data;
		}
		
		public long getSales() {	return DailySiseParser.getLong(datas[0]);	}
		public long getBizProfit() {	return DailySiseParser.getLong(datas[1]);	}
		public long getNetProfit() {	return DailySiseParser.getLong(datas[2]);	}
		public float getBizProfitRate() {	return DailySiseParser.getFloat(datas[3]);	}
		public float getNetProfitRate() {	return DailySiseParser.getFloat(datas[4]);	}
		public float getRoeRate() {	return DailySiseParser.getFloat(datas[5]);	}
		public float getDebtRate() {	return DailySiseParser.getFloat(datas[6]);	}
		public float getQuickRate() {	return DailySiseParser.getFloat(datas[7]);	}
		public float getReverseRate() {	return DailySiseParser.getFloat(datas[8]);	}
		public long getEps() {	return DailySiseParser.getLong(datas[9]);	}
		public long getBps() {	return DailySiseParser.getLong(datas[10]);	}
	}
}
