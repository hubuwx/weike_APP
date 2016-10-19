package com.atguigu.mobileplayer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：杨光福 on 2016/4/23 16:58
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：缓存工具类
 */
public class CacheUtils {

    /**
     * 缓存数据
     *
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context, String key, String values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, values).commit();


    }

    /**
     * 得到数据
     *
     * @param context
     * @param Key
     * @return
     */
    public static String getString(Context context, String Key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");
    }
}
