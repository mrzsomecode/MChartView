package com.zxz.chartview.chart.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.zxz.chartview.chart.animation.ChartAnimator;
import com.zxz.chartview.chart.axis.YAxis;

/**
 * Created by zxz on 2017/8/18.
 */

public class YAxisRenderer extends BaseRenderer {
    YAxis mYAxis;

    public YAxisRenderer(RectF contentRectF, YAxis yAxis, ChartAnimator chartAnimator) {
        super(contentRectF, chartAnimator);
        mYAxis = yAxis;
    }

    protected void setupGridPaint() {
        mChartPaint.setColor(mYAxis.getGridColor());
        mChartPaint.setStrokeWidth(mYAxis.getGridLineWidth());
        mChartPaint.setPathEffect(mYAxis.getGridDashPathEffect());
    }

    protected void setUpLinePaint() {
        mLablePaint.setColor(mYAxis.getAxisLineColor());
        mLablePaint.setStrokeWidth(mYAxis.getAxisLineWidth());
        mLablePaint.setPathEffect(mYAxis.getAxisLineDashPathEffect());
    }

    public void drawYAxisValue(Canvas canvas) {
        if (!mYAxis.isEnabled())
            return;
        setUpLinePaint();

    }

    public void drawYAxisGrid(Canvas canvas) {
        setupGridPaint();

    }

    @Override
    public void renderAxisLabels(Canvas canvas) {

    }

    @Override
    public void renderGridLines(Canvas canvas) {

    }

    @Override
    public void renderAxisLine(Canvas c) {

    }
}
