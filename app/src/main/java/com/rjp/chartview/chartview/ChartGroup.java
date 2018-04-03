package com.rjp.chartview.chartview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.rjp.chartview.R;


/**
 * author : Gimpo create on 2018/4/2 17:27
 * email  : jimbo922@163.com
 */

public class ChartGroup extends RelativeLayout {
    public ChartGroup(Context context) {
        this(context, null);
    }

    public ChartGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_chart_group, this);
    }
}
