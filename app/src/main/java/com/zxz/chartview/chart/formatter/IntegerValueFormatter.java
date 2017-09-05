package com.zxz.chartview.chart.formatter;

/**
 * 整数
 * Created by zxz on 2017/8/22.
 */
public class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String valueFormatter(float value, String lable) {
        return (int) value + "";
    }
}
