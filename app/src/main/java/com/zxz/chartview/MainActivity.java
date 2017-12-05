package com.zxz.chartview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zxz.chartview.chart.LineChartView;
import com.zxz.chartview.chart.MChartView;
import com.zxz.chartview.chart.RadoView;
import com.zxz.chartview.chart.bean.ChartBean;
import com.zxz.chartview.chart.path.BasePath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    //柱状图 大的item 可配置，超出屏幕可滑动
    private String[] titles1 = {"2017", "2016", "2015"};
    //最小一个，最大理论无限个,柱状图小item,多张雷达图覆盖对比
    private String[] titles2 = {"iphone", "android", "windows"};
    //雷达图维度，动态，可配置
    public String[] bottomExplainStr = {"格兰芬多", "赫奇帕奇", "拉文克劳", "斯莱特林", "中国大陆"};
    //颜色集合，必须跟 titles2 数量保持一致
    private int[] radoColors = {R.color.rado_color1, R.color.rado_color2, R.color.selectLeftColor};
    private int[][] chartColors = {{R.color.leftColor, R.color.selectLeftColor},
            {R.color.rightColor, R.color.selectRightColor},
            {R.color.colorPrimary, R.color.colorPrimaryDark}};
    private List<ChartBean> datas = new ArrayList<>();
    private List<ChartBean> lineDatas = new ArrayList<>();
    private List<ChartBean> radoDatas = new ArrayList<>();
    MChartView mChartView;
    RadoView mRadoView;
    private static final String TAG = "MainActivity";
    private EditText value;
    public LineChartView mLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChartView = (MChartView) findViewById(R.id.chart_view);
        mLineChart = (LineChartView) findViewById(R.id.lineChart);
        mRadoView = (RadoView) findViewById(R.id.rado_view);
        value = (EditText) findViewById(R.id.value);
        mLineChart.setAnimationDuration(5000);
        mRadoView.setBottomExplainStr(bottomExplainStr);
        mRadoView.showTopDescribe(true);
        mRadoView.showBottomDescribe(true);
        mRadoView.setShowRatio(true);
//        mRadoView.setRegioPath(BasePath.LINE);
        mRadoView.setRegioPath(BasePath.ARC);
    }

    public void click(View v) {
        testData();
    }

    /**
     * 柱状图和雷达图均支持多个图形
     */
    public void testData() {
        datas.clear();
        radoDatas.clear();
        lineDatas.clear();
        int data = Integer.parseInt(value.getText().toString());
        Random random = new Random(data);
        //折线图
        for (int i = 0; i < 1; i++) {
            ArrayList<ChartBean> child = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                child.add(new ChartBean(j + "", chartColors[1][0], chartColors[1][1], random.nextInt(data + 20)));
            }
            lineDatas.add(new ChartBean("2016", child));
        }
        //柱状图
        for (int i = 0; i < titles1.length; i++) {
            ArrayList<ChartBean> child = new ArrayList<>();
            for (int j = 0; j < titles2.length; j++) {
                child.add(new ChartBean(titles2[j], chartColors[j][0], chartColors[j][1], data));
            }
            datas.add(new ChartBean(titles1[i], child));
        }
        //雷达图
        for (int i = 0; i < titles2.length; i++) {
            ArrayList<ChartBean> radoChild = new ArrayList<>();
            for (int j = 1; j <= bottomExplainStr.length; j++) {
                radoChild.add(new ChartBean("" + j, 0, 0, random.nextInt(data + 20)));
            }
            radoDatas.add(new ChartBean(titles2[i], radoColors[i], radoChild));
        }
        mLineChart.setDatas(lineDatas);
        mChartView.setDatas(datas);
        mRadoView.setDatas(radoDatas);
    }
}
