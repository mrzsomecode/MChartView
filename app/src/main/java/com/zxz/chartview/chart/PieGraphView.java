package com.zxz.chartview.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zxz.chartview.chart.bean.PieBean;

import java.util.List;


/**
 * 饼状图控件。
 * Created by hxw on 2016/8/24.
 */
public class PieGraphView extends View {
    private List<PieBean> datas;
    private float total;
    private RectF normalOval;
    private RectF selectOval;
    private Paint paint;
    private float radius;
    private float cx;
    private float cy;
    private int select = 1;
    private int parentIndex = 0;
    //点击展开宽度
    private float openWidth = 10f;
    private float lableSpace = 10f;
    //中心白色圆心 比例
    private float centerCircleRadius = 0.85f;
    public final TextPaint mTextPaint;
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};

    public PieGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        normalOval = new RectF();
        selectOval = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(30f);
        paint.setStyle(Paint.Style.FILL);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(40f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        radius = Math.min(width, height) / 2 - (lableSpace + openWidth * 2 + paint.getTextSize() / 2);
        cx = width / 2;
        cy = height / 2;
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        normalOval.left = (cx - radius);
        normalOval.top = (cy - radius);
        normalOval.right = (cx + radius);
        normalOval.bottom = (cy + radius);
        selectOval.left = (cx - radius);
        selectOval.top = (cy - radius);
        selectOval.right = (cx + radius);
        selectOval.bottom = (cy + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datas != null) {
            float startAngle = 0;
            float sweepAngle = 0;
            PieBean parent = datas.get(parentIndex);
            for (int i = 0; i < parent.childs.size(); i++) {
                PieBean mPieBean = parent.childs.get(i);
                if (i == parent.childs.size() - 1) {
                    sweepAngle = 360 - startAngle;
                } else {
                    sweepAngle = (mPieBean.value * 1.0f / total * 360);
                }
                mPieBean.startAngle = startAngle;
                mPieBean.endAngle = startAngle + sweepAngle;
                if (i != select) {
                    drawPieChar(canvas, mPieBean, false);
                }
                startAngle += sweepAngle;
            }
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, cy, radius * centerCircleRadius, paint);
            PieBean selecedBean = parent.childs.get(select);
            drawPieChar(canvas, selecedBean, true);
            drawCenterText(canvas, selecedBean);
        }
    }

    /**
     * 画饼
     */
    @NonNull
    private PieBean drawPieChar(Canvas canvas, PieBean bean, boolean isSelected) {
        float middle;
        Point textPoint;
        middle = (bean.startAngle + bean.endAngle) / 2;
        int quadrant = getQuadrant(middle);
        float startAngle = bean.startAngle;
        float sweepAngle = bean.endAngle - bean.startAngle;
        if (isSelected) {
            drawSelcted(canvas, startAngle, sweepAngle, bean, middle, quadrant);
            textPoint = GeomTool.calcCirclePoint(startAngle + sweepAngle / 2, radius + lableSpace + openWidth,
                    selectOval.centerX(), selectOval.centerY(), null);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, cy, radius * centerCircleRadius - openWidth, paint);
        } else {
            paint.setColor(getResources().getColor(bean.getColor()));
            canvas.drawArc(normalOval, startAngle, sweepAngle, true, paint);
            textPoint = GeomTool.calcCirclePoint(startAngle + sweepAngle / 2, radius + lableSpace,
                    normalOval.centerX(), normalOval.centerY(), null);
        }
        drawValue(canvas, bean, textPoint, quadrant);
        return bean;
    }

    /**
     * 画中心选中的信息
     */
    private void drawCenterText(Canvas canvas, PieBean selecedBean) {
        mTextPaint.setColor(getResources().getColor(selecedBean.clickColor));
        String centerText = (int) selecedBean.value + "人\n" + selecedBean.lable;
        SpannableString s = new SpannableString(centerText);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), centerText.indexOf(selecedBean.lable), centerText.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.9f), centerText.indexOf(selecedBean.lable), centerText.length(), 0);
        float centerRadius = radius * centerCircleRadius;
        RectF holeRect = mRectBuffer[0];
        holeRect.left = cx - centerRadius;
        holeRect.top = cy - centerRadius;
        holeRect.right = cx + centerRadius;
        holeRect.bottom = cy + centerRadius;
        RectF boundingRect = mRectBuffer[1];
        boundingRect.set(holeRect);
        StaticLayout layout = new StaticLayout(s, 0, s.length(),
                mTextPaint, (int) Math.max(Math.ceil(boundingRect.width()), 1.f), Layout.Alignment.ALIGN_CENTER, 1.f, 0.f, false);
        canvas.save();
        canvas.translate(boundingRect.left, boundingRect.top + (boundingRect.height() - layout.getHeight()) / 2.f);
        layout.draw(canvas);
        canvas.restore();//别忘了restore
    }

    private void drawValue(Canvas canvas, PieBean bean, Point textPoint, int quadrant) {
        //画value
        paint.setColor(Color.BLACK);
        String value = (int) (bean.value / total * 100) + "%";
        float ascent = Math.abs(paint.ascent());
        switch (quadrant) {
            case 4:
                paint.setTextAlign(Paint.Align.LEFT);
                textPoint.y += ascent / 2;
                break;
            case 3:
                paint.setTextAlign(Paint.Align.RIGHT);
                textPoint.y += ascent / 2;
                break;
            case 2:
                paint.setTextAlign(Paint.Align.RIGHT);
                textPoint.y -= ascent / 2;
                break;
            case 1:
                paint.setTextAlign(Paint.Align.LEFT);
                if ((bean.startAngle + bean.endAngle) / 2 > 315)
                    textPoint.y += ascent / 2;
                break;
        }
        canvas.drawText(value, textPoint.x, textPoint.y, paint);
    }

    private int getQuadrant(float middle) {
        int quadrant = 1;
        if (middle <= 90) {
            quadrant = 4;
        } else if (middle > 90 && middle <= 180) {
            quadrant = 3;
        } else if (middle > 180 && middle < 270) {
            quadrant = 2;
        } else if (middle >= 270 && middle <= 360) {
            quadrant = 1;
        }
        return quadrant;
    }

    private void drawSelcted(Canvas canvas, float startAngle, float sweepAngle, PieBean mPieBean, float middle, int quadrant) {
        selectOval.left = (cx - radius);
        selectOval.top = (cy - radius);
        selectOval.right = (cx + radius);
        selectOval.bottom = (cy + radius);
        float top = 0;
        float left = 0;
        switch (quadrant) {
            case 4:
                top = (float) (Math.sin(Math.toRadians(middle)) * openWidth);
                left = (float) (Math.cos(Math.toRadians(middle)) * openWidth);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 3:
                middle = 180 - middle;
                top = (float) (Math.sin(Math.toRadians(middle)) * openWidth);
                left = (float) (Math.cos(Math.toRadians(middle)) * openWidth);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 2:
                middle = 270 - middle;
                left = (float) (Math.sin(Math.toRadians(middle)) * openWidth);
                top = (float) (Math.cos(Math.toRadians(middle)) * openWidth);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 1:
                middle = 360 - middle;
                left = (float) (Math.sin(Math.toRadians(middle)) * openWidth);
                top = (float) (Math.cos(Math.toRadians(middle)) * openWidth);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
        }
        paint.setColor(getResources().getColor(mPieBean.getClickColor()));
        canvas.drawArc(selectOval, startAngle, sweepAngle, true, paint);
    }

    public void setDatas(List<PieBean> numbers) {
        this.datas = numbers;
        total = 0;
        for (PieBean bean : datas) {
            for (PieBean child : bean.childs)
                total += child.value;
        }
        postInvalidate();
    }

    public void setOpenWidth(float openWidth) {
        this.openWidth = openWidth;
    }

    public void setCenterCircleRadius(float centerCircleRadius) {
        this.centerCircleRadius = centerCircleRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            //判断点是否在扇形内
            float clickAngle = GeomTool.calcAngle(x, y, cx, cy);
            float clickRadius = GeomTool.calcDistance(x, y, cx, cy);
            for (int i = 0; i < datas.get(parentIndex).childs.size(); i++) {
                PieBean point = datas.get(parentIndex).childs.get(i);
                if (point.startAngle <= clickAngle && point.endAngle >= clickAngle && clickRadius < radius) {
                    select = i;
                    if (mOnValueSelectedListener != null)
                        mOnValueSelectedListener.valueSelected(select);
                    invalidate();
                    return true;
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private OnValueSelectedListener mOnValueSelectedListener;

    public void setSelect(int select) {
        this.select = select;
    }

    public interface OnValueSelectedListener {
        void valueSelected(int position);
    }

    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        mOnValueSelectedListener = onValueSelectedListener;
    }
}