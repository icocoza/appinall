package com.ccz.appinall.services.model.db.stock;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ccz.appinall.library.dbhelper.DbReader;
import com.ccz.appinall.library.dbhelper.DbRecord;
import com.ccz.appinall.services.domain.stock.DailySiseParser;

public class RecStockValue extends DbRecord {
	static final String TBL_NAME = "stockvalue";

	public int code;//, 코드, string
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
	public long capital;//, 자본금(‘백만’) * 1,000,000, long
	
	public int max52w, min52w;//52주 최대, 최소
	public float pbr;//, PBR(‘배’), float
	public int bps;//, BPS(‘원’), int
	public float dividendRate;//, 배당수익률(‘%’), float
	public float sameLinePer;//, 동종업종PER(‘배’), float
	public float sameLinePerRate;//동종업계 등락

	public Timestamp stockAt;//, 날짜, 
	
	public RecStockValue(String poolName) {
		super(poolName);
	}

	@Override
	public boolean createTable() {
		String sql = String.format("CREATE TABLE IF NOT EXISTS %s ("
				+ "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, code INTEGER NOT NULL, current INTEGER NOT NULL, diff INTEGER, fluctuation FLOAT NOT NULL, volumeRate LONG NOT NULL, "
				+ "volumeWon LONG NOT NULL, faceValue INTEGER NOT NULL, per FLOAT NOT NULL, eps INTEGER DEFAULT 0, marketTotal LONG NOT NULL, "
				+ "stockCount LONG NOT NULL, foreigner LONG NOT NULL, capital LONG NOT NULL, "
				+ "max52w INTEGER, min52w INTEGER, pbr FLOAT DEFAULT 0, bps INTEGER DEFAULT 0, dividendRate FLOAT DEFAULT 0, sameLinePer FLOAT DEFAULT 0,"
				+ "sameLinePerRate FLOAT, stockAt DATETIME NOT NULL, "
				+ "INDEX idx_code(code), INDEX idx_stockAt(stockAt), INDEX idx_per(per))", RecStockValue.TBL_NAME);
		return super.createTable(sql);
	}

	@Override
	protected DbRecord doLoad(DbReader rd, DbRecord r) {
		RecStockValue rec = (RecStockValue)r;
		rec.code = rd.getInt("code");
		rec.current = rd.getInt("current");
		rec.diff = rd.getInt("diff");
		rec.fluctuation = rd.getFloat("fluctuation");
		rec.volumeRate = rd.getLong("volumeRate");
		rec.volumeWon = rd.getLong("volumeWon");
		rec.faceValue = rd.getInt("faceValue");
		rec.marketTotal = rd.getLong("marketTotal");
		rec.foreigner = rd.getLong("foreigner");
		
		rec.stockCount = rd.getLong("stockCount");
		rec.capital = rd.getLong("rec.capital");
		rec.max52w = rd.getInt("max52w");
		rec.min52w = rd.getInt("min52w");
		rec.per = rd.getFloat("per");
		rec.eps = rd.getInt("eps");
		rec.pbr = rd.getFloat("pbr");
		rec.bps = rd.getInt("bps");
		rec.dividendRate = rd.getFloat("dividendRate");
		rec.sameLinePer = rd.getFloat("sameLinePer");
		rec.sameLinePer = rd.getFloat("sameLinePer");
		rec.stockAt = rd.getDate("stockAt");
		return rec;
	}

	@Override
	protected DbRecord onLoadOne(DbReader rd) {
		return doLoad(rd, this);
	}

	@Override
	protected DbRecord onLoadList(DbReader rd) {
		return doLoad(rd, new RecStockValue(poolName));
	}
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
	public boolean insert(DailySiseParser ds) {
		String strToDate = "STR_TO_DATE('"+formatter.format(ds.stockAt)+"', '%Y.%m.%d')";
		String sql = String.format("INSERT INTO %s (code, current, diff, fluctuation, volumeRate, volumeWon, faceValue, per, eps, marketTotal, "
				+ "stockCount, foreigner, capital, max52w, min52w, pbr, bps, dividendRate, sameLinePer, sameLinePerRate, stockAt) "
				+ "VALUES(%d, %d, %d, %f, %d, %d, %d, %f, %d, %d, %d, %d, %d, %d, %d, %f, %d, %f, %f, %f, %s)", RecStockValue.TBL_NAME,
				ds.code, ds.current, ds.diff, ds.fluctuation, ds.volumeRate, ds.volumeWon, ds.faceValue, ds.per, ds.eps, 
				ds.marketTotal, ds.stockCount, ds.foreigner, ds.capital, ds.max52w, ds.min52w, ds.pbr, ds.bps, ds.dividendRate, 
				ds.sameLinePer, ds.sameLinePerRate, strToDate);
		return super.createTable(sql);
	}
	
	public boolean isExist(int stockCode, Timestamp stockAt) {
		String strToDate = "STR_TO_DATE('"+formatter.format(stockAt)+"', '%Y.%m.%d')";
		String sql = String.format("SELECT * FROM %s WHERE code=%d AND stockAt=%s", RecStockValue.TBL_NAME, stockCode, strToDate);
		return super.exist(sql);
	}

}
