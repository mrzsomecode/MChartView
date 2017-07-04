package com.zxz.chartview.chart.path;

import android.graphics.Canvas;

/**
 * 使用贝塞尔曲线实现雷达图的弧边
 * Created by Administrator on 2017/6/23.
 */

public class ArcPath extends BasePath {
    float upY;
    float upX;
    float cX = 0;
    float cY = 0;
    float upLen = 0;
    float curLen = 0;
    public Canvas mCanvas;

    private ArcPath() {
        super();
    }

    public ArcPath(float centerX, float centerY, float angle) {
        super(centerX, centerY, angle);
    }

    @Override
    public void draw(float x, float y, int position, int maxCount, Canvas canvas) {
        mCanvas = canvas;
        super.draw(x, y, position, maxCount, mCanvas);
        upX = x;
        upY = y;
    }

    //偏移90°，使第一个在正上方
    private double getAngle() {
        return angle * (mPosition - 2) - Math.PI / 2;
    }

    @Override
    public void line2XY(float x, float y) {
        upLen = Math.abs(centerY - upY) / 3 * 2;
        upLen = (float) Math.sqrt(Math.pow(Math.abs(centerY - upY), 2) + Math.pow(Math.abs(centerX - upX), 2)) / 3 * 2;
        curLen = (float) Math.sqrt(Math.pow(Math.abs(centerY - y), 2) + Math.pow(Math.abs(centerX - x), 2)) / 3 * 2;
        float offX = (float) (Math.cos(getAngle() + angle / 2) * Math.min(upLen, curLen));
        float offY = (float) (Math.sin(getAngle() + angle / 2) * Math.min(upLen, curLen));
        cX = centerX + offX;
        cY = centerY + offY;
        quadTo(cX, cY, x, y);
    }

    @Override
    protected void line2Start(float x, float y) {
        upLen = (float) Math.sqrt(Math.pow(Math.abs(centerY - y), 2) + Math.pow(Math.abs(centerX - x), 2)) / 3 * 2;
        curLen = Math.abs(centerY - startY) / 3 * 2;
        float offX = (float) (Math.sin(angle / 2) * Math.min(upLen, curLen));
        float offY = (float) (Math.cos(angle / 2) * Math.min(upLen, curLen));
        cX = centerX - offX;
        cY = centerY - offY;
        quadTo(cX, cY, startX, startY);
    }
}
