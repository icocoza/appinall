package com.ccz.appinall.library.module.scrap;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.ccz.appinall.library.util.ConnectionPool;

@Deprecated
public class PhantomJSDriverPool extends ConnectionPool<PhantomJSDriver> {

	private int width, height;
	private String phantomPath;
	
	public PhantomJSDriverPool(String poolName, int initCount, int maxCount, int width, int height) throws Exception {
		super(poolName, maxCount);
		this.width = width; 
		this.height = height;
		this.phantomPath = "/Users/1100177/utils/phantomjs";
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
	}

	public PhantomJSDriverPool(String poolName, int initCount, int maxCount, int width, int height, String phantomPath) throws Exception {
		super(poolName, maxCount);
		this.width = width;
		this.height = height;
		this.phantomPath = phantomPath;
		for(int i=0; i<initCount; i++)
			super.addPool(createConnection());
	}
	public void setPhantomJsPath(String value) {
		this.phantomPath = value;
	}
	@Override
	protected PhantomJSDriver createConnection() throws Exception {
		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(false);
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomPath);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {"--disk-cache=false","--debug=false", "--web-security=no", "--ignore-ssl-errors=yes"});
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX+"javascriptEnabled", true);
		((DesiredCapabilities) caps).setCapability("Platform", Platform.ANY);
		PhantomJSDriver driver = new PhantomJSDriver(caps);
		driver.manage().timeouts().setScriptTimeout(200, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(width, height));
		return driver;
	}
	
	@Override
	public void closeConnections() throws Exception {
		for(PhantomJSDriver pjs : pools)
			pjs.quit();
		pools.clear();
	}


	@Override
	protected boolean isClosed(PhantomJSDriver obj) {
		if(obj==null)
			return true;
		return false;
	}

}
