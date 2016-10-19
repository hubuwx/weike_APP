package com.atguigu.mobilesafe.net;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.util.Xml;

import com.atguigu.mobilesafe.bean.UpdateInfo;
import com.atguigu.mobilesafe.util.Contants;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miao on 2016/3/15.
 */
public class APIClicent {

    public static UpdateInfo getUpdateInfo() throws Exception {
        UpdateInfo info = null;
//        info = getUpdateByXML();
        info = getUpdateByJson();
        return info;
    }

    private static UpdateInfo getUpdateByJson() throws Exception{
        UpdateInfo info = null;
        HttpURLConnection conn = null;
        InputStream is = null;

        try{
            // 1 创建URL
            URL url = new URL(Contants.UPDATE_URL_JSON);
            conn = (HttpURLConnection) url.openConnection();
            // 2 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 3 获取连接
            conn.connect();
            // 4 获取响应码
            int code = conn.getResponseCode();
            if(code == 200) {
                // 5 获取输入流
                is = conn.getInputStream();
                // 6 JSON解析
                info = new Gson().fromJson(new InputStreamReader(is,"utf-8"), UpdateInfo.class);

            }else {
                throw  new RuntimeException("请求网络失败");
            }
        }finally {
            // 7 关闭资源
            if(conn !=null) {
                conn.disconnect();
            }
            if(is != null) {
                is.close();
            }
        }
        return info;
    }

    private static UpdateInfo getUpdateByXML() throws Exception {
        UpdateInfo info = new UpdateInfo();
        HttpURLConnection conn = null;
        InputStream is = null;

        try{
            // 1 创建URL
            URL url = new URL(Contants.UPDATE_URL_XML);
            conn = (HttpURLConnection) url.openConnection();
            // 2 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 3 获取连接
            conn.connect();
            // 4 获取响应码
            int code = conn.getResponseCode();
            if(code == 200) {
                // 5 获取输入流
                is = conn.getInputStream();

                /**
                 * <?xml version="1.0" encoding="utf-8"?>
                 <update>
                 <version>1.2</version>
                 <apkUrl>http://192.168.0.42:8080/ms.apk</apkUrl>
                 <desc>解决了xxxbug,优化了xxx</desc>
                 </update>
                 */
                // 6 XML 解析数据
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setInput(is, "utf-8");
                int eventType = xmlPullParser.getEventType();
                while (eventType != xmlPullParser.END_DOCUMENT){
                    if(eventType == xmlPullParser.START_TAG) {

                        String tagName = xmlPullParser.getName();
                        if("version".equals(tagName)) {
                            info.setVersion(xmlPullParser.nextText());
                        }else if("apkUrl".equals(tagName)) {
                            info.setApkUrl(xmlPullParser.nextText());
                        }else if("desc".equals(tagName)) {
                            info.setDesc(xmlPullParser.nextText());
                            break;
                        }
                    }
                    eventType = xmlPullParser.next();
                }
            }else {
                throw  new RuntimeException("请求网络失败");
            }
        }finally {
            // 7 关闭资源
            if(conn !=null) {
                conn.disconnect();
            }
            if(is != null) {
                is.close();
            }
        }

        return info;
    }


    public static void downloadAPK(ProgressDialog mPb, File file, String apkUrl) throws Exception{
        HttpURLConnection conn = null;
        InputStream is = null;

        try{
            // 1 创建URL
            URL url = new URL(apkUrl);
            conn = (HttpURLConnection) url.openConnection();
            // 2 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 3 获取连接
            conn.connect();
            // 4 获取响应码
            int code = conn.getResponseCode();
            if(code == 200) {
                mPb.setMax(conn.getContentLength());
                // 5 获取输入流
                is = conn.getInputStream();

                // 创建输出流
                FileOutputStream os = new FileOutputStream(file);

                // 边读边写
                byte[] bufffer = new byte[2048];
                int len = -1;
                while((len = is.read(bufffer)) != -1) {
                    os.write(bufffer,0,len);
                    mPb.incrementProgressBy(len);
                    SystemClock.sleep(3);
                }
            }else {
                throw  new RuntimeException("请求网络失败");
            }
        }finally {
            // 7 关闭资源
            if(conn !=null) {
                conn.disconnect();
            }
            if(is != null) {
                is.close();
            }
        }
    }
}
