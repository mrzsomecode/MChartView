package com.zxz.chartview.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.zxz.chartview.R;

import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public abstract class BaseChartView<T extends ICharData> extends View {
    //每个item间距
    protected int itemSpace = 30;
    //文字，线
    protected int lineColor;
    //单个Item宽度，里面的图形等分宽度
    protected int chartWidth;
    //单个item宽度（显示文字宽度）,最长文字宽度超过默认的或者xml配置的，选择最长文字宽度
    protected int itemWidth;
    protected List<T> datas;
    protected int[] maxValue = {100};
    protected float animationValue;
    protected int interval[] = {25};
    protected int lineCount = 5;
    protected Paint mPaint;
    protected Paint mChartPaint;
    protected float textSize;
    //图形与描述的间距
    protected int describeTextPadding = 30;

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
        itemSpace = array.getDimensionPixelSize(R.styleable.MyChartView_chartItemSpace, 30);
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
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(effects);

        mChartPaint = new Paint();
        mChartPaint.setTextSize(textSize);
        mChartPaint.setAntiAlias(true);
    }

    //计算最大刻度，刻度之间的间距Value
    protected void initMax() {
        int[] result = new int[2];
        result[0] = 25;
        result[1] = 100;
        for (int i = 0; i < datas.size(); i++) {
            for (ICharData value : datas.get(i).getChildDatas()) {
                result[1] = Math.max(result[1], value.getValue());
            }
        }
        Log.e(TAG, "initMax: " + result[1]);
        resetMax(result);
        interval[0] = result[0];
        maxValue[0] = result[1];
    }

    protected void resetMax(int[] tempMaxValue) {
        if (tempMaxValue[1] < 100)
            tempMaxValue[1] = 100;
        else {
            int n = tempMaxValue[1] / 100;
            tempMaxValue[1] = 100 * (n + ((tempMaxValue[1] % 100 > 0) ? 1 : 0));
        }
        tempMaxValue[0] = (int) Math.ceil(tempMaxValue[1] / 4.0);
        tempMaxValue[1] = tempMaxValue[0] * 4;
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
        rectF.top = bottom - (height* animationValue);
        canvas.drawRoundRect(rectF, corner, corner, mChartPaint);
    }

    //动画效果
    protected void startAnimation() {
        animationValue = 0.0f;
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1).setDuration(1000);
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
        super.onDraw(canvas);
        if (datas == null)
            return;
        drawLines(canvas);
        drawContent(canvas);
    }

    protected abstract void drawLines(Canvas canvas);

    protected abstract void drawContent(Canvas canvas);

}
