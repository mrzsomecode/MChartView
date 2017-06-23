package com.zxz.chartview.chart.path;

import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2017/6/23.
 */

public abstract class BasePath extends Path {
    //弧线边
    public static final int ARC = 1 << 1;
    //直线边
    public static final int LINE = 1 << 2;

    @IntDef({ARC, LINE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RegioPath {
    }

    float angle;
    float startX = 0;
    float startY = 0;
    float centerY = 0;
    float centerX = 0;

    BasePath() {
        super();
    }

    BasePath(float centerX, float centerY, float angle) {
        super();
        this.centerX = centerX;
        this.centerY = centerY;
        this.angle = angle;
    }

    public void draw(float x, float y, int position, int maxCount, Canvas canvas) {
        if (position == 1) {
            startX = x;
            startY = y;
            moveTo(x, y);
        } else {
            line2XY(x, y);
        }
        if (position == maxCount) {
            line2Start(x, y);
        }
    }

    protected abstract void line2Start(float x, float y);

    public abstract void line2XY(float x, float y);
}
