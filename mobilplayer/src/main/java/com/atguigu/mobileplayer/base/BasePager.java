package com.atguigu.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 作者：杨光福 on 2016/4/20 10:36
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：本地视频，本地音乐，网络视频，网络音乐基类或者说公共类
 */
public abstract class BasePager {

    /**
     * 上下文
     */
    public Context context;
    /**
     * 标识是否数据被初始化
     */
    public  boolean isInitData = false;

    /**
     * 实例化各个孩子的布局存储在这个字段上
     */
    public View rootView;
    public BasePager(Context context){
        isInitData = false;
        this.context = context;
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
