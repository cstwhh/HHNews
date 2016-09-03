package com.ihandy.s2014011446.bean;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by wuhanghang on 16-9-3.
 */

public class FavoriteItem {
    @DatabaseField(id = true)
    private String newsId;
    @DatabaseField
    private String sourceUrl;
    @DatabaseField
    private String title;
    @DatabaseField
    private String origin;

    public String getNewsId() {
        return newsId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getOrigin() {
        return origin;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
