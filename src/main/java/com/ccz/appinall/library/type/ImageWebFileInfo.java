package com.ccz.appinall.library.type;

import java.io.File;

import com.ccz.appinall.library.util.HashUtil;

public class ImageWebFileInfo {
	final public String imgId;	//sha256-by original path
	final public String url;
	final public File outFile;
	final public String ext;
	
	public int width, height;
	public String title;
	
	public ImageWebFileInfo(String url, String destPath, String ext) {
		this.url = url;
		this.imgId = HashUtil.getSha256Base62(url);
		this.outFile = new File(String.format("%s/%s.%s", destPath, imgId, ext));
		this.ext = ext;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public void setTitle(String title) { this.title = title; }
}
