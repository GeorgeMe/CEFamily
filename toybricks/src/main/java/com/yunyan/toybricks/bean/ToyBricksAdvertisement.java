package com.yunyan.toybricks.bean;

/**
 * Created by George on 2015/3/27.
 */
public class ToyBricksAdvertisement {

    public String Title;
    public String URL;
    public String Wangzhi;

    public ToyBricksAdvertisement(String title, String URL, String wangzhi) {
        Title = title;
        this.URL = URL;
        Wangzhi = wangzhi;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getWangzhi() {
        return Wangzhi;
    }

    public void setWangzhi(String wangzhi) {
        Wangzhi = wangzhi;
    }
}
