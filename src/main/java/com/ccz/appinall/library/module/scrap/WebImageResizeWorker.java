package com.ccz.appinall.library.module.scrap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.ccz.appinall.library.type.ImageWebFileInfo;
import com.ccz.appinall.library.util.ImageUtil;

public class WebImageResizeWorker {
    public final int THREADCOUNT = 10;
    
    Executor executor = null;
    int thumbWidth = 480, thumbHeight = 320;
    
    public interface WebToImageCallback {
    		void onCompleted(ImageWebFileInfo imgPath);
    		void onFailed(ImageWebFileInfo imgPath);
    }
    
    public WebImageResizeWorker() {
    		executor = Executors.newFixedThreadPool(THREADCOUNT);
    }
    
    public void setThumbnailSize(int thumbWidth, int thumbHeight) {
    		this.thumbWidth = thumbWidth;
    		this.thumbHeight = thumbHeight;
    }
    
    public void doConvert(final ImageWebFileInfo imgInfo, final PhantomJSDriverPool webDriverPool, final WebToImageCallback cb) {
    		
    		//Runnable runnable = () -> {
    			PhantomJSDriver driver = null;
			try {
				driver = webDriverPool.getConnection();
			    driver.get(imgInfo.url);
	 			final File outputFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES.FILE);
	 			//ImageUtil.resize(outputFile, imgInfo.outFile, thumbWidth, thumbHeight);
				//FileUtils.copyFile(outputFile, imgInfo.outFile);
			    byte[] imageInByte = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
				InputStream in = new ByteArrayInputStream(imageInByte);
				BufferedImage bufImg = ImageIO.read(in);
				float resizeWidth = (float)bufImg.getWidth();
				float resizeHeight = (float)bufImg.getHeight();
				Rectangle rect = new Rectangle(0,0,bufImg.getWidth(), (int)(resizeWidth*0.75f>resizeHeight?resizeHeight:resizeWidth*0.75));
				bufImg = cropImage(bufImg, rect);
				if(thumbWidth!=-1)
					bufImg = ImageUtil.resize(bufImg, thumbWidth, thumbHeight);
				imgInfo.setSize(rect.width, rect.height);
				imgInfo.setTitle(driver.getTitle());
				ImageIO.write(bufImg, "jpg", imgInfo.outFile);
				cb.onCompleted(imgInfo);
				
			} catch (Exception e) {
				e.printStackTrace();
				cb.onFailed(imgInfo);
			} finally {
				if(driver!=null)
					webDriverPool.returnConnection(driver);
			}
    		//};
    		//executor.execute(runnable);
    }
    
    private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(0, 0, rect.width, rect.height);
        return dest; 
    }
}
