package com.lizhi.threadUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 爬虫对象
 */
public class Reptile {

    public   Set<String> imageAll = new HashSet<>();//保存要抓的图片

    public  Set<String> htmlAll = new HashSet<>();//保存需要抓图的地址

    public Set<String> getImageAll() {
        return imageAll;
    }

    public void setImageAll(Set<String> imageAll) {
        this.imageAll = imageAll;
    }

    public Set<String> getHtmlAll() {
        return htmlAll;
    }

    public void setHtmlAll(Set<String> htmlAll) {
        this.htmlAll = htmlAll;
    }
}
