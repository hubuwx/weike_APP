package com.atguigu.mobileplayer;

import android.app.Application;

import org.xutils.x;

/**
 * 作者：杨光福 on 2016/4/23 15:29
 * 微信：yangguangfu520
 * QQ号：541433511
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
