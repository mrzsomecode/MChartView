package com.zxz.chartview.chart.axis;

import android.graphics.Color;
import android.graphics.PathEffect;

import com.zxz.chartview.chart.formatter.ValueFormatter;

/**
 * Created by zxz on 2017/8/15.
 */
public class BaseAxis {
    protected int mGridColor = Color.GRAY;
    protected float mGridLineWidth = 1.f;
    //轴每个点 延伸线属性
    protected PathEffect mGridDashPathEffect = null;
    protected int mAxisLineColor = Color.GRAY;
    protected float mAxisLineWidth = 1.f;
    //轴自身 线属性
    protected PathEffect mAxisLineDashPathEffect = null;
    protected boolean mEnabled = true;
    protected int mLabelCount = 5;
    protected String mLabel;
    protected boolean mDrawGridLine = true;
    protected boolean mDrawAxisLine = true;
    protected boolean mDrawLables = true;
    protected ValueFormatter mValueFormatter;
    //与content 图表内容的间距
    protected float offsetSpace = 5.f;

    public void setValueFormatter(ValueFormatter valueFormatter) {
        mValueFormatter = valueFormatter;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public void setDrawGridLine(boolean drawGridLine) {
        mDrawGridLine = drawGridLine;
    }

    public void setDrawLables(boolean drawLables) {
        mDrawLables = drawLables;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setGridLineWidth(float gridLineWidth) {
        mGridLineWidth = gridLineWidth;
    }

    public void setGridDashPathEffect(PathEffect gridDashPathEffect) {
        mGridDashPathEffect = gridDashPathEffect;
    }

    public void setAxisLineColor(int axisLineColor) {
        mAxisLineColor = axisLineColor;
    }

    public void setAxisLineWidth(float axisLineWidth) {
        mAxisLineWidth = axisLineWidth;
    }

    public void setAxisLineDashPathEffect(PathEffect axisLineDashPathEffect) {
        mAxisLineDashPathEffect = axisLineDashPathEffect;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public int getLabelCount() {
        return mLabelCount;
    }

    public void setLabelCount(int labelCount) {
        mLabelCount = labelCount;
    }

    public int getGridColor() {
        return mGridColor > 0 ? mGridColor : mAxisLineColor;
    }

    public void setGridColor(int gridColor) {
        mGridColor = gridColor;
    }

    public float getGridLineWidth() {
        return mGridLineWidth;
    }

    public PathEffect getGridDashPathEffect() {
        return mGridDashPathEffect;
    }

    public int getAxisLineColor() {
        return mAxisLineColor;
    }

    public float getAxisLineWidth() {
        return mAxisLineWidth;
    }

    public PathEffect getAxisLineDashPathEffect() {
        return mAxisLineDashPathEffect;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isDrawGridLine() {
        return mDrawGridLine;
    }

    public boolean isDrawAxisLine() {
        return mDrawAxisLine;
    }

    public void setDrawAxisLine(boolean drawAxisLine) {
        mDrawAxisLine = drawAxisLine;
    }

    public boolean isDrawLables() {
        return mDrawLables;
    }
}
