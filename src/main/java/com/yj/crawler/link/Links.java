package com.yj.crawler.link;

import com.yj.crawler.page.Page;

import java.util.*;

/**
 * 存储已经访问过的URL路径和待访问的URL路径
 */
public class Links {

    /** 已访问的url集合 已经访问过的主要考虑不能再重复了 使用set来保证不重复*/
    private static Set visitedUrlSet  = new HashSet();

    /** 待访问的url集合 待访问的主要考虑 1.规定访问顺序 2.保证不提供重复的待访问地址*/
    private  static LinkedList unVisitedUrlQueue = new LinkedList();

    /**获得已经访问的 URL 数目*/
    public static int getVisitedUrlNum() {
        return visitedUrlSet.size();
    }

    /**
     * 添加到访问过的 URL
     * @param url
     */
    public static void addVisitedUrlSet(String url) {
        visitedUrlSet.add(url);
    }

    /**
     * 移除访问过的 URL
     * @param url
     */
    public static void removeVisitedUrlSet(String url) {
        visitedUrlSet.remove(url);
    }


    /**
     * 获得 待访问的 url 集合
     * @return
     */
    public static LinkedList getUnVisitedUrlQueue() {
        return unVisitedUrlQueue;
    }

    /**
     * 添加到待访问的集合中  保证每个 URL 只被访问一次
     * @param url
     */
    public static void addUnvisitedUrlQueue(String url) {
        if (url != null && !url.trim().equals("")  && !visitedUrlSet.contains(url)  && !unVisitedUrlQueue.contains(url)){
            unVisitedUrlQueue.add(url);
        }
    }

    /**删除 待访问的url*/
    public static Object removeHeadOfUnVisitedUrlQueue() {
        synchronized (unVisitedUrlQueue){
            return unVisitedUrlQueue.removeFirst();
        }
    }

    /**判断未访问的 URL 队列中是否为空*/
    public static boolean unVisitedUrlQueueIsEmpty() {
        return unVisitedUrlQueue.isEmpty();
    }
}
