package com.chenluwei.weike.pager;

import android.app.Activity;
import android.content.Context;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.adapter.MainAdapter;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.base.MenuDetialBasePager;


import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 对应的页签页面
 * Created by lw on 2016/4/21.
 *
 */
public class MainPager extends MenuDetialBasePager{


    private MainAdapter adapter;

    @ViewInject(R.id.lv_home)
    private ListView lv_home;
    @ViewInject(R.id.vp_top)
    private ViewPager vp_top;
    @ViewInject(R.id.tv_top_des)
    private TextView tv_top_des;
    @ViewInject(R.id.ll_top_point)
    private LinearLayout ll_top_point;

    public MainPager(Activity activity) {
        super(activity);

    }

    @Override
    public View initView() {
        View view = View.inflate(activity, R.layout.main_pager, null);
        x.view().inject(this, view);
       // ViewUtils.inject(this,view);//把当前view和Xutils工具绑定
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }
}
