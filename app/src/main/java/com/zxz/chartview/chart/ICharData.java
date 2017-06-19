package com.zxz.chartview.chart;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/15.
 */

public interface ICharData {

    String getLable();

    int getValue();

    ArrayList<? extends ICharData> getChildDatas();
}
