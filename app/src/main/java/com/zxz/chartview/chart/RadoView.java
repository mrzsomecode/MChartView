package com.zxz.chartview.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.zxz.chartview.R;
import com.zxz.chartview.chart.bean.ChartBean;
import com.zxz.chartview.chart.path.ArcPath;
import com.zxz.chartview.chart.path.BasePath;
import com.zxz.chartview.chart.path.LinePath;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷达图 支持图形直边和弧边
 * Created by Administrator on 2017/6/14.
 */

public class RadoView extends BaseChartView<ChartBean> {
    protected Point point = new Point();
    private int count = 5;                //维度个数
    private float angle = (float) (Math.PI * 2 / count);
    private float radius = -1;                   //网格最大半径
    private int centerX;                  //中心X
    private int centerY;                  //中心Y
    public float textMaxW;
    //底部说明的高度
    private float bottomExplainH = 0;
    //比例尺的宽度
    public float mRatioW = 105;
    public float mRatioStartX = 0;


    public String[] bottomExplainStr;
    private float maxBottomWidth = 0;
    private float bottomNumberPadding = 14;
    private float textHeight = 0;

    private BasePath regionPath;
    private int pathType = BasePath.LINE;
    private boolean showRatio = false;


    /**
     * 是否显示比例尺
     */
    public void setShowRatio(boolean showRatio) {
        this.showRatio = showRatio;
    }

    /**
     * {@link BasePath#LINE,BasePath#ARC}
     *
     * @param pathType
     */
    public void setRegioPath(@BasePath.RegioPath int pathType) {
        this.pathType = pathType;
    }

    @Override
    public void setDatas(List<ChartBean> datas) {
        if (datas != null && datas.size() > 0) {
            count = datas.get(0).getChildDatas().size();
            angle = (float) (Math.PI * 2 / count);
        }
        super.setDatas(datas);
    }

    public void setBottomExplainStr(String[] bottomExplainStr) {
        this.bottomExplainStr = bottomExplainStr;
        maxBottomWidth = 0;
        for (String s : bottomExplainStr) {
            maxBottomWidth = Math.max(maxBottomWidth, mPaint.measureText(s));
        }
        //加上数字图形宽度(textSize+bottomNumberPadding) 间距10
        maxBottomWidth += mPaint.measureText("5") + bottomNumberPadding + 10 + 10;
    }

