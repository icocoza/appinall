package com.ccz.appinall.library.module.scrap;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ccz.appinall.library.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlScrapper {

	public static HtmlNode doScrap(String url) {
		try {
			String title = "";
			Document doc = Jsoup.connect(url).get();	//thread-safe
			Elements metaOgTitle = doc.select("meta[property=og:title]");
			if(metaOgTitle!=null)
				title = metaOgTitle.attr("content");
			else
				title = doc.title();
			String imageUrl = "";
			Elements metaOgImage = doc.select("meta[property=og:image]");
			if(metaOgImage!=null)
				imageUrl = metaOgImage.attr("content");
			else {
				Elements imgElements = doc.select("body").select("img[src~=(?i)\\.(png|jpe?g)]");
				if(imgElements==null || imgElements.size()<1)
					return new HtmlNode();
				imageUrl = findImageBySize(imgElements, 70);
				if(imageUrl==null)
					imageUrl = findImageOne(imgElements);
				if(imageUrl==null)
					imageUrl = imgElements.first().absUrl("src");
			}
			
			String body="";
			Elements metaOgDesc = doc.select("meta[property=og:description]");
			if(metaOgDesc!=null)
				body = metaOgDesc.attr("content");
			else {
				Elements bodyElement = doc.select("body").select("div p");
				if(bodyElement.size()>0)
					body = bodyElement.first().text();
				else
					body = doc.body().text();
			}
			return new HtmlNode(url, title, body, imageUrl);
		}catch(IOException e) {
			log.error(e.getMessage());
		}
		return new HtmlNode();
	}
	
	private static String findImageBySize(Elements elements, int size) {
		for(Element element : elements) {
			if(element.hasAttr("src") == true) {
				if( getWidth(element)>size && getHeight(element)>size ) {
					String name = getElementImageName(element);
					if(name!=null)
						return name;
				}
			}
		}
		return null;
	}
	
	private static String findImageOne(Elements elements) {
		for(Element element : elements) {
			if(element.hasAttr("src") == true) {
				String name = getElementImageName(element);
				if(name!=null)
					return name;
			}
		}
		return null;
	}
	
	private static String getElementImageName(Element element) {
		String name = element.absUrl("src");
		if(name == null || name.contains("logo") || name.contains("snb_"))
			return null;
		String className = element.attr("class");
		if(className !=null && (className.contains("logo") || className.contains("snb_")))
			return null;
		return name;
	}
	
	private static int getWidth(Element element) {
		if(element.hasAttr("width") && StrUtil.isNumeric(element.attr("width"))==true)
			return Integer.parseInt(element.attr("width"));
		return 0;
	}
	private static int getHeight(Element element) {
		if(element.hasAttr("height") && StrUtil.isNumeric(element.attr("height"))==true)
			return Integer.parseInt(element.attr("height"));
		return 0;
	}
	
}
