package com.yj.crawler.ip;


import java.util.concurrent.*;

/**
 * @author wuyang
 * @date 2018/7/31 18:11
 */
public class IpFilter implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        return thread;
    }
}
