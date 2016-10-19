package com.chenluwei.weike.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 2016/3/26.
 * sp工具类
 * 保存
 * 获取
 * 移除
 */
public class SpUtils {
    public static final String NANE = "name";
    public static final String PASSWORD = "password";
    public static final String COMPLETE = "complete";
    public static final String SIM_NUM = "simNum";
    public static final String SAFE_NUM = "safeNum";
    public static final String PROTECT = "protect";
    public static final String STYLE_INDEX = "styleIndex";
    public static final String UPLEFT = "upleft";
    public static final String UPTOP = "uptop";
    public static final String SHORT_CUT = "shortCut";
    public static final String ENTERMAIN = "enterMain";
    private static SpUtils spUtils = new SpUtils();
    private static SharedPreferences mSp;

    public static SpUtils getInstance(Context context){

        if(mSp == null) {
            mSp = context.getSharedPreferences("ms", Context.MODE_PRIVATE);
        }

        return spUtils;
    }


    //保存
    public void save(String key,Object value){

        if(value instanceof  String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if(value instanceof  Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }else if(value instanceof  Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 获取
    public String getString(String key, String defValue){
        return  mSp.getString(key,defValue);
    }
    public int getInt(String key, int defValue){
        return  mSp.getInt(key, defValue);
    }
    public boolean getBoolean(String key, boolean defValue){
        return  mSp.getBoolean(key, defValue);
    }

    public <T> T get(String key, T defValue){
        T t = null;

        if( defValue == null || defValue instanceof  String) {
            t = (T) mSp.getString(key,(String)defValue);
        } else if(defValue instanceof  Boolean) {
            Boolean b = mSp.getBoolean(key, (Boolean) defValue);

            t = (T) b;
        }else if(defValue instanceof  Integer) {
            Integer i = mSp.getInt(key, (Integer) defValue);
            t = (T)i;
        }

        return t;
    }

    // 移除
    public  void remove(String key){
        mSp.edit().remove(key).commit();
    }
}

