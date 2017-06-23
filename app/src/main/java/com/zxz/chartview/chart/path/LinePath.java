package com.zxz.chartview.chart.path;

/**
 * 直线边
 * Created by Administrator on 2017/6/23.
 */

public class LinePath extends BasePath {

    @Override
    protected void line2Start(float x, float y) {
        lineTo(startX, startY);
    }

    @Override
    public void line2XY(float x, float y) {
        lineTo(x, y);
    }
}
