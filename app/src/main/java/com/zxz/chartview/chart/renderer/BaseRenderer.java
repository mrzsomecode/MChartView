package com.zxz.chartview.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.zxz.chartview.chart.animation.ChartAnimator;

/**
 * Created by zxz on 2017/8/16.
 */

public abstract class BaseRenderer {
    protected RectF mContentRect = new RectF();
    protected Paint mChartPaint;
    protected Paint mLablePaint;
    protected Paint mValuePaint;
    protected ChartAnimator mChartAnimator;

    public BaseRenderer(RectF contentRect, ChartAnimator chartAnimator) {
        mChartAnimator = chartAnimator;
        this.mContentRect = contentRect;
    }

    public abstract void renderAxisLabels(Canvas canvas);

    public abstract void renderGridLines(Canvas canvas);

    public abstract void renderAxisLine(Canvas c);
}
