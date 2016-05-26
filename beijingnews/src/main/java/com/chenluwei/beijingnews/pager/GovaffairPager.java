package com.chenluwei.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.chenluwei.beijingnews.base.BasePager;

/**
 * Created by lw on 2016/5/18.
 */
public class GovaffairPager extends BasePager{
    public GovaffairPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        //设置标题
        tv_title.setText("政要");
        //设置内容
        TextView textView = new TextView(context);
        textView.setText("政要指南内容");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        //将子视图添加到FramLayout中
        fl_base_content.addView(textView);
    }
}
