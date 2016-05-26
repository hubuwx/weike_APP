package com.chenluwei.weike.pager;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.chenluwei.weike.R;
import com.chenluwei.weike.base.MenuDetialBasePager;


import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by lw on 2016/5/3.
 */
public class MenuDetailPager extends MenuDetialBasePager{


    @ViewInject(R.id.pager)
    private ViewPager pager;

    public MenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        View view = View.inflate(activity, R.layout.new_menu_detail,null);
        x.view().inject(this,view);//依赖注入，把view注入到xUtils框架里
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }
}
