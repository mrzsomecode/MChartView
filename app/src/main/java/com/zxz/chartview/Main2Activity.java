package com.zxz.chartview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zxz.chartview.chart.LineChartView;
import com.zxz.chartview.chart.PieGraphView;
import com.zxz.chartview.chart.bean.ChartBean;
import com.zxz.chartview.chart.bean.PieBean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Main2Activity extends Activity {
    //柱状图 大的item 可配置，超出屏幕可滑动
    private String[] titles1 = {"2017", "2016", "2015"};
    //最小一个，最大理论无限个,柱状图小item,多张雷达图覆盖对比
    private String[] titles2 = {"iphone", "android", "windows"};
    public PieGraphView mPie;
    public LineChartView mLine;
    EditText value;

    private int[] radoColors = {R.color.rado_color1, R.color.rado_color2, R.color.selectLeftColor};
    private int[][] chartColors = {{R.color.leftColor, R.color.selectLeftColor},
            {R.color.rightColor, R.color.selectRightColor},
            {R.color.colorPrimary, R.color.colorPrimaryDark}};
    private LinkedList<ChartBean> lineDatas;
    private LinkedList<PieBean> pieDatas;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mPie = (PieGraphView) findViewById(R.id.pieView);
        mLine = (LineChartView) findViewById(R.id.lineView);
        value = (EditText) findViewById(R.id.value);
        initData();
    }

    public void click(View v) {
        initData();
    }

    private void initData() {
        lineDatas = new LinkedList<>();
        pieDatas = new LinkedList<>();
        int data = Integer.parseInt(value.getText().toString());
        Random random = new Random(data);
        int[] datas = {60, 30, 10};
        //折线图
        for (int i = 0; i < 1; i++) {
            ArrayList<ChartBean> child = new ArrayList<>();
            for (int j = 0; j < 50; j++) {
                child.add(new ChartBean(j + "", chartColors[1][0], chartColors[1][1], random.nextInt(data + 20)));
            }
            lineDatas.add(new ChartBean("2016", child));
        }
        for (int i = 0; i < 1; i++) {
            ArrayList<PieBean> child = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                child.add(new PieBean(j + "全部提交", chartColors[j][0], chartColors[j][1], random.nextInt(100)));
            }
            pieDatas.add(new PieBean("2016", child));
        }
        mLine.setDatas(lineDatas);
        mPie.setDatas(pieDatas);
    }
}
