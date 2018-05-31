package com.yj.crawler.main;

import com.yj.crawler.link.LinkFilter;
import com.yj.crawler.link.Links;
import com.yj.crawler.page.Page;
import com.yj.crawler.page.PageParserTool;
import com.yj.crawler.page.RequestAndResponseTool;
import com.yj.crawler.utils.FileTool;
import org.jsoup.select.Elements;
import java.util.*;

/**
 * @date: 2018/5/30
 * @author: create by Right_ydd
 * @description: com.yj.crawler.main
 * 设计思路，两个线程抓取连接 两个线程消费连接
 */
public class MyCrawler implements Runnable{
    /**
     * 定义过滤器，提取以 xxx 开头的连接
     */
    LinkFilter filter = new LinkFilter() {
        @Override
        public boolean accept(String url) {
            if(url.startsWith("http://m.xiachufang.com/recipe/")){
                return true;
            }else{
                return false;
            }
        }
    };

    /**
     * 使用种子初始化URL队列
     * @param seeds 种子URL
     */
    private static void initCrawlerWithSeeds(String[] seeds){
        for(int i=0;i<seeds.length;i++){
            Links.addUnvisitedUrlQueue(seeds[i]);
        }
    }

    /**
     * 抓取过程
     * @param
     */
    @Override
    public void run(){

        /**
         * 循环条件：待抓取的连接不空且抓取的网页不多于1000 && Links.getVisitedUrlNum() <=1000
         * 当前条件为待访问序列不为空
         */
        while(!Links.unVisitedUrlQueueIsEmpty()){
            //先从待访问的序列中取出第一个
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if( visitUrl == null ){
                continue;
            }

            //根据URL得到page
            Page page = RequestAndResponseTool.sendRequestAndGetResponse(visitUrl);

            Elements elements = PageParserTool.select(page,"a");
            /*if(!elements.isEmpty()){
                System.out.println("下面将打印所有a标签：");
                System.out.println(elements);
            }*/
            if(filter.accept(visitUrl)) {
                Map<String,String> subMap = PageParserTool.getHtml(page,"aside[class=sub-title]","p[class=word-wrap]");
                Iterator iterator = subMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    System.out.println(entry.getKey());
                    System.out.println(entry.getValue());
                    iterator.remove();
                }
                //保存文件
                FileTool.saveToLocal(page);
                return ;
            }
            //将已经访问过的连接放入已访问的连接中
            Links.addVisitedUrlSet(visitUrl);

            //得到超链接
            Set<String> links = PageParserTool.getLinks(page,"a");
            for(String link : links){
                Links.addUnvisitedUrlQueue(link);
                //System.out.println("新增爬取路径: " + link);
            }
        }
    }

    //main 方法入口
    public static void main(String[] args) {
        // 初始化 URL 队列
        initCrawlerWithSeeds(new String[]{"http://m.xiachufang.com/"});

    }

}
