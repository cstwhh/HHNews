package com.ihandy.s2014011446.biz;

import android.content.Context;
import android.util.Log;

import com.ihandy.s2014011446.bean.NewsContent;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.common.NewsTypes;
import com.ihandy.s2014011446.dao.NewsContentDao;
import com.ihandy.s2014011446.dao.NewsItemDao;
import com.ihandy.s2014011446.utils.HttpUtils;
import com.ihandy.s2014011446.utils.StringUtils;
import com.ihandy.s2014011446.utils.NewsAPIUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private NewsContentDao mNewsContentDao;

    public NewsItemBiz(Context context) {
        mContext = context;
        mNewsItemDao = new NewsItemDao(context);
        mNewsContentDao = new NewsContentDao(context);
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
    public List<NewsItem> getNewsItemCache(int newsType,int currentPage,boolean isNeedRefresh) throws SQLException {
        //如果缓存为空或需要刷新缓存时重新从数据库提取数据
        if (mNewsItemCache == null || isNeedRefresh){
            mNewsItemCache = mNewsItemDao.searchByPageAndType(currentPage,newsType);
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

//    /**
//     * 获取新闻内容的数据库缓存
//     * @param url  地址
//     * @return  新闻内容缓存
//     * @throws SQLException
//     */
//    public List<NewsContent> getNewsContentCache(String url) throws SQLException {
//        //如果缓存为空或需要刷新缓存时重新从数据库提取数据
//
//        return mNewsContentCache;
//    }
//
//    /**
//     * 设置缓存
//     * @param mNewsContentCache
//     */
//    public void setNewsContentCache(List<NewsContent> mNewsContentCache) {
//        this.mNewsContentCache = mNewsContentCache;
//    }


    /**
     * 根据新闻类型和页码得到新闻列表
     * @param newsType      新闻类型
     * @param currentPage   页码
     * @param netAvailable  当前是否有网络
     * @return              新闻列表
     * @throws Exception
     */
    public List<NewsItem> getNewsItems(int newsType,int currentPage,boolean netAvailable) throws Exception {

        //当无网络时加载数据库中数据
        Log.i("ASDNET","netAvailable:"+netAvailable);

        if (!netAvailable ){
            return getNewsItemCache(newsType,currentPage,false);
        }
        //有网络时查看数据是否过期,未过期则返回缓存数据
        if (getNewsItemCache(newsType,currentPage,false) != null
                && this.isOutOfTime(getNewsItemCache(newsType,currentPage,false).get(0)) > 0){
            return getNewsItemCache(newsType,currentPage,true);
        }
        //若数据已过期，则重新获取

        String url = NewsAPIUtils.getNewsUrl(newsType, currentPage);
        String htmlStr = null;
        //如果服务器未返回数据,则返回数据库中的数据
        try {
            htmlStr = HttpUtils.doGet(url);
        }catch (Exception ex){
            return getNewsItemCache(newsType,currentPage,true);
        }
        List<NewsItem> newsItems = new ArrayList<NewsItem>();

        NewsItem newsItem;
        Document document = Jsoup.parse(htmlStr);
        Element itemTable = document.getElementsByClass(BASE_TABLE_CLASS).get(0);
        Elements items = itemTable.child(0).children();
        for (int i=0;i<items.size();i++){
            newsItem = new NewsItem();
            newsItem.setType(newsType);

            Element columnTable = items.get(i).getElementsByClass(COLUMN_TABLE_CLASS).get(0);
            Element link = columnTable.getElementsByTag("a").get(0);
            String contentUrl = link.attr("href");  //新闻内容链接
            newsItem.setUrl(NewsAPIUtils.NEWS_URL_MAIN + contentUrl);

            newsItem.setTitle(link.child(0).text());    //设置新闻标题

            //媒体聚焦页面有来源无时间
            if (newsType != NewsTypes.NEWS_TPYE_MTJJ){
                Element postTime = columnTable.getElementsByClass(POST_TIME_CLASS).get(0);
                newsItem.setDate(postTime.text());
            }else{
                Element source = columnTable.getElementsByClass(NEWS_SOURCE_CLASS).get(0);
                newsItem.setSource(source.text());
            }

            newsItem.setPageNumber(currentPage);
            //文章内容点击进入后再添加

            newsItem.setUpdateTime(new Date());
            newsItems.add(newsItem);

        }

        //将数据添加进数据库
        for(NewsItem item : newsItems) {
            mNewsItemDao.createOrUpdate(item);
        }
        //将数据添加进缓存
        setNewsItemCache(newsItems);

        return newsItems;

    }

    /**
     * 根据新闻的url获取新闻内容
     * @param url 新闻url
     * @return
     */
    public NewsContent getNewsContent(String url) throws Exception {

        NewsContent content = mNewsContentDao.searchByUrl(url);
        if (content != null){
            return content;
        }
        //获取html
        String htmlStr = HttpUtils.doGet(url);
//        Log.i("ASD","html"+htmlStr);
        NewsContent news = new NewsContent();

        Document document = Jsoup.parse(htmlStr);
//        Log.i("ASD","html"+htmlStr);
        //新闻url
        news.setUrl(url);

        //新闻标题
        Element titleElement = document.getElementsByClass(NEWS_TITLE_CLASS).get(0);
        Log.i("ASD","Title: "+titleElement.text());
        news.setTitle(titleElement.text());

        //包含新闻信息的p标签
        Element metaElement = document.getElementsByClass(NEWS_META_CLASS).get(0);
        Log.i("ASD","metaElement"+metaElement.text());
        //新闻时间
        news.setDate(StringUtils.getDateFromString(metaElement.text()));
        Log.i("ASDDATE","date:  "+StringUtils.getDateFromString(metaElement.text()));

        //新闻作者
        Element authorElement = document.getElementsByClass(NEWS_META_ITEM_CLASS).get(0);
        Log.i("ASD","authorElement"+authorElement.text());
        news.setAuthor(authorElement.text());

        //新闻来源
        Element sourceElement = document.getElementsByClass(NEWS_META_ITEM_CLASS).get(2);
        Log.i("ASD","sourceElement"+sourceElement.text());
        news.setSource(sourceElement.text());

        //新闻内容
        Element contentElement = document.getElementsByClass(NEWS_ARTICLE_CLASS).get(0);
        Elements contentItems = contentElement.children();
        //新闻内容都在p标签内，其中某些是图片
        for(Element contentItem : contentItems){

            Elements images = contentItem.getElementsByTag("img");
            //获取图片
            if (images.size() > 0){
                for (Element image : images){
//                    news.addImgUrl(image.attr("src"));
                }
                continue;
            }
            if(contentItem.text().trim().length()<=1){
                continue;
            }
            Log.i("ASD","contentText"+contentItem.text() + " length: " + contentItem.text().trim().length());
            news.addContent(contentItem.text());

        }

        //将数据添加进数据库
        mNewsContentDao.createOrUpdate(news);

        return news;
    }

    /**
     * 清除缓存数据库
     */
    public void clearCache(){
        mNewsContentDao.deleteAll();
        mNewsItemDao.deleteAll();
    }
}
