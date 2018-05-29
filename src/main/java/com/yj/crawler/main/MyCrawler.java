package com.yj.crawler.main;

import com.yj.crawler.link.LinkFilter;
import com.yj.crawler.link.Links;
import com.yj.crawler.page.Page;
import com.yj.crawler.page.PageParserTool;
import com.yj.crawler.page.RequestAndResponseTool;
import com.yj.crawler.utils.FileTool;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;

/**
 * @date: 2018/5/30
 * @author: create by Right_ydd
 * @description: com.yj.crawler.main
 */
public class MyCrawler {

    /**
     * 使用种子初始化URL队列
     * @param seeds 种子URL
     */
    private void initCrawlerWithSeeds(String[] seeds){
        for(int i=0;i<seeds.length;i++){
            Links.addUnvisitedUrlQueue(seeds[i]);
        }
    }

    /**
     * 抓取过程
     * @param seeds
     */
    public void crawling(String[] seeds){
        // 初始化 URL 队列
        initCrawlerWithSeeds(seeds);

        // 定义过滤器，提取以http：//www.baidu.com开头的连接
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

        //循环条件：待抓取的连接不空且抓取的网页不多于1000
        while(!Links.unVisitedUrlQueueIsEmpty() & Links.getVisitedUrlNum() <=1000){
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
        MyCrawler crawler = new MyCrawler();
        crawler.crawling(new String[]{"http://m.xiachufang.com/"});
    }
}
