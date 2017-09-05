package com.zxz.chartview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zxz.chartview.R;
import com.zxz.chartview.chart.bean.ChartBean;
import com.zxz.chartview.chart.formatter.IntegerValueFormatter;

import java.util.List;

/**
 * Created by zxz on 2017/8/15.
 */

public class LineChartView extends MChartView {
    Path path = new Path();
    private boolean showLable = true;
    private float dotRadius = 12f;
    private String yLable = "正确率";
    private String xLable = "作业记录点";
    private float minSpace = 4f;//最小间距
    private boolean scrollEnd;

    public void setyLable(String yLable) {
        this.yLable = yLable;
    }

    public void setxLable(String xLable) {
        this.xLable = xLable;
    }

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxValue = 100;
        interval = 20;
        lineCount = 6;
        xmlItemSpace = 25;
        describeTextPadding = 15;
        chartValueFormatter = new IntegerValueFormatter();
        yValueFormatter = new IntegerValueFormatter();
    }

    public void setShowLable(boolean showLable) {
        this.showLable = showLable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        startY = getHeight() - getPaddingBottom() - textSize - describeTextPadding;
        leftMaxW = mPaint.measureText(maxValue + "%");
        //坐标预留刻度(最大值)的宽度
        startX = leftMaxW + describeTextPadding + getPaddingLeft();
        //图形能用的最大高度
        chartHeight = startY - getPaddingTop();
        chartHeight -= chartHeight / lineCount + textSize / 2;
        drawLines(canvas);
        if (datas == null)
            return;
        drawContent(canvas);
    }

    @Override
    public void setDatas(List<ChartBean> datas) {
        setDatas(datas, true);
    }

    public void setDatas(List<ChartBean> datas, boolean scrollEnd) {
        this.datas = datas;
        this.scrollEnd = scrollEnd;
        initMax();
        if (scrollEnd) {
            post(new Runnable() {
                @Override
                public void run() {
                    getChartStartX(getMaxChildCount());
                    startAnimation();
                }
            });
        } else {
            startAnimation();
        }
    }

    private float getChartStartX(int maxChildCount) {
        float chartStartX = startX + itemSpace - offsetTouch;
        if (scrollEnd) {
            for (int i = 0; i < maxChildCount; i++) {
                if (chartStartX + itemSpace + itemWidth + offsetTouch > getWidth() - getPaddingRight()) {
                    if (i == maxChildCount - 1) {
                        //最后一个，超出去了就是最大滑动距离
                        outWidth = (int) (chartStartX + itemSpace + itemWidth + offsetTouch - (getWidth() - getPaddingRight()));
                        offsetTouch = outWidth;
                        scrollEnd = false;
                    }
                }
                chartStartX += itemSpace + itemWidth;
            }
        }
        return startX + itemSpace - offsetTouch;
    }

    private int getMaxChildCount() {
        int maxChildCount = 0;
        for (ChartBean bean : datas) {
            if (bean.childs != null)
                maxChildCount = Math.max(maxChildCount, bean.childs.size());
        }
        float cw = (float) Math.ceil((mWidth - leftMaxW - describeTextPadding - maxChildCount * itemWidth));
        float space = cw / (maxChildCount + 1);
        //最小间距4f,如果点太多超出去就可以滑动滑动
        itemSpace = space < 4f ? 4f : space;
        return maxChildCount;
    }

    @Override
    protected void initMax() {
        //计算点的最大宽度
        int tempItemW = 0;
        for (ChartBean bean : datas) {
            if (bean.childs != null) {
                for (ChartBean child : bean.childs) {
                    tempItemW = (int) Math.max(tempItemW, mPaint.measureText(chartValueFormatter.valueFormatter(child.value, child.lable)));
                }
            }
        }
        itemWidth = tempItemW;
    }

    @Override
    protected void drawLines(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.fourth_text_color));
        //y轴文字
        canvas.drawText(yLable, getPaddingLeft(), getPaddingTop() + textSize, mPaint);
        mPaint.setColor(Color.GRAY);
        mPaint.setPathEffect(null);
        path.reset();
        path.moveTo(startX, startY);
        path.lineTo(startX, startY - chartHeight - 20);
        //y轴 线
        canvas.drawPath(path, mPaint);
        for (int i = 0; i < lineCount; i++) {
            //Y轴刻度
            float curY = (startY - (chartHeight / (lineCount - 1) * i));
            String leftIndex = yValueFormatter.valueFormatter(interval * i, yLable);
            mPaint.setColor(yColor);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(leftIndex, getPaddingLeft() + leftMaxW, curY + (textSize - 5) / 3, mPaint);
            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setColor(lineColor);
            //y轴,对应的横线 0就是x轴
            if (i == 0) {
                mPaint.setColor(Color.GRAY);
                mPaint.setPathEffect(null);
                path.reset();
                path.moveTo(startX, startY);
                path.lineTo(getWidth() - getPaddingRight(), startY);
                canvas.drawPath(path, mPaint);
                mPaint.setColor(getResources().getColor(R.color.fourth_text_color));
                canvas.drawText(xLable, getWidth() - getPaddingRight() - mPaint.measureText(xLable),
                        startY + Math.abs(mPaint.ascent()) + describeTextPadding, mPaint);
            } else
                drawDashed(canvas, startX, getWidth() - getPaddingRight(), curY, curY);
        }
    }

    @Override
    protected void drawContent(Canvas canvas) {
        path.reset();
        //设置显示区域，超出宽度不显示,通过滑动显示
        canvas.clipRect(startX, 0, getWidth() - getPaddingRight(), getHeight());
        int maxChildCount = getMaxChildCount();
        for (int i = 0; i < datas.size(); i++) {
            ChartBean item = datas.get(i);
            int childsCount = item.getChildDatas().size();
            float chartStartX = getChartStartX(maxChildCount);
            mPaint.setColor(selectIndex == i ? getResources().getColor(R.color.fourth_text_color) : lineColor);
            int max = (int) (childsCount * animationValue);
            for (int j = 0; j < max; j++) {
                //最后一个，超出去了就是最大滑动距离
                if (i == datas.size() - 1 && chartStartX + itemSpace + itemWidth + offsetTouch > getWidth() - getPaddingRight()) {
                    outWidth = (int) (chartStartX + itemSpace + itemWidth + offsetTouch - (getWidth() - getPaddingRight()));
                }
                ChartBean child = item.getChildDatas().get(j);
                float height = ((child.getValue() * 1.0f) / maxValue * chartHeight);
                float cx = chartStartX + itemWidth / 2;
                float cy = startY - height;
                path.moveTo(cx, cy);
                if (j < max - 1) {
                    ChartBean nextChild = item.getChildDatas().get(j + 1);
                    float nextHeight = ((nextChild.getValue() * 1.0f) / maxValue * chartHeight);
                    float nextCx = chartStartX + itemSpace + itemWidth + itemWidth / 2;
                    float nextCy = startY - nextHeight;
                    path.lineTo(nextCx, nextCy);
                    mChartPaint.setStyle(Paint.Style.STROKE);
                    LinearGradient gradient = new LinearGradient(cx, cy, nextCx, nextCy, getResources().getColor(child.getColor()),
                            getResources().getColor(nextChild.getColor()), Shader.TileMode.CLAMP);
                    mChartPaint.setShader(gradient);
                    canvas.drawPath(path, mChartPaint);
                    mChartPaint.setShader(null);
                    path.reset();
                }
                changeColor(mChartPaint, child, i);
                mChartPaint.setStyle(Paint.Style.FILL);
                mChartPaint.setColor(getResources().getColor(child.getColor()));
                mChartPaint.setAlpha(255);
                canvas.drawCircle(cx, cy, dotRadius, mChartPaint);
                if (showLable) {
                    mPaint.setColor(yColor);
                    String value = chartValueFormatter.valueFormatter(child.value, child.lable);
                    canvas.drawText(value, cx - mPaint.measureText(value + "") / 2, startY - height - dotRadius - mPaint.descent(), mPaint);
                }
                chartStartX += itemSpace + itemWidth;
            }
        }
    }

}
