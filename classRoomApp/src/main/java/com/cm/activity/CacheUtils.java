package com.cm.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheUtils {
	
	
	/**
	 * ����
	 */
	private static final String NAME = "atguigu";
	private static SharedPreferences sp;

	/**
	 * �ж��Ƿ��Ѿ����뵽��ҳ��
	 * �õ�һ��boolean����
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
	 * ��¼�Ƿ�������ҳ��
	 * ����boolean����
	 * @param context ������
	 * @param key ��
	 * @param values ֵ
	 */
	public static void putBoolean(Context context ,String key ,boolean values){
		if(sp ==null){
			sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, values).commit();
	}
	
	

}