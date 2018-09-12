package com.yj.crawler.ip;

import com.yj.crawler.link.LinkFilter;
import com.yj.crawler.link.Links;
import com.yj.crawler.page.Page;
import com.yj.crawler.page.PageParserTool;
import com.yj.crawler.page.RequestAndResponseTool;
import com.yj.crawler.utils.FileTool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wuyang
 * @date 2018/7/31 18:09
 */
public class IpProduce extends Thread {

    // 待校验的IP
    public static LinkedBlockingQueue<Ip> ipOriginal = new LinkedBlockingQueue<Ip>(500);

    // 有效IP
    public static LinkedBlockingQueue<Ip> ipEffective = new LinkedBlockingQueue<Ip>(100);
    /**
     * 定义过滤器，提取以 xxx 开头的连接
     */
    LinkFilter filter = new LinkFilter() {
        @Override
        public boolean accept(String url) {
            if(url.startsWith("http://www.xicidaili.com/nn/")){
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

    @Override
    public void run() {
        /**
         * 循环条件：待抓取的连接不空且抓取的网页不多于1000 && Links.getVisitedUrlNum() <=1000
         * 当前条件为待访问序列不为空
         */
        int i = 0;
        while(true){
            String indexPage = String.valueOf(i++);
            String url = "http://www.xicidaili.com/nn/"+indexPage;
            initCrawlerWithSeeds(new String[]{url});
            //先从待访问的序列中取出第一个
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if( visitUrl == null ){
                continue;
            }

            if(filter.accept(visitUrl)) {
                //根据URL得到page
                Page page = RequestAndResponseTool.sendRequestAndGetResponse(visitUrl);
                //保存文件
                try {
                    FileTool.saveToLocal(page);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //将已经访问过的连接放入已访问的连接中
                Links.addVisitedUrlSet(visitUrl);
                try {
                    PageParserTool.getAttrs(page);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread ipProduce = new IpProduce();
        ipProduce.start();
    }
}
