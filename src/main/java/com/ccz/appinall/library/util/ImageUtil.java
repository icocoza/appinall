package com.ccz.appinall.library.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageUtil {
    public static final int RATIO = 0;
    public static final int SAME = -1;

    public class ImageSize {
    		public int width,  height;
    		
    		public ImageSize(int width, int height) {
    			this.width = width;
    			this.height = height;
    		}
    		public boolean isImage() {
    			return width>0 && height>0;
    		}
    }
    
    static public ImageSize getImageSize(String srcPath) throws IOException {
    	return getImageSize(new File(srcPath));
    }

    static public ImageSize getImageSize(File src) throws IOException {
    	Image srcImg = getImage(src);
        return new ImageUtil().new ImageSize(srcImg.getWidth(null), srcImg.getHeight(null));	//if not image, return -1
    }
    
    //below is,,, 출처: http://javacan.tistory.com/entry/124 [자바캔(Java Can Do IT)]
    public static boolean resize(File src, File dest, int width, int height) throws IOException {
    	BufferedImage destImg = resize(src, width, height);
        return ImageIO.write(destImg, "jpg", dest);
    }
    
    public static boolean crop(File src, File dest, int width, int height) throws IOException {
    	BufferedImage resizeImg = resize(src, width, height);
    	int cropSize = width > height ? height : width;
    	cropSize = (int)((float)cropSize * 0.85f);
    	
    	int x = (width - cropSize) / 2;
    	int y = (height - cropSize) / 2;
    	
    	BufferedImage cropImg = resizeImg.getSubimage(x, y, cropSize, cropSize);
        return ImageIO.write(cropImg, "jpg", dest);
    }

    public static BufferedImage resize(File src, int width, int height) throws IOException {
        Image srcImg = getImage(src);
        
        int srcWidth = srcImg.getWidth(null);
        int srcHeight = srcImg.getHeight(null);
        
        int destWidth = -1, destHeight = -1;
        
        destWidth =  (width == SAME) ? srcWidth : width;
        destHeight = (height == SAME) ? srcHeight : height;
        
        if (width == RATIO && height == RATIO) {
            destWidth = srcWidth;
            destHeight = srcHeight;
        } else if (width == RATIO) {
            double ratio = ((double)destHeight) / ((double)srcHeight);
            destWidth = (int)((double)srcWidth * ratio);
        } else if (height == RATIO) {
            double ratio = ((double)destWidth) / ((double)srcWidth);
            destHeight = (int)((double)srcHeight * ratio);
        }
        
        Image imgTarget = srcImg.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH); 
        int pixels[] = new int[destWidth * destHeight]; 
        PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth); 
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        } 
        BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB); 
        destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth); 
        return destImg;
    }
    
    public static Image getImage(String srcPath) throws IOException {
    	return getImage(new File(srcPath));
    }
    
    public static Image getImage(File src) throws IOException {
    	String suffix = src.getName().substring(src.getName().lastIndexOf('.')+1).toLowerCase();
        if (suffix.equals("bmp") || suffix.equals("png") || suffix.equals("gif"))
            return ImageIO.read(src);
        return new ImageIcon(src.toURI().toURL()).getImage();
    }
}
