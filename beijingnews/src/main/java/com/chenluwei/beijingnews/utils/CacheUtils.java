package com.chenluwei.beijingnews.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：杨光福 on 2016/5/3 14:13
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：保存软件的参数
 */
public class CacheUtils {
    /**
     * 保存软件参数
     * @param context
     * @param key
     * @param values
     */
    public static void putBoolean(Context context, String key, boolean values) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,values).commit();

    }

    /**
     * 得到软件保存的参数
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getBoolean(key,false);
    }

    /**
     * 保存String类型的数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context, String key, String values) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putString(key,values).commit();

    }

    /**
     * 得到缓存数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);

        return sp.getString(key,"");
    }
}
