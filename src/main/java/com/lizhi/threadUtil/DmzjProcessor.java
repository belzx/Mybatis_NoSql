//package com.lizhi.threadUtil;
//
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.processor.PageProcessor;
//import us.codecraft.webmagic.selector.Html;
//
//import java.util.List;
//
//
//public class DmzjProcessor implements PageProcessor {
//
//	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
//	// 抓取网站的相关配置，可以包括编码、抓取间隔1s、重试次数等
//	private Site site = Site.me().setCharset("utf8").setRetryTimes(1000).setSleepTime(1000);
//
//	// @Override
//	public Site getSite() {
//		return site;
//	}
//
//	// @Override
//	public void process(Page page) {
//		Html html = page.getHtml();
//		System.out.println(html);
//
//		List<String> links = page.getHtml().regex("http://.{0,1000}\\.jpg").all();
////		page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
//		links.stream().forEach(d ->{
//			FileUtil.saveImage(d);
//		});
//
//	}
//
////	public static void main(String[] args) {
////		long startTime = System.currentTimeMillis();
////
////		Spider.create(new DmzjProcessor()).addUrl("https://chinesepornmovie.net/videos/latest")
////				.thread(5) //开启五个线程抓取图片
////				.run();
////		long endTime = System.currentTimeMillis();
////
////		System.out.println("花费时间" + ((endTime - startTime) / 1000) + "秒");
////	}
//
//}
