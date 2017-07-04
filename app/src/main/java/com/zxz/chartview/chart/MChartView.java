package com.zxz.chartview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zxz.chartview.R;


/**
 * 柱状图  Xml必须显示固定高度
 * Created by Administrator on 2017/6/9.
 */
public class MChartView extends BaseChartView<ChartBean> {

    public float chartHeight;
    //左边刻度最大宽度
    public float leftMaxW;

    //超出显示的宽度,最大滑动宽度
    private int outWidth;

    //真实宽度 去除padding
    private int mWidth;
    //真实高度，去除padding
    private int mHeight;
    //图形绘制Y坐标起点
    private float startY;
    //图形绘制X坐标起点
    private float startX;

    //当前选中的  默认第一个 下标0开始
    private int selectIndex = 0;

    //点击回调
    private onItemTouchListener listener;
    //手指滑动
    private float offsetTouch = 0;


    public void setOnItemTouchListener(onItemTouchListener listener) {
        this.listener = listener;
    }

    public MChartView(Context context) {
        this(context, null);
    }

    public MChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        canOut = true;
        describeTextPadding = 10;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //底部预留文字的高度
        startY = getHeight() - getPaddingBottom() - textSize - 20;
        leftMaxW = mPaint.measureText(maxValue + "");
        //坐标预留刻度(最大值)的宽度
        startX = leftMaxW + 5 + getPaddingLeft();
        //图形能用的最大高度
        chartHeight = startY - (textSize - 5) - describeTextPadding - getPaddingTop();
        if (showTopDescribe)
            chartHeight -= textSize;
        super.onDraw(canvas);
    }

    int x, y;
    long downTime;

    //    private VelocityTracker vTracker = null;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //监听点击，根据坐标判断，点击了哪一块区域
        if (datas == null)
            return false;
        int scorllX = (int) (ev.getX() - x);
        x = (int) ev.getX();
        y = (int) ev.getY();
        int left = (int) (startX + itemSpace - offsetTouch);
        int right;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (System.currentTimeMillis() - downTime > 300)
                    break;
                for (int i = 0; i < datas.size(); i++) {
                    right = left + itemWidth;
                    Rect rect = new Rect(left, 0, right, (int) startY);
                    if (rect.contains(x, y)) {
                        if (listener != null)
                            listener.onChartTouch(i);
                        selectIndex = i;
                        invalidate();
                    }
                    left = (int) (right + itemSpace);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //左右移动
                if (scorllX != 0 && outWidth > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                offsetTouch -= scorllX;
                if (offsetTouch < 0) {
                    offsetTouch = 0;
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (offsetTouch > outWidth) {
                    offsetTouch = outWidth;
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        float cw = (float) Math.ceil((mWidth - leftMaxW - 5 - datas.size() * itemWidth));
        float space = cw / (datas.size() + 1);
        itemSpace = space > itemSpace ?  space : itemSpace;
        //计算居中需要的偏移量
        float chartStartX = startX + itemSpace - offsetTouch;
        //设置显示区域，超出宽度不显示,通过滑动显示
        canvas.clipRect(startX, 0, getWidth() - getPaddingRight() - 5 - startX, getHeight());
        for (int i = 0; i < datas.size(); i++) {
            ChartBean item = datas.get(i);
            //最后一个，超出去了就是最大滑动距离
            if (i == datas.size() - 1 && chartStartX + itemSpace + itemWidth + offsetTouch > getWidth() - getPaddingRight()) {
                outWidth = (int) (chartStartX + itemSpace + itemWidth + offsetTouch - (getWidth() - getPaddingRight()));
            }
            //画lable
            mPaint.setColor(selectIndex == i ? getResources().getColor(R.color.fourth_text_color) : lineColor);
            //lable居中
            canvas.drawText(item.lable, chartStartX + (itemWidth - mPaint.measureText(item.lable)) / 2,
                    startY + 15 + textSize, mPaint);
            int childsCount = item.getChildDatas().size();
            for (int j = 0; j < childsCount; j++) {
                ChartBean child = item.getChildDatas().get(j);
                changeColor(mChartPaint, child, i);
                //居中显示，每个小图形间距 5
                float itemChildW = (chartWidth - (5 * (childsCount - 1))) / childsCount;
                float childLeft = chartStartX + ((itemWidth - chartWidth) / 2) + itemChildW * j + 5 * j;
                float childRight = childLeft + itemChildW;
                float height = ((child.getValue() * 1.0f) / maxValue * chartHeight);
                drawChart(childLeft, childRight, startY, 14, height, animationValue, canvas);
            }
            chartStartX += itemSpace + itemWidth;
        }
    }

    private static final String TAG = "MChartView";

    @Override
    protected void drawLines(Canvas canvas) {
        mPaint.setColor(lineColor);
        if (datas != null && showTopDescribe) {
            float tTop = getPaddingTop();
            int describeTextStartX = getPaddingLeft();
            //描述柱状图的高度跟文字一样
            float descibeTextHeight = Math.abs(mPaint.ascent());
            float descibeTextValue;
            //左上角描述
            for (int i = 0; i < datas.get(0).getChildDatas().size(); i++) {
                ChartBean child = datas.get(0).getChildDatas().get(i);
                descibeTextValue = maxValue * (descibeTextHeight / chartHeight);
                mChartPaint.setColor(getResources().getColor(child.getClickColor()));
                mPaint.setColor(getResources().getColor(child.getClickColor()));
                float height = (descibeTextValue / maxValue * chartHeight);
                drawChart(describeTextStartX, describeTextStartX + 15, tTop + descibeTextHeight + mPaint.descent() / 2, 4,
                        height, animationValue, canvas);
                canvas.drawText(child.lable, describeTextStartX + 25, (tTop + descibeTextHeight), mPaint);
                describeTextStartX += mPaint.measureText(child.lable) + 50;
            }
        }
        //Y轴
//        drawDashed(canvas, startX, startX, startY, tTop);
        //箭头
//        canvas.drawLine(startX, tTop, startX + 10, tTop + 10, mPaint);
//        canvas.drawLine(startX, tTop, startX - 10, tTop + 10, mPaint);
        mPaint.setTextSize(textSize - 5);
        for (int i = 0; i < lineCount; i++) {
            //画横向刻度数字
            float curY = (startY - (chartHeight / (lineCount) * i));
            String leftIndex = interval * i + "";
            //左刻度
            mPaint.setColor(getResources().getColor(datas.get(0).getChildDatas().get(0).getClickColor()));
            canvas.drawText(leftIndex, getPaddingLeft() + ((leftMaxW - mPaint.measureText(leftIndex)) / 2), curY + (textSize - 5) / 3, mPaint);
            //x轴,刻度
            mPaint.setColor(lineColor);
            drawDashed(canvas, startX, getWidth() - getPaddingRight(), curY, curY);
        }
        mPaint.setTextSize(textSize);
    }

    private void changeColor(Paint paint, ChartBean child, int i) {
        paint.setColor(getResources().getColor(selectIndex == i ? child.getClickColor() : child.getColor()));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h - getPaddingTop() - getPaddingBottom();
        postInvalidate();
    }

    /**
     * 点击回调
     */
    public interface onItemTouchListener {
        void onChartTouch(int index);
    }
}
