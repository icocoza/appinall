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
	
	final int THUMB_SIZE=240;
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
	
	public void commit(ImageResizeWorker imageResizeWorker) throws IOException {
		fos.close();
		int width = 0, height = 0;
//		if(StrUtil.isImageFile(file.filename) == true) {
//			ImageSize imageSize = ImageUtil.getImageSize(new File(filepath));
//			file.width = imageSize.width;
//			file.height = imageSize.height;
//			makeThumb(imageResizeWorker);
//		}
		DbAppManager.getInst().updateFileInfo(scode, file.fileid, file.width, file.height, downsize, StrUtil.getHostIp());
	}
	
	public RecFile getFile() {	return file;		}
	
	public boolean isOverSize() { return file.filesize <= downsize; }
	
	public int getSeq() {	return ++seq % 1000; }
	
	public void makeThumb(ImageResizeWorker imageResizeWorker) {
		float rate = file.width > file.height ? THUMB_SIZE / file.width : THUMB_SIZE / file.height;
		String thumbname = String.format("thumb%d_%03d", System.currentTimeMillis(), seq);
		String dir = uploadPath +"/"+ scode +"/thumb/";
		int thumbwidth = (int)(file.width * rate);
		int thumbheight = (int)(file.height * rate);
		imageResizeWorker.doResize(filepath, dir + thumbname, thumbwidth, thumbheight, new ImageResizerCallback() {
			@Override
			public void onCompleted(ImageFileInfo imgPath) {
			}

			@Override
			public void onFailed(ImageFileInfo imgPath) {
			}
		});
		DbAppManager.getInst().updateThumbnail(scode, file.fileid, thumbname, thumbwidth, thumbheight);
	}
}
