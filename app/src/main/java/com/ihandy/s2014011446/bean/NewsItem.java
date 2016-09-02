package com.ihandy.s2014011446.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 新闻实体类
 */
public class NewsItem {

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void obtainSourceUrl() {sourceUrl = source.url;}
    public String getSourceUrl() {return sourceUrl;}
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void obtainImgsUrl() {imgsUrl = imgs.url;}
    public String getImgsUrl() {
        return imgs.url;
    }
    public void setImgsUrl(String imgsUrl) {this.imgsUrl = imgsUrl;}

    public String getOrigin() { return origin; }


    @DatabaseField
    private String category;

    class Imgs {String url;}
    private Imgs imgs;
    @DatabaseField
    private String imgsUrl;

    @DatabaseField(id = true)
    private String news_id;

    @DatabaseField
    private String origin;

    class Source {String url;}
    private Source source;
    @DatabaseField
    private String sourceUrl;

    @DatabaseField
    private String title;

    @DatabaseField
    private int pageNumber;

    private Bitmap bitmap = null;
//
//    @Override
//    public String toString() {
//        return "NewsItem{" +
//                    "category=" + category +
//                    ", imgsUrl=" + imgs.url +
//                    ", news_id=" + news_id +
//                    ", origin=" + origin +
//                    ", sourceUrl=" + source.url +
//                    ", title=" + title +
//                "}";
//    }
    public void setBitmap() {
        if (bitmap == null)  {
            bitmap = generateBitMap(getImgsUrl());
        }
    }
    public Bitmap generateBitMap(String urlStr) {
        Bitmap bitmap = null;
        InputStream is = null;
        Log.i("NewsItem", "getBitMapUrl: " + urlStr);
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
    public Bitmap getBitmap() { return bitmap;}

}
