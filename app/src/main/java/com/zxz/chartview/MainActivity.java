package com.zxz.chartview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zxz.chartview.chart.ChartBean;
import com.zxz.chartview.chart.MChartView;
import com.zxz.chartview.chart.RadoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private String[] titles1 = {"2017", "2016", "2015"};
    private String[] titles2 = {"iphone", "android"};
    public String[] bottomExplainStr = {"格兰芬多", "赫奇帕奇", "拉文克劳", "斯莱特林", "中国大陆"};
    private int[] radoColors = {R.color.rado_color1, R.color.rado_color2};
    private int[][] chartColors = {{R.color.leftColor, R.color.selectLeftColor}, {R.color.rightColor, R.color.selectRightColor}};
    private List<ChartBean> datas = new ArrayList<>();
    private List<ChartBean> radoDatas = new ArrayList<>();
    MChartView mChartView;
    RadoView mRadoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChartView = (MChartView) findViewById(R.id.chart_view);
        mRadoView = (RadoView) findViewById(R.id.rado_view);
        mRadoView.setBottomExplainStr(bottomExplainStr);
    }

    public void click(View v) {
        testData();
    }

    public void testData() {
        int defLeftColor = chartColors[0][0];
        int defLeftSelectedColor = chartColors[0][1];
        int defRightColor = chartColors[1][0];
        int defRightSelectedColor = chartColors[1][1];
        datas.clear();
        radoDatas.clear();
        Random random = new Random();
        for (int i = 0; i < titles1.length; i++) {
            ArrayList<ChartBean> child = new ArrayList<>();
            child.add(new ChartBean(titles2[0], defLeftColor, defLeftSelectedColor, random.nextInt(1000)));
//            child.add(new ChartBean(titles2[1], defRightColor, defRightSelectedColor, random.nextInt(1000)));
            datas.add(new ChartBean(titles1[i], child));
        }
        for (int i = 0; i < radoColors.length; i++) {
            ArrayList<ChartBean> radoChild = new ArrayList<>();
            for (int j = 1; j <= bottomExplainStr.length; j++) {
                radoChild.add(new ChartBean("" + j, 0, 0, random.nextInt(1000)));
            }
            radoDatas.add(new ChartBean(titles2[i], radoColors[i], radoChild));
        }
        mChartView.setDatas(datas);
        mRadoView.setDatas(radoDatas);
    }
}
