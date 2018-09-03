package com.ccz.appinall.library.module.scrap;

import com.ccz.appinall.library.util.ConnectionPool;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

@Deprecated
public class WebDriverPool extends ConnectionPool<WebDriver> {
	
	private int width, height;
	private String webdriverBin, webdriverGecko;
	
	public WebDriverPool(String poolName, int initCount, int maxCount, int width, int height) throws Exception {
		super(poolName, maxCount);
		this.width = width; 
		this.height = height;
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
	}

	public WebDriverPool(String poolName, int initCount, int maxCount, int width, int height, String webdriverBin, String webdriverGecko) throws Exception {
		super(poolName, maxCount);
		this.width = width;
		this.height = height;
		this.webdriverBin = webdriverBin;
		this.webdriverGecko = webdriverGecko;
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
		
	}

	public void initPropertyOSX() {
		System.setProperty("webdriver.firefox.bin","/Applications/Firefox.app/Contents/MacOS/firefox-bin");
	    System.setProperty("webdriver.gecko.driver","/Users/1100177/utils/geckodriver");
	}
	public void setWebdriverFirefoxBin(String value) {
		System.setProperty("webdriver.firefox.bin", value);
	}
	public void setWebdriverGechoDriver(String value) {
		System.setProperty("webdriver.gecko.driver", value);
	}

	@Override
	protected WebDriver createConnection() throws Exception {
		if(webdriverBin==null)
			initPropertyOSX();
		else {
			setWebdriverFirefoxBin(webdriverBin);
			setWebdriverGechoDriver(webdriverGecko);
		}
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().setSize(new Dimension(width, height));
		
		return driver;
	}

	@Override
	public void closeConnections() throws Exception {
		for(WebDriver wd : pools)
			wd.close();
	}

	@Override
	protected boolean isClosed(WebDriver obj) {
		if(obj==null)
			return true;
		return false;
	}

}
