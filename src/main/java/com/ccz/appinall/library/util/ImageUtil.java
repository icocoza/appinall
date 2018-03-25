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
    
    static public ImageSize getImageSize(File src) throws IOException {
    		Image srcImg = null;
        String suffix = src.getName().substring(src.getName().lastIndexOf('.')+1).toLowerCase();
        if (suffix.equals("bmp") || suffix.equals("png") || suffix.equals("gif"))
            srcImg = ImageIO.read(src);
        else
            srcImg = new ImageIcon(src.toURI().toURL()).getImage();
        
        return new ImageUtil().new ImageSize(srcImg.getWidth(null), srcImg.getHeight(null));	//if not image, return -1
    }
    
    //below is,,, 출처: http://javacan.tistory.com/entry/124 [자바캔(Java Can Do IT)]
    static public boolean resize(File src, File dest, int width, int height) throws IOException {
        Image srcImg = null;
        String suffix = src.getName().substring(src.getName().lastIndexOf('.')+1).toLowerCase();
        if (suffix.equals("bmp") || suffix.equals("png") || suffix.equals("gif")) {
            srcImg = ImageIO.read(src);
        } else {
            srcImg = new ImageIcon(src.toURI().toURL()).getImage();
        }
        
        int srcWidth = srcImg.getWidth(null);
        int srcHeight = srcImg.getHeight(null);
        
        int destWidth = -1, destHeight = -1;
        
        if (width == SAME) {
            destWidth = srcWidth;
        } else if (width > 0) {
            destWidth = width;
        }
        
        if (height == SAME) {
            destHeight = srcHeight;
        } else if (height > 0) {
            destHeight = height;
        }
        
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
        
        return ImageIO.write(destImg, "jpg", dest);
    }
    
    public static BufferedImage resize(BufferedImage image, int width, int height) {
	    	float w = new Float(width) ;
	    	float h = new Float(height) ;
	
	    	if ( w <= 0 && h <= 0 ) {
	    		w = image.getWidth();
	    		h = image.getHeight();
	    	} else if ( w <= 0 ) {
	    		w = image.getWidth() * ( h / image.getHeight() ); 
	    	} else if ( h <= 0 ) {
	    		h = image.getHeight() * ( w / image.getWidth() ); 
	    	}
	
	    	int wi = (int) w;
	    	int he = (int) h;
	    	BufferedImage resizedImage = new BufferedImage(wi,he,BufferedImage.TYPE_INT_RGB);
	    	resizedImage.getGraphics().drawImage(image.getScaledInstance(wi,he,Image.SCALE_SMOOTH),0,0,wi,he,null);
	
	    	return resizedImage;
    	}
}
