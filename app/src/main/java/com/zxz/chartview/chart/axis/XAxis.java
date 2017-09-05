package com.zxz.chartview.chart.axis;

/**
 * Created by zxz on 2017/8/15.
 */

public class XAxis extends BaseAxis {
    /**
     * the position of the x-labels relative to the chart
     */
    private XAxisPosition mPosition = XAxisPosition.TOP;

    /**
     * enum for the position of the x-labels relative to the chart
     */
    public enum XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }
}
