package com.ccz.appinall.library.module.scrap;

import java.io.File;

import com.ccz.appinall.library.module.scrap.ImageResizeWorker.ImageResizerCallback;
import com.ccz.appinall.library.type.ImageFileInfo;
import com.ccz.appinall.library.util.StrUtil;

public class LocalImageResizeMgr {
	
	public static LocalImageResizeMgr s_pThis;
	public static LocalImageResizeMgr getInst() {		return s_pThis = (s_pThis==null ? new LocalImageResizeMgr() : s_pThis);		}
	public static void freeInst() {		s_pThis = null;	}
	
	ImageResizeWorker  imgResizeWorker = new ImageResizeWorker();
	private String[] destImagePath;
	private float maxWidth=320f, maxHeight=480f;
	
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
	
	public void setMaxSize(float width, float height) {
		this.maxWidth = width;
		this.maxHeight = height;
	}
	
	int inc=0;
	public ImageFileInfo getStoragePath(String filename, long filesize, long timestamp) {
		String fname = StrUtil.getFileName(filename);
		String fext = StrUtil.getFileExt(filename);
		String origPath = String.format("%s/%s_%dX%d.%s", destImagePath[(++inc) % MAX_SUBFOLDER], fname, filesize, timestamp, fext);
		String thumbPath = String.format("%s/thumb/%s_%dX%d.%s", destImagePath[(inc) % MAX_SUBFOLDER], fname, filesize, timestamp, fext);
		return new ImageFileInfo(origPath, thumbPath);
	}
	
	public void makeThumb(ImageFileInfo imgPath, int destWidth, int destHeight, final ImageResizerCallback cb) {
		float rate = destWidth > destHeight ? (float)maxWidth/destWidth : (float)maxHeight/destHeight; 
		int resizeWidth = (int)((float)destWidth * rate);
		int resizeHeight = (int)((float)destHeight * rate);
		
		imgResizeWorker.doResize(imgPath, resizeWidth, resizeHeight, cb);
	}
	
}
