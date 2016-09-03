package com.ihandy.s2014011446.biz;

import android.content.Context;
import android.util.Log;

import com.ihandy.s2014011446.bean.NewsType;
import com.ihandy.s2014011446.common.NewsTypes;
import com.ihandy.s2014011446.dao.NewsTypeDao;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.ihandy.s2014011446.utils.NewsAPIUtils;
import com.ihandy.s2014011446.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by wuhanghang on 16-8-31.
 * 处理新闻类型
 */

public class NewsTypeBiz {
    private Context mContext;

    private NewsTypeDao mNewsTypeDao;

    public NewsTypeBiz(Context context) {
        mContext = context;
        mNewsTypeDao = new NewsTypeDao(context);
    }
    public List<NewsType> getNewsTypes(boolean netAvailable) throws Exception {

        //当无网络时加载数据库中数据
        //有网络时查看数据是否过期,未过期则返回缓存数据
        //若数据已过期，则重新获取

        String url = NewsAPIUtils.getTypeUrl();
        Log.i(getClass().getName(), "url: " + url);
        String jsonStr = null;
        //如果服务器未返回数据,则返回数据库中的数据
        try {
            jsonStr = HttpUtils.doGet(url);
        }catch (Exception ex){
           // return getNewsTypeCache(newsType,currentPage,true);
        }
        Log.i(getClass().getName(), "getNewsTypes: " + jsonStr);
        jsonStr = StringUtils.replaceBlank(jsonStr);

        String PATTERN = "\"categories\": \\{(.*?)\\},";
        Pattern p =Pattern.compile(PATTERN);
        Matcher m = p.matcher(jsonStr);
        String keyValueStr = null;
        if(m.find()) keyValueStr = m.group(1);
        else return null;

        Log.i(getClass().getName(), "keyValueStr: " + keyValueStr);
        PATTERN = "\"(.*?)\": \"(.*?)\",?";
        p =Pattern.compile(PATTERN);
        m = p.matcher(keyValueStr);
        mNewsTypeDao.setAllIsExist(false);
        List<NewsType> newsTypes = new ArrayList<NewsType>();
        while(m.find()) {
            Log.i("getNewsType", "getKeyValue   " + m.group(1) + ":" + m.group(2));
            NewsType newsType = new NewsType();
            newsType.setUrlType(m.group(1));
            newsType.setShowType(m.group(2));
            newsType.setExist(true);
            NewsType originNewsType = mNewsTypeDao.searchByUrlType(newsType.getUrlType());
            //TODO 数据库与find的不一致时需要测试
            if(originNewsType == null) { //原先数据库中不存在
                newsType.setShowOrder(0); //默认要收看
                newsTypes.add(newsType); //默认要显示
            }
            else { //原先数据库中存在
                //showOrder与原先保持一致
                int originShowOrder = originNewsType.getShowOrder();
                newsType.setShowOrder(originShowOrder);
                //是否显示
                if(originShowOrder >= 0)
                    newsTypes.add(newsType);
            }
            //要保存
            mNewsTypeDao.createOrUpdate(newsType);
        }
        Log.i("sort-test", "before: ");
        for(NewsType newsType: newsTypes)
            Log.i("sort-test", newsType.getShowType() + " " + newsType.getShowOrder());

        Collections.sort(newsTypes);

        Log.i("sort-test", "After: ");
        for(NewsType newsType: newsTypes)
            Log.i("sort-test", newsType.getShowType() + " " + newsType.getShowOrder());

        return newsTypes;

    }
}
