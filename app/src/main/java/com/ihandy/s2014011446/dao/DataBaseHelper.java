package com.ihandy.s2014011446.dao;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ihandy.s2014011446.bean.NewsContent;
import com.ihandy.s2014011446.bean.NewsItem;
import com.ihandy.s2014011446.bean.NewsType;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 *
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "News.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<NewsType,Integer> newsTypeDao = null;
    private Dao<NewsItem,Integer> newsItemDao = null;
    private Dao<NewsContent,Integer> newsContentDao = null;

    private RuntimeExceptionDao<NewsType,Integer> newsTypeRuntimeDao = null;
    private RuntimeExceptionDao<NewsItem,Integer> newsItemRuntimeDao = null;
    private RuntimeExceptionDao<NewsContent,Integer> newsContentRuntimeDao = null;


    private static DataBaseHelper helper = null;

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 获取helper对象
     * @param context
     * @return helper对象
     */
    public static DataBaseHelper getHelper(Context context){
        if (helper == null){
            helper = new DataBaseHelper(context);
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {

            TableUtils.createTable(connectionSource, NewsContent.class);
            TableUtils.createTable(connectionSource, NewsItem.class);

            newsItemDao = getNewsItemDao();
            newsContentDao = getNewsContentDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {

        try {
            TableUtils.dropTable(connectionSource, NewsItem.class, true);
            TableUtils.dropTable(connectionSource, NewsContent.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Dao<NewsItem,Integer> getNewsItemDao() throws SQLException {
        if (newsItemDao == null){
            newsItemDao = getDao(NewsItem.class);
        }
        return newsItemDao;
    }
    public Dao<NewsType,Integer> getNewsTypeDao() throws SQLException {
        if (newsTypeDao == null){
            newsTypeDao = getDao(NewsType.class);
        }
        return newsTypeDao;
    }

    public Dao<NewsContent, Integer> getNewsContentDao() throws SQLException {
        if (newsContentDao == null){
            newsContentDao = getDao(NewsContent.class);
        }
        return newsContentDao;
    }

    public RuntimeExceptionDao<NewsContent, Integer> getNewsContentRuntimeDao() {
        if (newsContentRuntimeDao == null){
            newsContentRuntimeDao = getRuntimeExceptionDao(NewsContent.class);
        }
        return newsContentRuntimeDao;
    }

    public RuntimeExceptionDao<NewsItem, Integer> getNewsItemRuntimeDao() {
        if (newsItemRuntimeDao == null){
            newsItemRuntimeDao = getRuntimeExceptionDao(NewsItem.class);
        }
        return newsItemRuntimeDao;
    }
    public RuntimeExceptionDao<NewsType, Integer> getNewsTypeRuntimeDao() {
        if (newsTypeRuntimeDao == null){
            newsTypeRuntimeDao = getRuntimeExceptionDao(NewsType.class);
        }
        return newsTypeRuntimeDao;

    }


}
