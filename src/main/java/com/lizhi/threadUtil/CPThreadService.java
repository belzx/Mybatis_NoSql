package com.lizhi.threadUtil;


import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CPThreadService extends  AbstractThreadService<Reptile>{

    /**使用过的图片*/
    volatile Set<String> usedImages = new HashSet<>();

    /**爬过的网站*/
    volatile Set<String> usedHtml = new HashSet<>();

    /**需要爬虫的地址*/
    volatile  Set<String> htmls = new HashSet<>();
    {
        htmls.add("https://chinesepornmovie.net/videos/latest");
    }

    public static void main(String[] args) {
        System.out.println("开始启动");
        new CPThreadService().productToComsumers();

    }

    /**
     * 抓取页面 并获取页面信息
     */
    @Override
    public List<Reptile> product() {
        ArrayList arrayList = new ArrayList();

        try {
            ArrayList<String> strings = new ArrayList<>(htmls);
            String s = strings.get((int) (Math.random() * (strings.size() - 1)));
            String s2 = strings.get((int) (Math.random() * (strings.size() - 1)));
            HashSet<String> objects = new HashSet<>();
            objects.add(s);
            objects.add(s2);

            objects.forEach(d ->{
               arrayList.add(getAllImageAndHtml(d));
           });

            if(arrayList.size() == 0){
                arrayList.add("https://chinesepornmovie.net/videos/latest");
            }

        }catch (Exception e){

        }

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return arrayList;
    }


    /**
     * 取出的值
     * @param poll
     */
    @Override
    public void consumer(Reptile poll) {
        synchronized (htmls){
            //开始处理地址
            //用过的清除
            usedHtml.addAll(htmls);
            htmls.clear();
            htmls.addAll(poll.getHtmlAll());

            //生成新的
            poll.getHtmlAll().forEach(d -> {
                if(!usedHtml.contains(d)){
                    htmls.add(d);
                }
            });
        }
        final  List<String> imageAll = new ArrayList<>();
        synchronized (usedImages){
            //开始清除图片地址
            imageAll.addAll(poll.getImageAll());
            final Set<String> saveImages = poll.getImageAll();
            imageAll.forEach(d -> {
                if(usedImages.add(d)){
                    saveImages.add(d);
                }
            });
        }

        new Thread(()->{
            //保存图片
            imageAll.forEach(d ->{
                FileUtil.saveImage(d);
            });
        }).start();

    }

    public  Reptile getAllImageAndHtml(String link){
        ChinesPageProcessor pageOfYYY = new ChinesPageProcessor();
        Spider.create(pageOfYYY).addUrl(link)
                .thread(5) //开启五个线程抓取图片,还有地址
                .run();
        Reptile reptile = new Reptile();
        reptile.setHtmlAll(pageOfYYY.getHtmlAll());
        reptile.setImageAll(pageOfYYY.getImageAll());
        return reptile;
    }


}
