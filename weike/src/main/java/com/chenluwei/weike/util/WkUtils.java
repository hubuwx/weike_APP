package com.chenluwei.weike.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by lw on 2016/3/27.
 * 该应用的通用工具类
 */
public class WkUtils {

    //获取联网状态的方法
    public static Boolean getIsConncet(Context context) {
        boolean connected = false;
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取active的NetworkInfo对象
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if(activeNetworkInfo != null) {
            connected = activeNetworkInfo.isConnected();
        }

        Log.i("Q11","__"+connected);

        return connected;


    }
}
