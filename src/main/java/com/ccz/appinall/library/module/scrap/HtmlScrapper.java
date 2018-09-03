package com.ccz.appinall.library.module.scrap;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HtmlScrapper {

	public static HtmlNode doScrap(String url) {
		try {
			
			Document doc = Jsoup.connect(url).get();	//thread-safe
			
			Elements elements = doc.select("body").select("img[src~=(?i)\\.(png|jpe?g)]");
			if(elements==null || elements.size()<1)
				return new HtmlNode();
			String image = findImageBySize(elements, 200);
			if(image==null)
				image = findImageOne(elements);
			if(image==null)
				image = elements.first().absUrl("src");
			
			elements = doc.select("body").select("div p");
			String body="";
			if(elements.size()>0)
				body = elements.first().text();
			else
				body = doc.body().text();
			return new HtmlNode(url, doc.title(), body, image);
		}catch(IOException e) {
			log.error(e.getMessage());
		}
		return new HtmlNode();
	}
	
	private static String findImageBySize(Elements elements, int size) {
		for(Element element : elements) {
			if(element.hasAttr("src") == true) {
				if( getWidth(element)>size && getHeight(element)>size ) {
					String name = element.absUrl("src");
					if(name.contains("logo") || name.contains("snb_"))
						continue;
					return name;
				}
			}
		}
		return null;
	}
	
	private static String findImageOne(Elements elements) {
		for(Element element : elements) {
			if(element.hasAttr("src") == true) {
				String name = element.absUrl("src");
				if(name.contains("logo") || name.contains("snb_"))
					continue;
				return name;
			}
		}
		return null;
	}
	
	private static int getWidth(Element element) {
		if(element.hasAttr("width"))
			return Integer.parseInt(element.attr("width"));
		return 0;
	}
	private static int getHeight(Element element) {
		if(element.hasAttr("height"))
			return Integer.parseInt(element.attr("height"));
		return 0;
	}
	
}
