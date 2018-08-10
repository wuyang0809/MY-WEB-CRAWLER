package com.yj.crawler.main;

import com.yj.crawler.link.LinkFilter;
import com.yj.crawler.link.Links;
import com.yj.crawler.page.Page;
import com.yj.crawler.page.PageParserTool;
import com.yj.crawler.page.RequestAndResponseTool;
import com.yj.crawler.utils.FileTool;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;

/**
 * 连接爬取，生产目标文件
 * @author wuyang
 * @date 2018/7/31 15:04
 */
public class CrawlerProduce extends Thread {

    /**
     * 定义过滤器，提取以 xxx 开头的连接
     */
    LinkFilter filter = new LinkFilter() {
        @Override
        public boolean accept(String url) {
            if(url.startsWith("http://202.96.245.182/xxcx/yp.jsp")){
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
        int i = 1;
        while(i == 1){
            initCrawlerWithSeeds(new String[]{"http://202.96.245.182/xxcx/yp.jsp?sPagenum="+i});
            //先从待访问的序列中取出第一个
            String visitUrl = (String) Links.removeHeadOfUnVisitedUrlQueue();
            if( visitUrl == null ){
                continue;
            }

            if(filter.accept(visitUrl)) {
                //根据URL得到page
                Page page = RequestAndResponseTool.sendRequestAndGetResponse(visitUrl);
                Elements elements = PageParserTool.select(page,"tr[bgcolor='#FFFFFF']");
                System.out.println(elements.select("td").eq(0).first().text());
                //保存文件
                try {
                    FileTool.saveToLocal(page);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //将已经访问过的连接放入已访问的连接中
                Links.addVisitedUrlSet(visitUrl);
            }
            i++;
        }
    }

    public static void main(String[] args) {
        Thread thread = new CrawlerProduce();
        thread.start();

    }
}
