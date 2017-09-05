package com.zxz.chartview.chart.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/15.
 */

public interface ICharData {

    String getLable();

    float getValue();

    boolean isShowLable();

    boolean isShowValue();

    ArrayList<? extends ICharData> getChildDatas();
}
