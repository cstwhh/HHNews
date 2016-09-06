package com.ihandy.a2014011446.utils;

/**
 * 相关API
 */
public class NewsAPIUtils {

    public static String getTypeUrl() {
        return "http://assignment.crazz.cn/news/en/category?timestamp=" + System.currentTimeMillis();
    }

    public static String getNewsUrl(String newsType,int maxNewsID){
        //TODO 为了兼容所有带currentPage的函数调用,这里先进行重定位
//        return "http://assignment.crazz.cn/news/query?locale=en&category=" + newsType + "&max_news_id=" + maxNewsID;
        return getNewsUrl(newsType);
    }
    public static String getNewsUrl(String newsType){
        return "http://assignment.crazz.cn/news/query?locale=en&category=" + newsType;
    }

}
