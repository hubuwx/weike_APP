package com.atguigu.mobilesafe.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by miao on 2016/3/15.
 * sp操作类
 * 1 保存
 * 2 获取
 * 3 移除
 */
public class SpUtils {
    public static final String CONFIG = "config";
    public static final String SIM_NUM = "sim_num";
    public static final String SAFE_NUM = "safe_num";
    public static final String PROTECT = "protect";
    public static final String UP_LEFT = "up_left";
    public static final String UP_TOP = "up_top";
    public static final String STYLE_INDEX = "style_index";
    private  static SpUtils spUtils = spUtils = new SpUtils();;
    private static SharedPreferences sp = null;

    public static SpUtils getInstance(Context context){
        if(sp == null) {
            sp = context.getSharedPreferences("ms",Context.MODE_PRIVATE);
        }

        return spUtils;
    }

    // 保存
    public void save(String key, Object value){
        if(value instanceof  String) {
            sp.edit().putString(key, (String) value).commit();
        } else if(value instanceof  Boolean){
            sp.edit().putBoolean(key, (Boolean) value).commit();
        } else if(value instanceof  Integer) {
            sp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 获取
    public String getString(String key, String defValue){
        return  sp.getString(key,defValue);
    }

    public int getInt(String key, int defValue){
        return  sp.getInt(key, defValue);
    }


    public boolean getBoolean(String key, boolean defValue){
        return sp.getBoolean(key, defValue);
    }

    public <T> T get(String key, Object defValue){
        T t = null;

        if(defValue == null || defValue instanceof String) {
            t = (T) sp.getString(key, (String) defValue);
        } else if(defValue instanceof  Integer) {
            Integer in = sp.getInt(key, (Integer) defValue);
            t = (T) in;
        } else if(defValue instanceof  Boolean) {
            Boolean b = sp.getBoolean(key, (Boolean) defValue);
            t = (T) b;
        }

        return t;
    }

    // 移除
    public void remove(String key){
        sp.edit().remove(key).commit();
    }
}
