package com.zxz.chartview.chart.bean;

import android.support.annotation.ColorRes;

import java.util.ArrayList;

/**
 * Created by zxz on 2017/8/15.
 */

public class LineBean implements ICharData {
    public int defColor;
    @ColorRes
    public int clickColor;
    public float value;
    public String lable;

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

    public ArrayList<? extends ICharData> getChildDatas() {
        return null;
    }
}
