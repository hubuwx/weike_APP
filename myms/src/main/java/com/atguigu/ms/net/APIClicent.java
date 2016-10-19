package com.atguigu.ms.net;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.util.Xml;

import com.atguigu.ms.bean.UpdateInfo;
import com.atguigu.ms.util.Contants;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2016/3/25.
 * 网络操作类
 */
public class APIClicent {

    // 获取更新详情
    public static UpdateInfo getUpdateInfo() throws Exception {
        UpdateInfo updateInfo = new UpdateInfo();

//        // xml方式获取更新详情
//        updateInfo = getUpdateByXml();
        updateInfo = getUpdateByJson();


        return updateInfo;
    }

    // json方式获取更新详情
    private static UpdateInfo getUpdateByJson() throws Exception {
        UpdateInfo updateInfo = new UpdateInfo();

        HttpURLConnection conn = null;
        InputStream is = null;

        try {
            // 获取URL
            URL url = new URL(Contants.UPDATE_JSON_URL);
            conn = (HttpURLConnection) url.openConnection();
            // 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 获取连接
            conn.connect();
            // 获取响应码
            int code = conn.getResponseCode();
            // 成功
            if (code == 200) {
                // 获取输入流
                is = conn.getInputStream();
                // json方式解析
                updateInfo = new Gson().fromJson(new InputStreamReader(is, "utf-8"), UpdateInfo.class);


            } else {
                // 失败
                throw new RuntimeException("请求失败");
            }

        } finally {
            // 关闭资源
            if (conn != null) {
                conn.disconnect();
            }

            if (is != null) {
                is.close();
            }
        }

        return updateInfo;
    }

    // xml方式获取更新详情
    private static UpdateInfo getUpdateByXml() throws Exception {
        UpdateInfo updateInfo = new UpdateInfo();

        HttpURLConnection conn = null;
        InputStream is = null;

        try {
            // 获取URL
            URL url = new URL(Contants.UPDATE_XML_URL);
            conn = (HttpURLConnection) url.openConnection();
            // 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 获取连接
            conn.connect();
            // 获取响应码
            int code = conn.getResponseCode();
            // 成功
            if (code == 200) {
                // 获取输入流
                is = conn.getInputStream();
                // xml方式解析
//            <update>
//            <version>1.2</version>
//            <apkUrl>http://192.168.56.1:8080/ms.apk</apkUrl>
//            <desc>解决了xxxbug,优化了xxx</desc>
//            </update>

                XmlPullParser xmlPullParser = Xml.newPullParser();

                xmlPullParser.setInput(is, "utf-8");

                int eventType = xmlPullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = xmlPullParser.getName();

                        if ("version".equals(tagName)) {
                            updateInfo.setVersion(xmlPullParser.nextText());
                        } else if ("apkUrl".equals(tagName)) {
                            updateInfo.setApkUrl(xmlPullParser.nextText());
                        } else if ("desc".equals(tagName)) {
                            updateInfo.setDesc(xmlPullParser.nextText());
                            break;
                        }
                    }

                    eventType = xmlPullParser.next();
                }
            } else {
                // 失败
                throw new RuntimeException("请求失败");
            }

        } finally {
            // 关闭资源
            if (conn != null) {
                conn.disconnect();
            }

            if (is != null) {
                is.close();
            }
        }

        return updateInfo;
    }

    // 下载apk
    public static void downloadApk(ProgressDialog mPb, File mFileApk, String apkUrl) throws Exception {

        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            // 获取URL
            URL url = new URL(apkUrl);
            conn = (HttpURLConnection) url.openConnection();
            // 设置参数
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // 获取连接
            conn.connect();
            // 获取响应码
            int code = conn.getResponseCode();
            // 成功
            if (code == 200) {
                // 设置进度条的最大值
                mPb.setMax(conn.getContentLength());

                // 获取输入流
                is = conn.getInputStream();
                // 创建输出流
                fos = new FileOutputStream(mFileApk);

                // 边读边写
                byte[] buffer = new byte[2048];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);

                    // 更新进度
                    mPb.incrementProgressBy(len);

                    SystemClock.sleep(1);
                }
            } else {
                // 失败
                throw new RuntimeException("请求失败");
            }

        } finally {
            // 关闭资源
            if (conn != null) {
                conn.disconnect();
            }

            if (fos != null) {
                fos.close();
            }

            if (is != null) {
                is.close();
            }
        }

    }
}
