package com.chenluwei.weike.base;

import android.content.Context;
import android.view.View;

/**
 * Created by lw on 2016/4/20.
 * 作用：各个中间窗体的基类
 */
public abstract class BasePager {
    /**
     * 上下文
     */
    public final Context context;
    /**
     * 实例化各个孩子的布局存储在这个字段上
     */
    public View rootView;
    //判断该页面是否已经被初始化过一次
    public boolean isInitData;
    public BasePager(Context context){
        this.context = context;
        isInitData = false;
        rootView = initView();//各个页面的实例
    }

    /**
     * 由孩子去实现，加载不同的布局，或者是不同的UI
     * @return
     */
    public abstract View initView();

    /**
     * 当孩子需要联网请求的时候，或者初始化数据的时候重写这个方法
     */
    public void initData(){

    }

}
