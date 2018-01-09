package com.ccz.appinall.library.type;

import java.io.File;
import java.io.IOException;

import com.ccz.appinall.library.util.HashUtil;
import com.ccz.appinall.library.util.ImageUtil;
import com.ccz.appinall.library.util.ImageUtil.ImageSize;

public class ImageFileInfo {
	final public String imgId;	//sha256-by original path
	final public String origPath, thumbPath;
	
	public long width, height;
	public long thumbWidth, thumbHeight;
	
	public ImageFileInfo(String origPath, String thumbPath) {
		this.origPath = origPath;
		this.thumbPath = thumbPath;
		
		File f = new File(thumbPath);
		f = new File(f.getParent());
		f.mkdirs();
		
		this.imgId = HashUtil.getSha256Base62(origPath);
		try {
			ImageSize is = ImageUtil.getImageSize(new File(origPath));
			this.width = is.width;
			this.height = is.height;
		} catch (IOException e) {
			e.printStackTrace();
			this.width = this.height = -1;
		}
	}
	
	public boolean isImage() {
		return width>0 && height>0;
	}
	
	public void setThumbSize(int thumbWidth, int thumbHeight) {
		this.thumbWidth = thumbWidth;
		this.thumbHeight = thumbHeight;
	}
}
