package com.cm.activity;

import android.content.Context;

import com.miebo.utils.SystemUtils;

public class AppConstant {
	// ����ǲ����ֻ�����,����IP��ַ��Ҫ�޸ĳɵ��Ա���IP
	
	public static String defaultIP = "192.168.1.107:8080/ClassRoomAppService/";

	public static String getRootUrl(Context context) {
		
		if (SystemUtils.isEmulator()) {
			return "http://10.0.2.2:8080/ClassRoomAppService/";// ģ��������,�̶��Ǹõ�ַ,��Ҫ�޸�!!!
			
		} else {
			return "http://" + defaultIP;
			//10.0.2.2
		}
	}

	/**
	 * ��ȡ�����Url servlet Ŀ¼
	 * 
	 * @param context
	 * @return
	 */
	public static String getUrl(Context context) {
		return getRootUrl(context) + "servlet/";
	}

}
