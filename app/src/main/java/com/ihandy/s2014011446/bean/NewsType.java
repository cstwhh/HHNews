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
    private int showOrder; //whether user like read this type
    @DatabaseField
    private boolean isExist;    //最新一次中是否存在

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


    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }


    public int getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(int showOrder) {
        this.showOrder = showOrder;
    }

}
