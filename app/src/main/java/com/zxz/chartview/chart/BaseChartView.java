package com.zxz.chartview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.zxz.chartview.R;
import com.zxz.chartview.chart.bean.ICharData;

import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public abstract class BaseChartView<T extends ICharData> extends View {
    //每个item间距
    protected float itemSpace = 30;
    protected float xmlItemSpace = 30;
    //文字，线
    protected int lineColor;
    //单个Item宽度，里面的图形等分宽度
    protected int chartWidth;
    //单个item宽度（显示文字宽度）,最长文字宽度超过默认的或者xml配置的，选择最长文字宽度
    protected int itemWidth;
    protected List<T> datas;
    protected int maxValue = 100;
    protected float animationValue;
    protected int interval = 25;
    protected int lineCount = 5;
    protected Paint mPaint;
    protected Paint mChartPaint;
    protected float textSize;
    //图形与描述的间距
    protected int describeTextPadding = 30;
    //是否显示顶部描述
    protected boolean showTopDescribe = false;
    //是否显示底部描述
    protected boolean showBottomDescribe = false;
    protected boolean canOut = false;
    //真实宽度 去除padding
    protected int mWidth;
    //真实高度，去除padding
    protected int mHeight;
    protected int animationDuration = 1000;
    public PathEffect mEffects;

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void showBottomDescribe(boolean show) {
        showBottomDescribe = show;
    }

    public void showTopDescribe(boolean show) {
        showTopDescribe = show;
    }


    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们自定义的样式属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyChartView);
        lineColor = array.getColor(R.styleable.MyChartView_lableColor, Color.DKGRAY);
        chartWidth = array.getDimensionPixelSize(R.styleable.MyChartView_chartWidth, 50);
        textSize = array.getDimensionPixelSize(R.styleable.MyChartView_chartTextSize, 30);
        itemWidth = array.getDimensionPixelSize(R.styleable.MyChartView_chartItemWidth, 150);
        xmlItemSpace = array.getDimensionPixelSize(R.styleable.MyChartView_chartItemSpace, 30);
        itemSpace = xmlItemSpace;
        array.recycle();
        initPaint();
    }


    private static final String TAG = "BaseChartView";

    //初始化画笔
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        //虚线
        mEffects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(mEffects);

        mChartPaint = new Paint();
        mChartPaint.setTextSize(textSize);
        mChartPaint.setAntiAlias(true);
    }

    //计算最大刻度，刻度之间的间距Value
    protected void initMax() {
        float tempMax = 4;
        int tempItemWidth = 0;
        for (int i = 0; i < datas.size(); i++) {
            tempItemWidth = (int) Math.max(tempItemWidth, mPaint.measureText(datas.get(i).getLable()));
            if (datas.get(i).getChildDatas() != null) {
                for (ICharData value : datas.get(i).getChildDatas()) {
                    tempMax = Math.max(tempMax, value.getValue());
                }
            } else {
                tempMax = Math.max(tempMax, datas.get(i).getValue());
            }
        }
        Log.e(TAG, "initMax : " + tempItemWidth);
        itemWidth = tempItemWidth;
        resetMax(tempMax);
    }

    protected void resetMax(float tempMax) {
        float tempInterval = 0;
        maxValue = (int) tempMax;
        while ((tempInterval = (tempMax / (lineCount - 1))) % 5 != 0) {
            float tempInterval2 = (tempMax / lineCount);
            if (tempInterval2 % 5 == 0 && canOut) {
                if (tempMax - maxValue > 0) {
                    interval = (int) Math.ceil(tempMax / (lineCount));
                    maxValue = interval * lineCount;
                    return;
                }
            } else if (tempInterval2 == 1) {
                if (tempMax > maxValue || canOut) {
                    interval = (int) Math.ceil(tempMax / (lineCount));
                    maxValue = interval * lineCount;
                    return;
                }
            }
            tempMax++;
        }
        interval = (int) Math.ceil(tempMax / (lineCount - 1));
        maxValue = interval * lineCount;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
        initMax();
        startAnimation();
    }

    /**
     * 画柱状图
     */
    protected void drawChart(float left, float right, float bottom, float corner, float height, float animationValue, Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = left;
        rectF.right = right;
        rectF.bottom = bottom;
        rectF.top = bottom - (height * animationValue);
        canvas.drawRoundRect(rectF, corner, corner, mChartPaint);
    }

    //动画效果
    protected void startAnimation() {
        animationValue = 0.0f;
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1).setDuration(animationDuration);
        ani.setInterpolator(new DecelerateInterpolator());
        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        ani.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (datas == null)
            return;
        drawLines(canvas);
        drawContent(canvas);
    }

    /**
     * 画虚线
     */
    protected void drawDashed(Canvas canvas, float startX, float endX, float startY, float endY) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(mEffects);
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, mPaint);
        mPaint.setPathEffect(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h - getPaddingTop() - getPaddingBottom();
        postInvalidate();
    }

    protected abstract void drawLines(Canvas canvas);

    protected abstract void drawContent(Canvas canvas);

}
