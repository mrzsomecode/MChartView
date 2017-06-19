package com.zxz.chartview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zxz.chartview.R;

import java.util.List;

/**
 * 柱状图  Xml必须显示固定高度
 * Created by Administrator on 2017/6/9.
 */
public class MChartView extends BaseChartView<ChartBean> {
    //左
    private int leftColor;
    private int selectLeftColor;
    //右
    private int rightColor;
    private int selectRightColor;

    //最大值 默认100
    private int leftMaxValue = 100;
    private int rightMaxValue = 100;
    //间隔
    private int lefrInterval = 25;
    private int rightInterval = 25;

    public float chartHeight;
    //左边刻度最大宽度
    public float leftMaxW;
    //右边刻度最大宽度
    public float rightMaxW;

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

    //整体居中偏移（大的item）
    private float centerOffset;

    public void setOnItemTouchListener(onItemTouchListener listener) {
        this.listener = listener;
    }


    @Override
    public void setDatas(List<ChartBean> datas) {
        leftColor = getResources().getColor(datas.get(0).getChildDatas().get(0).getColor());
        selectLeftColor = getResources().getColor(datas.get(0).getChildDatas().get(0).getClickColor());
        rightColor = getResources().getColor(datas.get(0).getChildDatas().get(1).getColor());
        selectRightColor = getResources().getColor(datas.get(0).getChildDatas().get(1).getClickColor());
        super.setDatas(datas);
    }

    //计算最大刻度，刻度之间的间距Value,centerOffset
    protected void initMax() {
        leftMaxValue = 100;
        rightMaxValue = 100;
        for (int i = 0; i < datas.size(); i++) {
            ChartBean item = datas.get(i);
            //计算最大文字的宽度 作为每个item的宽度
            itemWidth = (int) Math.max(itemWidth, mPaint.measureText(item.lable));
            leftMaxValue = Math.max(leftMaxValue, item.getChildDatas().get(0).getValue());
            rightMaxValue = Math.max(rightMaxValue, item.getChildDatas().get(1).getValue());
        }
        int[] temp = new int[]{0, leftMaxValue};
        resetMax(temp);
        lefrInterval = temp[0];
        leftMaxValue = temp[1];
        temp = new int[]{0, rightMaxValue};
        resetMax(temp);
        rightInterval = temp[0];
        rightMaxValue = temp[1];
    }

    public MChartView(Context context) {
        this(context, null);
    }

