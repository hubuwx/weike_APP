package com.chenluwei.beijingnews.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.R;

/**
 * Created by lw on 2016/5/19.
 * 四个菜单页面的基类
 */
public abstract class MenuDetailBasePager  {
    /**
     * 代表各个页面
     */
    public View rootView;
    public Context context;



    public MenuDetailBasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    /**
     * 当孩子需要初始化数据的时候，重写该方法，并且在适当的时候调用
     */
    public void initData(){

    }
}
