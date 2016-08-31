package com.ihandy.s2014011446.bean;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by wuhanghang on 16-8-31.
 */

public class NewsType {
    @DatabaseField(id = true)
    private String urlType;
    @DatabaseField
    private String showType;
    @DatabaseField
    private boolean isView; //whether user like read this type
    @DatabaseField
    private boolean isExist;    //最新一次中是否存在
    @DatabaseField(canBeNull = false)
    private Date updateTime;//更新时间

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
}