    public RadoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyChartView);
        radius = array.getDimensionPixelSize(R.styleable.MyChartView_radoRadius, -1);
        array.recycle();
    }

    public RadoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadoView(Context context) {
        this(context, null);
    }

    private static final String TAG = "RadoView";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int specSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        textHeight = Math.abs(mPaint.ascent());
        bottomExplainH = textHeight * 5;
        //如果没有设置半径，自动计算半径(半径和控件高度 必须设置一个)
        //减去描述的距离,底部预留文字高度
        if (radius == -1) {
            if (showTopDescribe)
                radius = Math.min(h - bottomExplainH - describeTextPadding - textHeight * 2 - getPaddingTop() - getPaddingBottom(), w - getPaddingLeft() - getPaddingRight()) / 2;
            else
                radius = Math.min(h - bottomExplainH - textHeight - getPaddingTop() - getPaddingBottom(), w - getPaddingLeft() - getPaddingRight()) / 2;
        }

        switch (specMode) {
            case View.MeasureSpec.EXACTLY:
                h = specSize;
                break;
            case View.MeasureSpec.UNSPECIFIED:
            case View.MeasureSpec.AT_MOST:
                mPaint.setTextSize(textSize);
                //底部描述高度(bottomExplainH)，加上顶部描述高度(textHeight+describeTextPadding),
                // 再加上圆环维度标注数字的高度和间距(textHeight+itemSpace+mPaint.descent)
                h = (int) (radius * 2 + bottomExplainH + textHeight + describeTextPadding +
                        textHeight + itemSpace + mPaint.descent()
                        + getPaddingTop() + getPaddingBottom());
                if (!showTopDescribe)
                    h -= textHeight + describeTextPadding;
                if (!showBottomDescribe)
                    h -= bottomExplainH;
                break;
        }
        setMeasuredDimension(w, h);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = (w - getPaddingLeft() - getPaddingRight()) / 2;
        centerY = (int) (h - getPaddingBottom() - radius);
        if (showBottomDescribe)
            centerY -= bottomExplainH;
        textMaxW = mPaint.measureText(maxValue + "");
        mRatioStartX = getWidth() - getPaddingRight() - 20 - textMaxW - mRatioW;
        //直线边还是弧线边
        if (pathType == BasePath.LINE) {
            regionPath = new LinePath();
        } else {
            regionPath = new ArcPath(centerX, centerY, angle);
        }
        //如果圆会超过比例尺，就减去,然后重新计算控件高度
        if (showRatio && centerX + radius > mRatioStartX) {
            radius -= centerX + radius - mRatioStartX;
            requestLayout();
        } else
            postInvalidate();
    }

    @Override
    protected void drawLines(Canvas canvas) {
        mPaint.setColor(lineColor);
        for (int i = 0; i < count; i++) {
            drawCircle(canvas, i);
            drawLines(canvas, i);
        }
    }

    @Override
    protected void drawContent(Canvas canvas) {
        float explainStartX = getPaddingLeft();
        drawText(canvas, datas.get(0).getChildDatas());
        for (ChartBean item : datas) {
            mChartPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mChartPaint.setColor(getResources().getColor(item.getColor()));
            mChartPaint.setAlpha(127);
            drawRegion(canvas, item.getChildDatas());
            //描述柱状图的高度跟文字一样
            if (showTopDescribe) {
                drawChart(explainStartX, explainStartX + 15, getPaddingTop() + textHeight, 5, textHeight, animationValue, canvas);
                canvas.drawText(item.lable, explainStartX + 25, getPaddingTop() + textHeight - mPaint.descent() / 2, mChartPaint);
            }
            explainStartX += 65 + mChartPaint.measureText(item.lable);
        }
    }

    /**
     * 绘制圆环
     */

    private void drawCircle(Canvas canvas, int i) {
        if (i > 0) {
            //每个圆环的半径
            float r = radius / (count - 1);
            float curR = r * i;
            PathEffect effects = null;
            if (i < count - 1) {
                int interval = 20 - 5 * i;
                effects = new DashPathEffect(new float[]{interval, interval, interval, interval}, 0);
                mPaint.setPathEffect(effects);
            }
            //比例尺
            if (showRatio) {
                float ratioStartX = getWidth() - getPaddingRight() - 20 - textMaxW - mRatioW;
                float ratioStartY = centerY + radius - (textHeight + 5) * (i - 1);
                mPaint.setTextSize(textSize - 4);
                canvas.drawText(i * interval + "", getWidth() - getPaddingRight() - textMaxW, ratioStartY + textHeight / 3, mPaint);
                drawDashed(canvas, ratioStartX, ratioStartX + mRatioW, ratioStartY, ratioStartY, effects);
            }
            //圆环
            canvas.drawCircle(centerX, centerY, curR, mPaint);
            mPaint.setTextSize(textSize);
        }
        mPaint.setPathEffect(null);
    }

    /**
     * 绘制直线
     */
    private void drawLines(Canvas canvas, int i) {
        path.reset();
        path.moveTo(centerX, centerY);
        Point point = getPoint(i);
        path.lineTo(point.x, point.y);
        canvas.drawPath(path, mPaint);
    }

    public Point getPoint(int position) {
        point.x = (int) (centerX + radius * Math.cos(getAngle(position)));
        point.y = (int) (centerY + radius * Math.sin(getAngle(position)));
        return point;
    }

    /**
     * 绘制文字
     *
     * @param canvas
     * @param item
     */
    private void drawText(Canvas canvas, ArrayList<ChartBean> item) {
        float trueWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float maxCount = trueWidth / maxBottomWidth;
        if (maxCount >= 4) maxCount = 4;
        else maxCount = (float) Math.floor(maxCount);
        //底部描述参数
        //小于就是第一行
        float bottomChartWidth = 0;
        float leftBottomWidth = 0;
        float rightBottomWidth = 0;
        if (showBottomDescribe && bottomExplainStr != null) {
            bottomChartWidth = mPaint.measureText("5") + bottomNumberPadding;
            leftBottomWidth = mPaint.measureText(bottomExplainStr[0]) + bottomChartWidth;
            rightBottomWidth = bottomExplainStr.length > 3 ? mPaint.measureText(bottomExplainStr[3]) + bottomChartWidth : leftBottomWidth;
        }
        float bottomItemW = 0;
        float btStartX = getPaddingLeft();
        float bottomStartY = centerY + radius + textHeight * 3;
        int i = 0;
        for (ChartBean bean : item) {
            mPaint.setTextSize(textSize);
            mPaint.setColor(getResources().getColor(R.color.fourth_text_color));
            //环形lable
            String key = bean.getLable();
            Point point = getPoint(i);
            double curA = getAngle(i);
            float x = point.x;
            float y = point.y;
            float dis = mPaint.measureText(key);//文本长度
            if (curA == -Math.PI / 2) {
                canvas.drawText(key, x - dis / 2, y - itemSpace - mPaint.descent(), mPaint);
            } else if (curA >= 0 && curA <= Math.PI / 2) {//第4象限
                canvas.drawText(key, x, y + textHeight + itemSpace, mPaint);
            } else if (curA >= Math.PI / 2 && curA <= Math.PI) {//第3象限
                canvas.drawText(key, x - dis, y + textHeight + itemSpace, mPaint);
            } else if (curA > Math.PI && curA < Math.PI + Math.PI / 2) {//第2象限
                canvas.drawText(key, x - dis - itemSpace, y, mPaint);
            } else if (curA <= 0 && curA > -Math.PI / 2) {//第1象限
                canvas.drawText(key, x + itemSpace, y, mPaint);
            }
            if (showBottomDescribe) {
                //底部文字的宽度
                float bottomStrWidth = mPaint.measureText(bottomExplainStr[i]);
                float offset = 0;
                switch ((int) ((i + 1) % maxCount)) {
                    case 1:
                        //第一个 靠左,startX不变，有多宽占多宽
                        bottomItemW = leftBottomWidth;
                        break;
                    //中间2个 居中
                    case 2:
                        bottomItemW = (trueWidth - leftBottomWidth - rightBottomWidth) / 2;
                        offset = ((bottomItemW - bottomStrWidth - bottomChartWidth) / 2) * (4 / 3f);
                        break;
                    case 3:
                        bottomItemW = (trueWidth - leftBottomWidth - rightBottomWidth) / 2;
                        offset = ((bottomItemW - bottomStrWidth - bottomChartWidth) / 2) * (2 / 3f);
                        break;
                    //最后一个 靠右，有多宽占多宽
                    case 0:
                        bottomItemW = rightBottomWidth;
                        break;
                }
                //换行了
                if (i + 1 > maxCount) {
                    btStartX -= trueWidth;
                    bottomStartY += textHeight * 2;
                    maxCount += maxCount;
                }
                float curStartX = btStartX + offset;
                mChartPaint.setColor(getResources().getColor(R.color.app_main_color));
                drawChart(curStartX, curStartX + bottomChartWidth, bottomStartY + 2, 5, textHeight + 4, 1f, canvas);
                canvas.drawText(bottomExplainStr[i], curStartX + bottomChartWidth + bottomNumberPadding,
                        bottomStartY - mPaint.descent() / 2, mPaint);
                mPaint.setColor(Color.WHITE);
                canvas.drawText((i + 1) + "", curStartX + (bottomChartWidth - mPaint.measureText(i + "")) / 2, bottomStartY - mPaint.descent() / 2, mPaint);
                btStartX += bottomItemW;
            }
            i++;
        }
    }

    //偏移90°，使第一个在正上方
    private double getAngle(int i) {
        return angle * i - Math.PI / 2;
    }

    /**
     * 绘制区域
     *
     * @param canvas
     * @param item
     */
    private void drawRegion(Canvas canvas, ArrayList<ChartBean> item) {
        regionPath.reset();
        for (int i = 0; i < item.size(); i++) {
            double percent = item.get(i).getValue() / ((maxValue - interval) * 1.0);
            float x = (float) (centerX + radius * Math.cos(getAngle(i)) * percent * animationValue);
            float y = (float) (centerY + radius * Math.sin(getAngle(i)) * percent * animationValue);
            regionPath.draw(x, y, i + 1, item.size(), canvas);
            canvas.drawCircle(x, y, 5, mChartPaint);
        }
        canvas.drawPath(regionPath, mChartPaint);
    }

}
