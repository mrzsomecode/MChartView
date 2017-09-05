package com.zxz.chartview.chart.formatter;

/**
 * 百分比
 * Created by zxz on 2017/8/22.
 */
public class PercentageValueFormatter extends ValueFormatter {
    @Override
    public String valueFormatter(float value, String lable) {
        return (int) value + "%";
    }
}
