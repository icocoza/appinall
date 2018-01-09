package com.ccz.appinall.library.module.scrap;

import java.io.File;

import com.ccz.appinall.library.module.scrap.WebImageResizeWorker.WebToImageCallback;
import com.ccz.appinall.library.type.ImageWebFileInfo;

public class WebImageResizeMgr {
	public static WebImageResizeMgr s_pThis;
	
	public static WebImageResizeMgr getInst() {
		if(s_pThis==null)
			s_pThis = new WebImageResizeMgr();
		return s_pThis;
	}
	
	public static void freeInst() {
		s_pThis = null;
	}
	
	PhantomJSDriverPool webDriverPool;
	WebImageResizeWorker  webImageWorker = new WebImageResizeWorker();
	private String[] destImagePath;
	private final int MAX_SUBFOLDER = 50;
	
	public void setPath(String[] destImgPath) {
		this.destImagePath = destImgPath;
	}
	
	public void setPath(String destImgPath) {
		this.destImagePath = new String[MAX_SUBFOLDER];
		for(int i=0; i<MAX_SUBFOLDER; i++) {
			this.destImagePath[i] = String.format("%s/%03d", destImgPath, i);
			new File(this.destImagePath[i]).mkdirs();
		}
	}

	public void init(int initCount, int maxCount, int width, int height) throws Exception {
		webDriverPool = new PhantomJSDriverPool("webdriver", initCount, maxCount, width, height);
	}

	public void close() {
		try {
			webDriverPool.closeConnections();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init(int initCount, int maxCount, int width, int height, String phantomPath) throws Exception {
		webDriverPool = new PhantomJSDriverPool("webdriver", initCount, maxCount, width, height, phantomPath);
	}

	int inc=0;
	public ImageWebFileInfo getStoragePath(String url, String ext) {
		return new ImageWebFileInfo(url, destImagePath[(++inc) % MAX_SUBFOLDER], ext);
	}

	public void makeImage(ImageWebFileInfo imgInfo, final WebToImageCallback cb) {
		webImageWorker.doConvert(imgInfo, webDriverPool, cb);
	}

}
