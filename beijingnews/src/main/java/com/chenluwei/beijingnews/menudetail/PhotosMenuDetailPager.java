package com.chenluwei.beijingnews.menudetail;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.chenluwei.beijingnews.base.MenuDetailBasePager;

/**
 * Created by lw on 2016/5/19.
 * 图组菜单详情页面
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager {
    public PhotosMenuDetailPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        //设置内容
        TextView textView = new TextView(context);
        textView.setText("图组菜单详情页面");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        return textView;
    }
}
