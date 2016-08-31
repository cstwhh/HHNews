package com.ihandy.s2014011446.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * 新闻实体类
 */
public class NewsItem {

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
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

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @DatabaseField
    private String category;
    class Imgs {
        String url;
    }
    private Imgs imgs;
    @DatabaseField
    private String imgsUrl;
    @DatabaseField(id = true)
    private String news_id;
    @DatabaseField
    private String origin;
    class Source {
        String url;
    }
    private Source source;
    @DatabaseField
    private String sourceUrl;
    @DatabaseField
    private String title;
    @DatabaseField
    private Date updateTime;
    @DatabaseField
    private int pageNumber;


    @Override
    public String toString() {
        return "NewsContent{" +
                    "category=" + category +
                    ", imgsUrl=" + imgs.url +
                    ", news_id=" + news_id +
                    ", origin=" + origin +
                    ", sourceUrl=" + source.url +
                    ", title=" + title +
                "}";
    }

}
