package com.chenluwei.weike.net;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.bean.UpdataInfo;
import com.chenluwei.weike.util.Contants;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by lw on 2016/3/27.
 *网络连接相关的工具类
 */
public class APIClient {


    /**
     * 在本工程中联网有两种情况：
     * 1.连接本地TomCat，用来测试和学习
     * 2.连接真实的云服务器Bmob
     * 因为两者使用的SDK不同，故通过一个boolean来判断进行哪种连接
     *
     */
    private static boolean isConnectBmob = true;
    private static HttpURLConnection mConn;
    private static InputStream mIs;
    private static UpdataInfo mUpdataInfo;
    private static String mUrlStirng;

    //输入流，因为在方法内部会冲突所有提出来


    //连接服务器获取Json数据并解析为Bean类的方法
    //****这里注意用Bmob时需要不同的方法，这里待解决***
    public static UpdataInfo getUpDataInfo(Context context) throws Exception{
        return isConnectBmob?getUpdataInfo_Bmob(context):getUpdataInfo_TomCat(context);
    }

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private static UpdataInfo getUpdataInfo_Bmob(final Context context) {
        final UpdataInfo[] updataInfo = new UpdataInfo[1];
        // 初始化 Bmob SDK
        // 第二个参数Application ID是我在Bmob服务器端创建的Application ID
        Bmob.initialize(context, Contants.BMOBID);

        BmobQuery<FileInfo> query = new BmobQuery<FileInfo>();
        query.getObject(context, Contants.BMOB_JSON, new GetListener<FileInfo>() {
            @Override
            public void onSuccess(final FileInfo fileInfo) {

                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        System.out.println(Thread.currentThread().getName());

                        BmobFile file = fileInfo.getFile();
                        Log.e("TAG", "BmobFile:" + file);
                        mUrlStirng = file.getFileUrl(context);
                        Log.e("TAG", "onSuccess:" + mUrlStirng);

                        try {
                            URL url = new URL(mUrlStirng);
                            mConn = (HttpURLConnection) url.openConnection();
                            mConn.setConnectTimeout(5000);
                            mConn.setReadTimeout(5000);
                            mConn.connect();
                            int code = mConn.getResponseCode();
                            if (code == 200) {
                                mIs = mConn.getInputStream();
                                Gson gson = new Gson();
                                updataInfo[0] = gson.fromJson(new InputStreamReader(mIs), UpdataInfo.class);


                            } else {
                                throw new RuntimeException();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (mConn != null) {
                                mConn.disconnect();
                            }

                            if (mIs != null) {
                                try {
                                    mIs.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }.start();



            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("TAG", "updataInfo[0]:" +updataInfo[0] );
        return updataInfo[0];
    }

    @Nullable
    private static UpdataInfo getUpdataInfo_TomCat(Context context) {
        mConn = null;
        mIs = null;
        mUpdataInfo = null;
        mUrlStirng = null;
        try {
            if(isConnectBmob) {
            BmobChange.getBmobJsonUrl(context, mUrlStirng);
            }
            else {
                mUrlStirng = Contants.UPDATE_JSON_XML;
            }
            Log.e("TAG", "URL为" + mUrlStirng);
            URL url = new URL(Contants.UPDATE_JSON_XML);
            mConn = (HttpURLConnection) url.openConnection();
            mConn.setConnectTimeout(5000);
            mConn.setReadTimeout(5000);
            mConn.connect();
            int code = mConn.getResponseCode();
            if(code == 200) {
                mIs = mConn.getInputStream();
                Gson gson = new Gson();
                mUpdataInfo = gson.fromJson(new InputStreamReader(mIs, "utf-8"), UpdataInfo.class);
            }else{
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(mConn != null) {
                mConn.disconnect();
            }

            if(mIs != null) {
                try {
                    mIs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  mUpdataInfo;
    }


    //联网下载最新版本的工具方法
    //***使用Bmob需要使用另外的方法***有待完成
    public static void download(File apkFile,ProgressDialog pd,String apkUrl) throws MalformedURLException {
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;

        URL url = new URL(apkUrl);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            if(conn.getResponseCode() == 200) {

                pd.setMax(conn.getContentLength());
                is = conn.getInputStream();
                fos = new FileOutputStream(apkFile);
                int len;
                byte []buff = new byte[2048];
                while((len = is.read(buff)) != -1) {
                    fos.write(buff,0,len);
                    pd.incrementProgressBy(len);
                    SystemClock.sleep(5);
                }
            }else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(conn != null) {
                conn.disconnect();
            }
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
