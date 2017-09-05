package com.zxz.chartview.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.zxz.chartview.chart.axis.XAxis;

/**
 * Created by zxz on 2017/8/18.
 */

public class XAxisRenderer extends BaseRenderer {
    private XAxis xAxis;
    private Path path = new Path();

    public XAxisRenderer(RectF contentRect, XAxis xAxis) {
        super(contentRect, null);
        this.xAxis = xAxis;
    }

    protected void setupGridPaint() {
        mChartPaint.setColor(xAxis.getGridColor());
        mChartPaint.setStrokeWidth(xAxis.getGridLineWidth());
        mChartPaint.setPathEffect(xAxis.getGridDashPathEffect());
    }

    protected void setUpLinePaint() {
        mChartPaint.setColor(xAxis.getAxisLineColor());
        mChartPaint.setStrokeWidth(xAxis.getAxisLineWidth());
        mChartPaint.setPathEffect(xAxis.getAxisLineDashPathEffect());
    }

    @Override
    public void renderAxisLabels(Canvas canvas) {
        if (!xAxis.isEnabled() || !xAxis.isDrawLables())
            return;

    }

    @Override
    public void renderGridLines(Canvas canvas) {
        if (!xAxis.isEnabled() || !xAxis.isDrawGridLine())
            return;
        setupGridPaint();
        drawLine(canvas, mChartPaint);
    }

    @Override
    public void renderAxisLine(Canvas canvas) {
        if (!xAxis.isEnabled() || !xAxis.isDrawAxisLine())
            return;
        setUpLinePaint();
        drawLine(canvas, mChartPaint);
    }

    public void drawLine(Canvas canvas, Paint paint) {
        path.reset();
        path.moveTo(mContentRect.left, mContentRect.bottom);
        path.lineTo(mContentRect.right, mContentRect.bottom);
        canvas.drawPath(path, paint);
        canvas.drawText(xAxis.getLabel(), mContentRect.right - paint.measureText(xAxis.getLabel()),
                mContentRect.bottom + Math.abs(paint.ascent()), paint);
    }
}
