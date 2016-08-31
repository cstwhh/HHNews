package com.ihandy.s2014011446.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ihandy.s2014011446.common.NewsTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 相关API
 */
public class NewsAPIUtils {

    public static String getNewsUrl(String newsType,int maxNewsID){
        //TODO 为了兼容所有带currentPage的函数调用,这里先进行重定位
//        return "http://assignment.crazz.cn/news/query?locale=en&category=" + newsType + "&max_news_id=" + maxNewsID;
        return getNewsUrl(newsType);
    }
    public static String getNewsUrl(String newsType){
        return "http://assignment.crazz.cn/news/query?locale=en&category=" + newsType;
    }

}
