package com.lizhi.threadUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChinesPageProcessor implements PageProcessor {

    public  final Set<String> imageAll = new HashSet<>();//保存要抓的图片
    public  final Set<String> htmlAll = new HashSet<>();//保存需要抓图的地址

    // @Override
    public Site getSite() {
        return Site.me().setCharset("utf8").setRetryTimes(1000).setSleepTime(1000);
    }

    @Override
    public void process(Page page) {
        if (page.getStatusCode() != 200) {
            System.out.println("获取页面失败");
            return;
        }

        List<String> all = page.getHtml().regex("http://.{0,300}\\.jpg").all();
        imageAll.addAll(all);

        List<String> aa = page.getHtml().regex("href=\"https://chinesepornmovie\\.net/watch.{0,300}\\.html").all();
        aa.stream().forEach(d -> {
            htmlAll.add(d.substring(6));
        });
    }

    public Set<String> getImageAll() {
        return imageAll;
    }

    public Set<String> getHtmlAll() {
        return htmlAll;
    }
}
