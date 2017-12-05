package com.zxz.chartview.chart.bean;

import android.support.annotation.ColorRes;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/9.
 */

public class ChartBean implements ICharData {
    public int defColor;
    @ColorRes
    public int clickColor;
    public float value;
    public String lable;
    public ArrayList<ChartBean> childs;

    public ChartBean(String lable, ArrayList<ChartBean> childs) {
        this.childs = childs;
        this.lable = lable;
    }

    public ChartBean(String lable, @ColorRes int color, ArrayList<ChartBean> childs) {
        this.defColor = color;
        this.childs = childs;
        this.lable = lable;
    }

    public ChartBean(String lable, @ColorRes int color, @ColorRes int clickColor, int value) {
        this.lable = lable;
        this.defColor = color;
        this.clickColor = clickColor;
        this.value = value;
    }


    public int getColor() {
        return defColor;
    }

    public int getClickColor() {
        return clickColor;
    }

    @Override
    public String getLable() {
        return lable;
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public boolean isShowLable() {
        return true;
    }

    @Override
    public boolean isShowValue() {
        return true;
    }

    public ArrayList<ChartBean> getChildDatas() {
        return childs;
    }
}
