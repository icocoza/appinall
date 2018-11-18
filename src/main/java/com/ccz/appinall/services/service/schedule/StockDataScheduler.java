package com.ccz.appinall.services.service.schedule;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ccz.appinall.common.rdb.DbCommonManager;
import com.ccz.appinall.services.domain.stock.DailySiseParser;
import com.ccz.appinall.services.domain.stock.StockCodes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StockDataScheduler {

	@Autowired StockCodes stockCodes;
	private final String SISE_URL_FORMAT = "https://finance.naver.com/item/sise.nhn?code=%06d";
	
	public StockDataScheduler(@Autowired StockCodes stockCodes) {
		
	}
	//[Seconds] [Minutes] [Hours] [Day of month] [Month] [Day of week] [Year]")
	@Scheduled(cron = "0 0 18 * * ?")
	public void doSchedule() throws NumberFormatException, IOException {
		if(isExistTodayStockData() == true) {
			log.info("[SISE_SCHEDULE_INIT] it might be holiday or already downloaded the stock data.");
			return;
		}
			
		for(String stockCode : stockCodes.getStockCodes())
			addStockData(stockCode, stockCodes.getStockName(stockCode));
	}
	
	private void addStockData(String stockCode, String stockName) {
		try {
			DailySiseParser dsp = new DailySiseParser(Integer.parseInt(stockCode), stockName, String.format(SISE_URL_FORMAT, Integer.parseInt(stockCode)));
			if( DbCommonManager.getInst().insertStockData(dsp) == false )
				log.error("[SISE_DB] fail to insert the StockData into DB: " + stockCode);
			Thread.sleep(30);
		} catch (Exception e) {
			log.error("[SISE_ERROR]" + stockCode + " - " +e.getMessage());
		}
	}
	
	private boolean isExistTodayStockData() throws NumberFormatException, IOException {
		String stockCode = stockCodes.getKospiCodes().iterator().next();
		DailySiseParser dsp = new DailySiseParser(Integer.parseInt(stockCode), stockCodes.getStockName(stockCode), String.format(SISE_URL_FORMAT, Integer.parseInt(stockCode)));
		return DbCommonManager.getInst().isExistStockDate(dsp.code, dsp.stockAt);
	}
	
}
