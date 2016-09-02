package com.ihandy.s2014011446.biz;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ihandy.s2014011446.R;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.common.NewsTypes;
import com.ihandy.s2014011446.dao.NewsItemDao;
import com.ihandy.s2014011446.ui.fragments.NewsListFragment;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.ihandy.s2014011446.utils.StringUtils;
import com.ihandy.s2014011446.utils.NewsAPIUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理新闻的业务逻辑类
 */
public class NewsItemBiz {

    private NewsItemDao mNewsItemDao;

    public NewsItemBiz(Context context) {
        mNewsItemDao = new NewsItemDao(context);
    }

    /**
     * 获取新闻项的数据库缓存
     * @param newsType  类型
     * @param currentPage   当前页
     * @return  新闻项列表缓存
     * @throws SQLException
     */
    public List<NewsItem> getNewsItemCache(String newsType,int currentPage) throws SQLException {
        return mNewsItemDao.getCache(newsType);
    }

    /**
     * 根据新闻类型和页码得到新闻列表
     * @param newsType      新闻URL类型
     * @param currentPage   页码
     * @return              新闻列表
     * @throws Exception
     */
    public List<NewsItem> getNewsItems(String newsType,int currentPage) throws Exception {

        String url = NewsAPIUtils.getNewsUrl(newsType, currentPage);

        String jsonStr = null;
        //如果服务器未返回数据,则返回数据库中的数据
        try {
            jsonStr = HttpUtils.doGet(url);
        }catch (Exception ex){
            return getNewsItemCache(newsType,currentPage);
        }
        Log.i("GETITEM", "getNewsItems: " + url);
        jsonStr = StringUtils.replaceBlankAndSpace(jsonStr);

        String PATTERN = "\"news\":(\\[.*\\])";
        Pattern p =Pattern.compile(PATTERN);
        Matcher m = p.matcher(jsonStr);
        String jsonList = null;
        if(m.find()) jsonList = m.group(1);
        else return null;
        Log.i(getClass().getName(), "jsonList: " + jsonList);

        jsonList = StringUtils.getRightJsonSyntax(jsonList);
        Log.i(getClass().getName(), "rightJsonList: " + jsonList);


        List<NewsItem> newsItems = new ArrayList<NewsItem>();
        try {
            newsItems = new Gson().fromJson(jsonList, new TypeToken<List<NewsItem>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        for(NewsItem newsItem : newsItems) {
            newsItem.setTitle("" + i);
            ++i;
//            Log.i("NewsItemBiz", "NewsItemsToString: " + newsItem.toString());
//            newsItem.setBitmap();
//            newsItem.generateImageBytesForSave();
            mNewsItemDao.createOrUpdate(newsItem);
        }


        return newsItems;

    }
    /**
     * 清除缓存数据库
     */
    public void clearCache(){
        mNewsItemDao.deleteAll();
    }
}