    public MChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //底部预留文字的高度
        startY = getHeight() - getPaddingBottom() - textSize - 20;
        //坐标预留刻度(最大值)的宽度
        startX = mPaint.measureText(leftMaxValue + "") + getPaddingLeft();
        //图形能用的最大高度
        chartHeight = startY - textSize - getPaddingTop();
        leftMaxW = mPaint.measureText(leftMaxValue + "");
        rightMaxW = mPaint.measureText(rightMaxValue + "");
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
        int left = (int) (startX + itemSpace - offsetTouch + centerOffset);
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
                    left = right + itemSpace;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //左右移动
                if (scorllX != 0 && outWidth > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                offsetTouch -= scorllX;
                if (offsetTouch < 0) {
                    offsetTouch = 0;
                } else if (offsetTouch > outWidth) {
                    offsetTouch = outWidth;
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        //计算居中需要的偏移量
        int needW = datas.size() * itemWidth + datas.size() * itemSpace;
        if (mWidth - rightMaxW - leftMaxW > needW) {
            centerOffset = (mWidth - rightMaxW - leftMaxW - needW) / 2 - itemSpace / 2;
        } else {
            centerOffset = 0;
        }
        float chartStartX = startX + itemSpace - offsetTouch + centerOffset;
        //设置显示区域，超出宽度不显示,通过滑动显示
        canvas.clipRect(startX, 0, mWidth - rightMaxW, getHeight());
        for (int i = 0; i < datas.size(); i++) {
            ChartBean item = datas.get(i);
            //最后一个，超出去了就是最大滑动距离
            if (i == datas.size() - 1 && chartStartX + itemSpace + itemWidth + offsetTouch > mWidth) {
                outWidth = (int) (chartStartX + itemSpace + itemWidth + offsetTouch - mWidth);
            }
            //画lable
            mPaint.setColor(selectIndex == i ? getResources().getColor(R.color.fourth_text_color) : lineColor);
            //lable居中
            canvas.drawText(item.lable, chartStartX + (itemWidth - mPaint.measureText(item.lable)) / 2,
                    startY + 15 + textSize, mPaint);
            int childsCount = item.getChildDatas().size();
            for (int j = 0; j < childsCount; j++) {
                changeChartColor(j, i);
                //居中显示，每个小图形间距 5
                float childLeft = chartStartX + ((itemWidth - chartWidth) / 2) + chartWidth / childsCount * j + 5 * j;
                float childRight = childLeft + chartWidth / childsCount;
                float height = ((item.getChildDatas().get(j).getValue() * 1.0f) / (j % 2 == 0 ? leftMaxValue : rightMaxValue) * chartHeight);
                drawChart(childLeft, childRight, startY, 14, height, animationValue, canvas);
            }
            chartStartX += itemSpace + itemWidth;
        }
    }

    private static final String TAG = "MChartView";

    @Override
    protected void drawLines(Canvas canvas) {
        mPaint.setColor(lineColor);
        if (datas != null) {
            float tTop = getPaddingTop();
            int describeTextStartX = getPaddingLeft();
            //描述柱状图的高度跟文字一样
            float descibeTextHeight = Math.abs(mPaint.ascent());
            float descibeTextValue;
            //左上角描述
            for (int i = 0; i < datas.get(0).getChildDatas().size(); i++) {
                descibeTextValue = (i % 2 == 0 ? leftMaxValue : rightMaxValue) * (descibeTextHeight / chartHeight);
                changeChartColor(i, selectIndex);
                changeTextColor(i, selectIndex);
                float height = (descibeTextValue / (i % 2 == 0 ? leftMaxValue : rightMaxValue) * chartHeight);
                drawChart(describeTextStartX, describeTextStartX + 15, tTop + descibeTextHeight + mPaint.descent() / 2, 4,
                        height, animationValue, canvas);
                canvas.drawText(datas.get(0).getChildDatas().get(i).lable, describeTextStartX + 25, (tTop + descibeTextHeight), mPaint);
                describeTextStartX += mPaint.measureText(datas.get(0).getChildDatas().get(i).lable) + 50;
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
            float curY = (startY - ((chartHeight - describeTextPadding - mPaint.descent()) / (lineCount - 1) * i));
            String leftIndex = lefrInterval * i + "";
            //左刻度
            mPaint.setColor(selectLeftColor);
            canvas.drawText(leftIndex, getPaddingLeft() + ((leftMaxW - mPaint.measureText(leftIndex)) / 2), curY + (textSize - 5) / 3, mPaint);
            String rightIndex = rightInterval * i + "";
            //右刻度
            mPaint.setColor(selectRightColor);
            canvas.drawText(rightIndex, mWidth + getPaddingLeft() - rightMaxW +
                            ((rightMaxW - mPaint.measureText(rightIndex)) / 2),
                    curY + (textSize - 5) / 3, mPaint);

            //x轴,刻度
            mPaint.setColor(lineColor);
            drawDashed(canvas, startX, getWidth() - getPaddingRight() - rightMaxW, curY, curY);
        }
        mPaint.setTextSize(textSize);
    }

    /**
     * 画虚线
     */
    private void drawDashed(Canvas canvas, float startX, float endX, float startY, float endY) {
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, mPaint);
    }

    private void changeChartColor(int childIndex, int itemIndex) {
        if (childIndex % 2 == 0)
            mChartPaint.setColor(selectIndex == itemIndex ? selectLeftColor : leftColor);
        else
            mChartPaint.setColor(selectIndex == itemIndex ? selectRightColor : rightColor);
    }

    private void changeTextColor(int childIndex, int itemIndex) {
        if (childIndex % 2 == 0)
            mPaint.setColor(selectIndex == itemIndex ? selectLeftColor : leftColor);
        else
            mPaint.setColor(selectIndex == itemIndex ? selectRightColor : rightColor);
    }

    /**
     * 画柱状图
     */
//    private void drawChart(float left, float right, float bottom, float corner, float value, int index, Canvas canvas) {
//        RectF rectF = new RectF();
//        rectF.left = left;
//        rectF.right = right;
//        rectF.bottom = bottom;
//        rectF.top = bottom - (value / (index % 2 == 0 ? leftMaxValue : rightMaxValue) * chartHeight * animationValue);
//        canvas.drawRoundRect(rectF, corner, corner, mChartPaint);
//    }
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
