package com.ccz.appinall.library.module.scrap;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.ccz.appinall.library.type.ImageFileInfo;
import com.ccz.appinall.library.util.ImageUtil;

public class ImageResizeWorker {
    public final int THREADCOUNT = 10;
    
    Executor executor = null;
    ImageResizerCallback imgCallback;

    public interface ImageResizerCallback {
    		void onCompleted(ImageFileInfo imgPath);
    		void onFailed(ImageFileInfo imgPath);
    }
    
    public ImageResizeWorker() {
    		executor = Executors.newFixedThreadPool(THREADCOUNT);
    }
    
    public void doResize(final ImageFileInfo imgPath, final int width, final int height, final ImageResizerCallback cb) {
    		Runnable runnable = () -> {
    			try {
					if(ImageUtil.resize(new File(imgPath.origPath), new File(imgPath.thumbPath), width, height)==true) {
						imgPath.setThumbSize(width, height);
						cb.onCompleted(imgPath);
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
    			cb.onFailed(imgPath);
    		};
    		executor.execute(runnable);
    }
    

}
