package com.ccz.appinall.services.service.schedule;

import com.ccz.appinall.common.rdb.DbCommonManager;
import com.ccz.appinall.services.domain.stock.AnnualResultsParser;
import com.ccz.appinall.services.domain.stock.StockCodes;

public class StockDataFinance {

	StockCodes stockCodes;
	int thisYear, lastYear;
	private final String FINANCE_URL_FORMAT = "https://m.stock.naver.com/item/main.nhn#/stocks/%06d/annual";
	
	public StockDataFinance(StockCodes stockCodes, int thisYear) {
		this.stockCodes = stockCodes;
		this.thisYear = thisYear;
	}
	
	public void doLoadFinanceData() throws Exception {
		
		if(this.isExistFinance()==true)
			return;
		if(lastYear == thisYear-1)
			return;
		
	}

	private boolean isExistFinance() throws Exception {
		String stockCode = stockCodes.getKospiCodes().iterator().next();
		int code = Integer.parseInt(stockCode);
		AnnualResultsParser ar = new AnnualResultsParser(code, String.format(FINANCE_URL_FORMAT, code));
		for(int i=0; i<ar.annualResult.length; i++) {
			if( DbCommonManager.getInst().isExistFinanceData(code, ar.annualResult[i].year) == false )
				return true;
			lastYear = ar.annualResult[i].year;
		}
		return false;
	}
}
