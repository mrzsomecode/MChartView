package com.zxz.chartview.chart.bean;

import android.support.annotation.ColorRes;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/9.
 */

public class PieBean implements ICharData {
    public int defColor;
    @ColorRes
    public int clickColor;
    public float value;
    public String formatValue;
    public String lable;
    public float startAngle;
    public float endAngle;

    public ArrayList<PieBean> childs;

    public PieBean(String lable, ArrayList<PieBean> childs) {
        this.childs = childs;
        this.lable = lable;
    }

    public PieBean(String lable, @ColorRes int color, ArrayList<PieBean> childs) {
        this.defColor = color;
        this.childs = childs;
        this.lable = lable;
    }

    public PieBean(String lable, @ColorRes int color, @ColorRes int clickColor, float value) {
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

    public String getFormaterValue() {
        return formatValue;
    }

    @Override
    public boolean isShowLable() {
        return false;
    }

    @Override
    public boolean isShowValue() {
        return false;
    }

    @Override
    public ArrayList<? extends ICharData> getChildDatas() {
        return null;
    }
}
