package com.chenluwei.weike.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Message;
import android.util.Log;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

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


    /**
     * 判断该地址是否是网络资源
     * http,mms,rtsp,http.m3u8
     * @param uri
     * @return
     */
    public static boolean isNetUri(String uri) {
        boolean result = false;
        if(uri != null) {
            if(uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("mms") || uri.toLowerCase().startsWith("rtsp")) {
                result = true;
            }
        }
        return result;
    }


    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    /**
     * 获取当前网速的方法
     * 这个方法一定要一秒钟调用一次
     */
    public  String getNetSpeed(Context context) {
        String result = "0kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB

        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        result = speed+"kb/s";
       return result;
    }


}
