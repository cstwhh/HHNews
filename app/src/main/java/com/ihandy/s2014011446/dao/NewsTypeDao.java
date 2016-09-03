package com.ihandy.s2014011446.dao;

import android.content.Context;

import com.ihandy.s2014011446.bean.NewsType;
import com.ihandy.s2014011446.bean.NewsItem;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * 对新闻列表的数据库操作
 */
public class NewsTypeDao {

    private RuntimeExceptionDao<NewsType,Integer> mNewsTypeDao;
    private DataBaseHelper mDataBaseHelpter;

    public NewsTypeDao(Context context) {
        mDataBaseHelpter = DataBaseHelper.getHelper(context);
        this.mNewsTypeDao = mDataBaseHelpter.getNewsTypeRuntimeDao();
    }

    /**
     * 更新或添加
     * @param newsType 需要更新的新闻列表项
     */
    public void createOrUpdate(NewsType newsType){
        mNewsTypeDao.createOrUpdate(newsType);
    }


    public List<NewsType> queryAll(){
        List<NewsType> news = mNewsTypeDao.queryForAll();
        return news;
    }

    public NewsType searchByUrlType(String urlType) throws SQLException {

        List<NewsType> newsTypes = mNewsTypeDao.queryBuilder().where().eq("urlType",urlType).query();
        if (newsTypes.size() > 0){
            return newsTypes.get(0);
        }
        return null;

    }


}
