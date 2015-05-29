package com.mining.ForumMining;

import java.util.ArrayList;
import java.util.List;

import com.crawl.web.util.URLFormatter;

public class Sample {
	
	public static void main(String args[]){
		URLFormatter format= new URLFormatter();
		List<String> expectedList=new ArrayList<String>();
		expectedList.add("http://www.google.com/201312.mbox/browser");
		expectedList.add("http://www.google.com/201311.mbox/browser");
		expectedList.add("http://www.google.com/201310.mbox/browser");
		
		List<String> urlList=new ArrayList<String>();
		urlList.add("[HtmlAnchor[<a href=\"201312.mbox/browser\" title=\"Dynamic browser\">]");
		urlList.add("[HtmlAnchor[<a href=\"201311.mbox/browser\" title=\"Dynamic browser\">]");
		urlList.add("[HtmlAnchor[<a href=\"201310.mbox/browser\" title=\"Dynamic browser\">]");
		
		String regex="2013.*mbox/browser";
		//List<String> actualList=urlFormat.getURLList("http://www.google.com/", urlList, regex);
		List<String> actualList=format.getURLList("http://www.google.com/", urlList, regex);
		
		System.out.println(actualList);
		System.out.println(expectedList);
	}
}
