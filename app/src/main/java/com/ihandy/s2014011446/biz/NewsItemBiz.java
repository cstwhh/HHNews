package com.ihandy.s2014011446.biz;

import android.content.Context;
import android.util.Log;

import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.common.NewsTypes;
import com.ihandy.s2014011446.dao.NewsItemDao;
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

    //新闻列表页相关class
    private static final String BASE_TABLE_CLASS = "border1";  //包含新闻列表的table的class
    private static final String COLUMN_TABLE_CLASS = "columnStyle";    //包含新闻条目的table的class
    private static final String POST_TIME_CLASS = "posttime";        //包含新闻时间的class
    private static final String NEWS_SOURCE_CLASS = "derivation";        //包含新闻来源媒体的class

    //新闻内容页相关class

    private static final String NEWS_TITLE_CLASS = "biaoti";        //包含新闻标题td的class
    private static final String NEWS_META_CLASS = "postmeta";       //包含新闻相关信息的p标签的class
    private static final String NEWS_META_ITEM_CLASS = "meta_item";  //包含新闻相关信息条目的class
    private static final String NEWS_ARTICLE_CLASS = "article";  //包含新闻内容td的class

    private static final int OUTOFTIME_MINUTE = 30;             //新闻过期时间（分钟）

    private List<NewsItem> mNewsItemCache;                  //NewsItem缓存
//    private List<NewsItem> mNewsContentCache;               //NewsContent缓存

    private Context mContext;

    private NewsItemDao mNewsItemDao;

    public NewsItemBiz(Context context) {
        mContext = context;
        mNewsItemDao = new NewsItemDao(context);
    }

    /**
     * 查看对象是否过期
     * @param t
     * @param <T>
     * @return 如果未过期则返回大于0的数，如果过期则返回<0的数
     */
    public <T> int isOutOfTime(T t){


        if (t instanceof NewsItem ){
            return ((NewsItem) t).getUpdateTime().compareTo(getUnOutOfTimeDate());
        }

        return -1;
    }

    /**
     * 得到未过期的最迟时间（即修改时间小于此时间为过期）
     * @return 过期时间
     */
    public Date getUnOutOfTimeDate(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, - OUTOFTIME_MINUTE);
        return calendar.getTime();
    }

    /**
     * 获取新闻项的数据库缓存
     * @param newsType  类型
     * @param currentPage   当前页
     * @param isNeedRefresh 是否强制刷新
     * @return  新闻项列表缓存
     * @throws SQLException
     */
    public List<NewsItem> getNewsItemCache(String newsType,int currentPage,boolean isNeedRefresh) throws SQLException {
        //如果缓存为空或需要刷新缓存时重新从数据库提取数据
        if (mNewsItemCache == null || isNeedRefresh){
            // TODO
            // mNewsItemCache = mNewsItemDao.searchByPageAndType(currentPage,newsType);
        }
        return mNewsItemCache;
    }

    /**
     * 设置缓存
     * @param mNewsItemCache
     */
    public void setNewsItemCache(List<NewsItem> mNewsItemCache) {
        this.mNewsItemCache = mNewsItemCache;
    }

    /**
     * 根据新闻类型和页码得到新闻列表
     * @param newsType      新闻URL类型
     * @param currentPage   页码
     * @param netAvailable  当前是否有网络
     * @return              新闻列表
     * @throws Exception
     */
    public List<NewsItem> getNewsItems(String newsType,int currentPage,boolean netAvailable) throws Exception {

        //当无网络时加载数据库中数据
        Log.i("ASDNET","netAvailable:"+netAvailable);

        if (!netAvailable ) {
            return getNewsItemCache(newsType,currentPage,false);
        }
        //有网络时查看数据是否过期,未过期则返回缓存数据
        if (getNewsItemCache(newsType,currentPage,false) != null
                && this.isOutOfTime(getNewsItemCache(newsType,currentPage,false).get(0)) > 0){
            return getNewsItemCache(newsType,currentPage,true);
        }
        //若数据已过期，则重新获取

        String url = NewsAPIUtils.getNewsUrl(newsType, currentPage);

        String jsonStr = null;
        //如果服务器未返回数据,则返回数据库中的数据
        try {
            jsonStr = HttpUtils.doGet(url);
        }catch (Exception ex){
            return getNewsItemCache(newsType,currentPage,true);
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
        for(NewsItem newsItem : newsItems) {
            Log.i("NewsItemBiz", "NewsItemsToString: " + newsItem.toString());
            newsItem.setBitmap();
        }



//        NewsItem newsItem = new NewsItem();
//        newsItem.setPageNumber(currentPage);
//        //文章内容点击进入后再添加
//
//        newsItem.setUpdateTime(new Date());
//        newsItems.add(newsItem);

//        //将数据添加进数据库
//        for(NewsItem item : newsItems) {
//            mNewsItemDao.createOrUpdate(item);
//        }
//        //将数据添加进缓存
//        setNewsItemCache(newsItems);

        return newsItems;

    }
    /**
     * 清除缓存数据库
     */
    public void clearCache(){
        mNewsItemDao.deleteAll();
    }
}
