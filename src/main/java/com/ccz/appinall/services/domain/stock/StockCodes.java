package com.ccz.appinall.services.domain.stock;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StockCodes {
	public Map<String, String> Kospi  = new HashMap<String, String>();
	public Map<String, String> Kosdaq = new HashMap<String, String>();
	public Map<String, String> stockCodeName = new HashMap<String, String>();
	
	public StockCodes() {
		if(this.Load("/stockcode2.csv")==false)
			log.error("Stock Code Loading Failed");
	}
	public boolean Load(String codePath) {		 
		try {
			InputStream in = getClass().getResourceAsStream(codePath);
			Scanner scan = new Scanner(in, "euc-kr");
			scan.nextLine();
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] sp = line.split(",", -1);
				if(sp.length<3)
					continue;
				System.out.println(sp[2]);
				if("kospi".equals(sp[0].toLowerCase()))
					Kospi.put(sp[1], sp[2]);
				else if("kosdaq".equals(sp[0].toLowerCase()))
					Kosdaq.put(sp[1], sp[2]);
				stockCodeName.put(sp[1], sp[2]);
			}
			scan.close();
			in.close();
			return Kospi.size() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public Set<String> getKospiCodes() {
		return Kospi.keySet();
	}
	
	public Set<String> getKosdaqCodes() {
		return Kosdaq.keySet();
	}
	
	public String getKospiName(String code) {
		if(Kospi.containsKey(code)==false)
			return "";
		return Kospi.get(code);
	}
	public String getKosdaqName(String code) {
		if(Kosdaq.containsKey(code)==false)
			return "";
		return Kosdaq.get(code);
	}
	
	public Set<String> getStockCodes() {
		return stockCodeName.keySet();
	}
	public String getStockName(String code) {
		if(stockCodeName.containsKey(code)==false)
			return "";
		return stockCodeName.get(code);
	}
	
}
