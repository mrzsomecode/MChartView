package com.zxz.chartview.chart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

import com.zxz.chartview.R;
import com.zxz.chartview.chart.bean.PieBean;
import com.zxz.chartview.chart.formatter.ValueFormatter;

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
    private int select = 0;
    private int parentIndex = 0;
    //点击展开宽度
    private float openWidth = 10f;
    //展开动画进度
    private float openAnimationValue = 1.f;
    private float lableSpace = 10f;
    //中心白色圆心 比例
    private float centerCircleRadius = 0.8f;
    public final TextPaint mTextPaint;
    private RectF[] mRectBuffer = {new RectF(), new RectF(), new RectF()};

    private ValueFormatter centerValueFormatter;

    private int maxValue;//所有百分比加起来
    int index;//当前画到第几个
    PieBean parent;//父节点是那个

    public void setCenterValueFormatter(ValueFormatter centerValueFormatter) {
        this.centerValueFormatter = centerValueFormatter;
    }

    public PieGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        normalOval = new RectF();
        selectOval = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dp_12));
        paint.setStyle(Paint.Style.FILL);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dp_16));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        radius = Math.min(width, height) / 2 - (lableSpace + openWidth * 2 + paint.getTextSize());
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
        if (datas != null && datas.size() > 0) {
            maxValue = 0;
            index = 0;
            float startAngle = 0;
            float sweepAngle = 0;
            parent = datas.get(parentIndex);
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
                    index++;
                    drawPieChar(canvas, mPieBean, false);
                }
                startAngle += sweepAngle;
            }
            index++;
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
            //选中状态，根据动画value 半径递增
            textPoint = GeomTool.calcCirclePoint(startAngle + sweepAngle / 2, radius + lableSpace + openWidth + openWidth * openAnimationValue,
                    selectOval.centerX(), selectOval.centerY(), null);
            paint.setColor(Color.WHITE);
            //选中状态，根据动画value 半径递减
            canvas.drawCircle(cx, cy, radius * centerCircleRadius - openWidth * openAnimationValue, paint);
        } else {
            paint.setColor(getResources().getColor(bean.getColor()));
            canvas.drawArc(normalOval, startAngle, sweepAngle, true, paint);
            textPoint = GeomTool.calcCirclePoint(startAngle + sweepAngle / 2, radius + lableSpace + openWidth,
                    normalOval.centerX(), normalOval.centerY(), null);
        }
        drawValue(canvas, bean, textPoint, quadrant);
        return bean;
    }

    //点击选中动画刷新
    public void startSelectedAnimation() {
        ValueAnimator open = ObjectAnimator.ofFloat(0.f, 1.f).setDuration(300);
        open.setInterpolator(null);
        open.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                openAnimationValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        open.start();
    }

    /**
     * 画中心选中的信息
     */
    private void drawCenterText(Canvas canvas, PieBean selecedBean) {
        mTextPaint.setColor(getResources().getColor(selecedBean.clickColor));
        String centerText = (int) selecedBean.value + "人\n" + selecedBean.lable;
        if (centerValueFormatter != null) {
            centerText = centerValueFormatter.valueFormatter(selecedBean.value, selecedBean.lable);
        }
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

    //画value值
    private void drawValue(Canvas canvas, PieBean bean, Point textPoint, int quadrant) {
        if (bean.value <= 0) {
            return;
        }
        paint.setColor(getResources().getColor(R.color.fourth_text_color));
        maxValue += (int) (bean.value / total * 100);
        //保证所有value加起来为100%
        String value;
        if (index == parent.childs.size() - 1 && maxValue < 100) {
            value = (int) (bean.value / total * 100) + (100 - maxValue) + "%";
        } else
            value = (int) (bean.value / total * 100) + "%";
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

    //获得象限

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
                top = (float) (Math.sin(Math.toRadians(middle)) * openWidth * openAnimationValue);
                left = (float) (Math.cos(Math.toRadians(middle)) * openWidth * openAnimationValue);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 3:
                middle = 180 - middle;
                top = (float) (Math.sin(Math.toRadians(middle)) * openWidth * openAnimationValue);
                left = (float) (Math.cos(Math.toRadians(middle)) * openWidth * openAnimationValue);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 2:
                middle = 270 - middle;
                left = (float) (Math.sin(Math.toRadians(middle)) * openWidth * openAnimationValue);
                top = (float) (Math.cos(Math.toRadians(middle)) * openWidth * openAnimationValue);
                left = top = Math.max(left, top);
                selectOval.left -= left;
                selectOval.right += left;
                selectOval.top -= top;
                selectOval.bottom += top;
                break;
            case 1:
                middle = 360 - middle;
                left = (float) (Math.sin(Math.toRadians(middle)) * openWidth * openAnimationValue);
                top = (float) (Math.cos(Math.toRadians(middle)) * openWidth * openAnimationValue);
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
            if (datas != null && datas.size() > parentIndex) {
                for (int i = 0; i < datas.get(parentIndex).childs.size(); i++) {
                    PieBean point = datas.get(parentIndex).childs.get(i);
                    if (point.startAngle <= clickAngle && point.endAngle >= clickAngle && clickRadius < radius) {
                        select = i;
                        startSelectedAnimation();
                        if (mOnValueSelectedListener != null)
                            mOnValueSelectedListener.valueSelected(select);
                        return true;
                    }
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private OnValueSelectedListener mOnValueSelectedListener;

    public void setSelect(int select) {
        setSelected(select, true);
    }

    public void setSelected(int select, boolean startAnimation) {
        this.select = select;
        if (startAnimation)
            startSelectedAnimation();
        else
            postInvalidate();
    }

    public interface OnValueSelectedListener {
        void valueSelected(int position);
    }

    public void setOnValueSelectedListener(OnValueSelectedListener onValueSelectedListener) {
        mOnValueSelectedListener = onValueSelectedListener;
    }
}