package com.yj.crawler.main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.yj.crawler.config.RabbitMQConnectionPool;
import com.yj.crawler.link.LinkFilter;
import com.yj.crawler.link.Links;
import com.yj.crawler.page.Page;
import com.yj.crawler.page.PageParserTool;
import com.yj.crawler.page.RequestAndResponseTool;
import com.yj.crawler.utils.FileTool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * 连接爬取，生产目标文件
 * @author wuyang
 * @date 2018/7/31 15:04
 */
@Component
public class MedicalInsuranceDrugCrawler extends Thread implements ApplicationContextAware {

    LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(200);

    @Autowired
    private RabbitMQConnectionPool rabbitMQConnectionPool;

    private static final String TASK_QUEUE_NAME = "MedicalInsuranceDrug";

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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("-------任务开始处理------");
        Thread produce = new Produce();
        Thread consume = new Consume();
        produce.start();
        consume.start();
    }

    /**
     * 生产数据
     */
    @Component
    class Produce extends Thread{
        @Override
        public void run() {
            /**
             * 循环条件：待抓取的连接不空且抓取的网页不多于1000 && Links.getVisitedUrlNum() <=1000
             * 当前条件为待访问序列不为空
             */
            int i = 1;
            while(i <= 144){
                //先从待访问的序列中取出第一个
                String visitUrl = "http://202.96.245.182/xxcx/yp.jsp?sPagenum="+i;
                if(filter.accept(visitUrl)) {
                    //根据URL得到page
                    Page page = RequestAndResponseTool.sendRequestAndGetResponse(visitUrl);
                    //保存文件
                    try {
                        String filName = FileTool.saveToLocal(page);
                        linkedBlockingQueue.put(filName);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(i+"个文件生成完毕");
                i++;
            }
        }
    }

    /**
     * 消费已经爬取的数据
     */
    @Component
    class Consume extends Thread{

        @Override
        public void run(){
            Channel channel = null;
            try {
                channel = rabbitMQConnectionPool.getChannel();
                channel.queueDeclare(TASK_QUEUE_NAME,true,false,false,null);
                while (true){
                    String filePath = (String) linkedBlockingQueue.take();
                    String content = FileTool.readFile(filePath);
                    Document document = Jsoup.parse(content);
                    String json = PageParserTool.getMedicalInsuranceDrug(document);
                    channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,json.getBytes());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}