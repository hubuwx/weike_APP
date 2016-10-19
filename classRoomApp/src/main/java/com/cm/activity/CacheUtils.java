package com.cm.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheUtils {
	
	
	/**
	 * 名字
	 */
	private static final String NAME = "atguigu";
	private static SharedPreferences sp;

	/**
	 * 判断是否已经进入到主页面
	 * 得到一个boolean类型
	 * @param context
	 * @param key
	 * @param def
	 * @return
	 */
	public static boolean getBoolean(Context context ,String key ,boolean defValue){
		if(sp ==null){
			sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
		
		return sp.getBoolean(key, defValue);
	}
	
	
	/**
	 * 记录是否进入过主页面
	 * 保存boolean类型
	 * @param context 上下文
	 * @param key 键
	 * @param values 值
	 */
	public static void putBoolean(Context context ,String key ,boolean values){
		if(sp ==null){
			sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, values).commit();
	}
	
	

}