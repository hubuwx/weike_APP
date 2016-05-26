package com.chenluwei.weike.base;

import android.app.Activity;
import android.view.View;

/**
 * Created by lw on 2016/5/3.
 */
public abstract class MenuDetialBasePager {
    public Activity activity;
    /**
     * 根据View
     */
    public View rootView;

    public MenuDetialBasePager(Activity activity) {
        this.activity = activity;
        rootView = initView();
    }

    /**
     * 让孩子强制实现这个方法，实现特有的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当孩子需要初始化数据的时候调用这个方法
     */
    public void initData(){

    }

}
