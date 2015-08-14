package com.yunyan.mall.entities;

import java.io.Serializable;

/**
 * Created by George on 2015/8/14.
 */
public class Goods implements Serializable{

    private String goods_Name;
    private String goods_Img;

    public String getGoods_Img() {
        return goods_Img;
    }

    public void setGoods_Img(String goods_Img) {
        this.goods_Img = goods_Img;
    }

    public String getGoods_Name() {
        return goods_Name;
    }

    public void setGoods_Name(String goods_Name) {
        this.goods_Name = goods_Name;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goods_Name='" + goods_Name + '\'' +
                ", goods_Img='" + goods_Img + '\'' +
                '}';
    }
}
