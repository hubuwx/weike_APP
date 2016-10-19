package com.chenluwei.weike.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chenluwei.weike.base.BasePager;

/**
 * Created by lw on 2016/4/20.
 */
public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("网络音频页面的数据被初始化了...");
        textView.setText("网络音频页面的内容");
    }
}
