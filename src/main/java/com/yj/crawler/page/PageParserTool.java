package com.yj.crawler.page;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class PageParserTool {

    /**存储待解析的page*/
    private static Vector<Page> unAnalyzePage = new Vector<Page>();

    public static Page getunAnalyzePage(){
        synchronized (unAnalyzePage){
            return unAnalyzePage.remove(0);
        }
    }

    public static List<Page> getunAnalyzePages(){
        synchronized (unAnalyzePage){
            List<Page> pageList = new ArrayList<>(unAnalyzePage.subList(0,10));
            unAnalyzePage.subList(0,10).clear();
            return pageList;
        }
    }

    /**
     * 通过选择器来选取页面的元素
     * @param page
     * @param cssSelector
     * @return
     */
    public static Elements select(Page page, String cssSelector){
        return page.getDoc().select(cssSelector);
    }

    /**
     * 通过css选择器来得到指定元素
     * @return
     */
    public static Element select(Page page, String cssSlector, int index){
        Elements eles = select(page, cssSlector);
        int realIndex = index;
        if(index <0 ){
            realIndex = eles.size() + index;
        }
        return eles.get(realIndex);
    }

    /**
     * 获取满足选择器的元素中的连接 选择器cssSelector必须定位到具体的超链接
     * 例如我们想抽取id为content的div中的所有超链接，
     * 这里就要奖cssSelector定义为div[id=content] a
     * 放入set中，防止重复
     * @param page
     * @param cssSelector
     * @return
     */
    public static Set<String> getLinks(Page page, String cssSelector){
        Set<String> links = new HashSet<String>();
        Elements elements = select(page, cssSelector);
        Iterator iterator = elements.iterator();
        while(iterator.hasNext()){
            Element element = (Element) iterator.next();
            if( element.hasAttr("href")){
                links.add(element.attr("abs:href"));
            }else if( element.hasAttr("abs:src")){
                links.add(element.attr("abs:src"));
            }
        }
        return links;
    }

    /**
     * 获取页面中满足制定css选择器的所有元素的指定属性的集合
     * 例如通过getAttrs("img[src]","abs:src")可获取网页中所有图片的连接
     * @param page
     * @param cssSelector
     * @param attrName
     * @return
     */
    public static ArrayList<String> getAttrs(Page page, String cssSelector, String attrName){
        ArrayList<String> result = new ArrayList<String>();
        Elements elements = select(page, cssSelector);
        for(Element element : elements){
            if(element.hasAttr(attrName)){
                result.add(element.text());
            }
        }
        return result;
    }

    public static Map<String,String> getHtml(Page page, String cssSelector_1, String cssSelector_2){
        ArrayList<String> result = new ArrayList<String>();
        Elements elements = select(page, "ul[class=plain] li");
        Map<String,String> map = new LinkedHashMap<String,String>();
        for(Element element : elements){
            Element element_1 = element.select(cssSelector_1).first();
            Element element_2 = element.select(cssSelector_2).first();
            if(element_1 == null || element_2 == null){
                continue;
            }
            map.put(element_1.text(),element_2.text());
        }
        return map;
    }
}
