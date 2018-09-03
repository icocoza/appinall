package com.ccz.appinall.library.module.scrap;

import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.library.util.SqlUtils;

import lombok.Getter;

public class HtmlNode {
	@Getter private String scrapid;
    @Getter private String mainTitle = "", subTitle = "";
    @Getter private final String url;
    @Getter private final String imageUrl;
    @Getter private String shortBody = "";
    
    private final int BODY_LIMIT = 100;
    
    private HtmlNode thisObj = null;
    private static int sec = 1;
    
    public HtmlNode() {
    	url = imageUrl = shortBody = null;
    }
    
    public HtmlNode(String url, String title, String body, String imageUrl) {
    	this.scrapid = KeyGen.makeKey("web", sec++); 
        this.url = url;
        this.imageUrl = imageUrl.split("\\?")[0];
        
        if(title.contains("-") == true)
        	splitTitle(title, "-");
        else if(title.contains("|") == true)
        	splitTitle(title, "\\|");
        else if(title.contains(":") == true)
        	splitTitle(title, ":");
        else if(title != null)
        	this.mainTitle = title;
        if(body.length()>0)
        	this.shortBody = body.length()<=100 ? body : body.substring(0, BODY_LIMIT);
        this.thisObj = this;
        
        mainTitle = SqlUtils.escape(mainTitle);
        subTitle = SqlUtils.escape(subTitle);
        shortBody = SqlUtils.escape(shortBody);
    }
    
    public boolean isEmpty() {
    	return thisObj==null;
    }
    
    private void splitTitle(String title, String div) {
    	String[] split = title.split(div, -1);
    	mainTitle = split[0].trim();
    	subTitle = split[1].trim();
    }
}
