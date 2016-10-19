package com.cm.activity;

import android.content.Context;

import com.miebo.utils.SystemUtils;

public class AppConstant {
	// 如果是采用手机调试,下面IP地址需要修改成电脑本地IP
	
	public static String defaultIP = "192.168.1.107:8080/ClassRoomAppService/";

	public static String getRootUrl(Context context) {
		
		if (SystemUtils.isEmulator()) {
			return "http://10.0.2.2:8080/ClassRoomAppService/";// 模拟器调试,固定是该地址,不要修改!!!
			
		} else {
			return "http://" + defaultIP;
			//10.0.2.2
		}
	}

	/**
	 * 获取服务端Url servlet 目录
	 * 
	 * @param context
	 * @return
	 */
	public static String getUrl(Context context) {
		return getRootUrl(context) + "servlet/";
	}

}
