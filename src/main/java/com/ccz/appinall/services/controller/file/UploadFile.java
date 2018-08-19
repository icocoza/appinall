package com.ccz.appinall.services.controller.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker;
import com.ccz.appinall.library.module.scrap.ImageResizeWorker.ImageResizerCallback;
import com.ccz.appinall.library.type.ImageFileInfo;
import com.ccz.appinall.library.util.ImageUtil;
import com.ccz.appinall.library.util.ImageUtil.ImageSize;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.model.db.RecFile;

public class UploadFile {
	final RecFile file;
	
	public long downsize = 0;
	public FileOutputStream fos;
	
	private String scode;
	private String uploadPath, filepath;
	
	static final float THUMB_SIZE=480;
	public static final String THUMB_PATH = "thumb";
	public static final String CROP_PATH = "crop";
	
	static int seq=0;
	
	public UploadFile(RecFile file) {
		this.file = file;
	}
	
	public boolean open(String scode, String serverIp, String uploadPath) {
		try {
			String dir = uploadPath +"/"+ scode;
			filepath = dir +"/"+ file.fileid;
			this.uploadPath = uploadPath; 
			new File(dir).mkdirs();
			fos = new FileOutputStream(filepath);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public void write(byte[] buf, int size) throws IOException {
		fos.write(buf, 0, size);
		downsize += size;
	}
	public void discard() throws IOException {
		fos.close();
		new File(filepath).delete();
	}
	
	public boolean commit(String scode, ImageResizeWorker imageResizeWorker) throws IOException {
		this.scode = scode;
		fos.close();
		if(StrUtil.isImageFile(file.filename) == true) {
			ImageSize imageSize = ImageUtil.getImageSize(new File(filepath));
			file.width = imageSize.width;
			file.height = imageSize.height;
			makeThumb(imageResizeWorker);
		}
		return DbAppManager.getInst().updateFileInfo(scode, file.fileid, file.width, file.height, downsize);
	}
	
	public RecFile getFile() {	return file;		}
	
	public boolean isOverSize() { return file.filesize <= downsize; }
	
	
	
	public void makeThumb(ImageResizeWorker imageResizeWorker) {
		float rate = file.width > file.height ? THUMB_SIZE / (float)file.width : THUMB_SIZE / (float)file.height;
		String thumbName = newThumbFilename();
		String dir = getThumbPath(scode, uploadPath);
		
		(new File(dir)).mkdirs();
		int thumbWidth = (int)(file.width * rate);
		int thumbHeight = (int)(file.height * rate);
		imageResizeWorker.doResize(filepath, dir + thumbName, thumbWidth, thumbHeight, new ImageResizerCallback() {
			@Override
			public void onCompleted(Object dest) {
				System.out.println(dest);
			}

			@Override
			public void onFailed(Object src) {
				System.out.println(src);
			}
		});
		DbAppManager.getInst().updateThumbnail(scode, file.fileid, thumbName, thumbWidth, thumbHeight);
	}
	
	public void makeCrop(String src, String dest) {
		
	}
	
	public static int getSeq() {	return ++seq % 1000; }
	
	public static String newThumbFilename() {
		 return String.format("%s%d_%03d", THUMB_PATH, System.currentTimeMillis(), getSeq());
	}
	public static String getThumbPath(String scode, String uploadPath) {
		return uploadPath +"/"+ scode +"/" + THUMB_PATH +"/";
	}
	
	public static String newCropFilename() {
		 return String.format("%s%d_%03d", CROP_PATH, System.currentTimeMillis(), getSeq());
	}
	public static String newCropFilename(String name) {
		 return String.format("%s%s", CROP_PATH, name);
	}

	public static String getCropPath(String scode, String uploadPath) {
		return uploadPath +"/"+ scode +"/" + CROP_PATH +"/";
	}

}
