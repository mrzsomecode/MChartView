package com.zxz.chartview.chart;

import android.graphics.Point;
import android.graphics.RectF;

/**
 * 与2D屏幕有关的计算，屏幕约定为X轴向右，Y轴向下，顺时针角度增加。
 * Created by hxw on 2016/8/25.
 */
public class GeomTool {

    /**
     * 这个方法放在这里展示了原始的计算过程
     *
     * @see #calcCirclePoint
     */
    @Deprecated
    private static Point calcCirclePoint2(float angle, float radius, float cx, float cy, Point resultOut) {
        if (resultOut == null) {
            resultOut = new Point();
        }

        // 将angle控制在0-360，注意这里的angle是从X正轴顺时针增加。而sin,cos等的计算是X正轴开始逆时针增加
        angle = clampAngle(angle);
        double radians = angle / 180f * Math.PI;
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        double x = 0, y = 0;

        if (angle == 0 || angle == 360) {
            // sin：0 cos: 1
            x = cx + radius;
            y = cy;
        } else if (angle > 0 && angle < 90) {
            // sin：0~1 cos: 1~0
            double dy = radius * sin;
            double dx = radius * cos;
            x = cx + dx;
            y = cy + dy;
        } else if (angle == 90) {
            // sin：1 cos: 0
            x = cx;
            y = cy + radius;
        } else if (angle > 90 && angle < 180) {
            // sin：1~0 cos: 0~-1
            double dy = radius * sin;
            double dx = radius * cos;
            x = cx + dx;
            y = cy + dy;
        } else if (angle == 180) {
            // sin：0 cos: -1
            x = cx - radius;
            y = cy;
        } else if (angle > 180 && angle < 270) {
            // sin：0~-1 cos: -1~0
            double dy = radius * sin;
            double dx = radius * cos;
            x = cx + dx;
            y = cy + dy;
        }  else if (angle == 270) {
            // sin：-1 cos: 0
            x = cx;
            y = cy - radius;
        } else if (angle > 270 && angle < 360) {
            // sin：-1~0 cos: 0~1
            double dy = radius * sin;
            double dx = radius * cos;
            x = cx + dx;
            y = cy + dy;
        }

        resultOut.set((int) x, (int) y);
        return resultOut;
    }

    /**
     * 计算指定角度、圆心、半径时，对应圆周上的点。
     * @param angle 角度，0-360度，X正轴开始，顺时针增加。
     * @param radius 圆的半径
     * @param cx 圆心X
     * @param cy 圆心Y
     * @param resultOut 计算的结果(x, y) ，方便对象的重用。
     * @return resultOut, or new Point if resultOut is null.
     */
    public static Point calcCirclePoint(float angle, float radius, float cx, float cy, Point resultOut) {
        if (resultOut == null) resultOut = new Point();

        // 将angle控制在0-360，注意这里的angle是从X正轴顺时针增加。而sin,cos等的计算是X正轴开始逆时针增加
        angle = clampAngle(angle);
        double radians = angle / 180f * Math.PI;
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        double dy = radius * sin;
        double dx = radius * cos;
        double x = cx + dx;
        double y = cy + dy;

        resultOut.set((int) x, (int) y);
        return resultOut;
    }

    /**
     * 计算坐标(x, y)到圆心(cx, cy)形成的角度，角度从0-360，360度就是0度，顺时针增加
     * （x轴向右，y轴向下）若2点重合返回-1;
     */
    public static int calcAngle(float x, float y, float cx, float cy) {
        double resultDegree = 0;

        double vectorX = x - cx; // 点到圆心的X轴向量，X轴向右，向量为(0, vectorX)
        double vectorY = cy - y; // 点到圆心的Y轴向量，Y轴向上，向量为(0, vectorY)
        if (vectorX == 0 && vectorY == 0) {
            // 重合？
            return -1;
        }
        // 点落在X,Y轴的情况这里就排除
        if (vectorX == 0) {
            // 点击的点在Y轴上，Y不会为0的
            if (vectorY > 0) {
                resultDegree = 90;
            } else {
                resultDegree = 270;
            }
        } else if (vectorY == 0) {
            // 点击的点在X轴上，X不会为0的
            if (vectorX > 0) {
                resultDegree = 0;
            } else {
                resultDegree = 180;
            }
        } else {
            // 根据形成的正切值算角度
            double tanXY = vectorY / vectorX;
            double arc = Math.atan(tanXY);
            // degree是正数，相当于正切在四个象限的角度的绝对值
            double degree = Math.abs(arc / Math.PI * 180);
            // 将degree换算为对应x正轴开始的0-360的角度
            if (vectorY < 0 && vectorX > 0) {
                // 右下 0-90
                resultDegree = degree;
            } else if (vectorY < 0 && vectorX < 0) {
                // 左下 90-180
                resultDegree = 180 - degree;
            } else if (vectorY > 0 && vectorX < 0) {
                // 左上 180-270
                resultDegree = 180 + degree;
            } else {
                // 右上 270-360
                resultDegree = 360 - degree;
            }
        }

        return (int) resultDegree;
    }

    /**
     * 计算指定区域中可放置的最大正方形区域。
     * @param region 指定的区域
     * @param squareRect 正方形区域，将在原区域中居中
     * @return squareRect, or new RectF if squareRect is null.
     */
    public static RectF calcMaxSquareRect(RectF region, RectF squareRect) {
        if (squareRect == null) squareRect = new RectF();
        if (region == null) return squareRect;

        float w = region.width();
        float h = region.height();
        if (w == h) {
            squareRect.set(region);
        } else if (w > h) {
            float padding = (w - h) / 2;
            squareRect.set(region);
            squareRect.inset(padding, 0);
        } else { // (w < h)
            float padding = (h - w) / 2;
            squareRect.set(region);
            squareRect.inset(0, padding);
        }
        return squareRect;
    }

    /**
     * 将角度变换为0-360度。
     * @param angle 原角度
     * @return 0-360之间的等效角度
     */
    public static float clampAngle(float angle) {
        return ((angle % 360) + 360) % 360;
    }

    /**
     * 返回给定值在区间[min, max]上的最近值。
     */
    public static float clamp(float value, float min, float max) {
        if (min > max) {
            min = min + max;
            max = min - max;
            min = min - max;
        }
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * 计算点(x1, y1)和(x2, y2)之间的距离。
     */
    public static float calcDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
