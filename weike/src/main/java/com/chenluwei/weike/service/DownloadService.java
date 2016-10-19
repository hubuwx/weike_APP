package com.chenluwei.weike.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.bean.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lw on 2016/5/8.
 */
public class DownloadService extends Service {
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloads/";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final int MSG_INIT = 0;
    public static final String ACTION_FINISH = "ACTION_FINISH";
    //private DownloadTask mTask = null;
    //下载任务的集合
    private Map<Integer,DownloadTask> mTasks = new HashMap<>();
   // private ThreadInfo threadInfo = null;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_INIT:
                    DownloadFileInfo fileInfo = (DownloadFileInfo) msg.obj;
                    Log.i("Download", "fileInfo"+fileInfo.toString());
                    //启动下载任务
                    DownloadTask task = new DownloadTask(DownloadService.this,fileInfo,3);
                    task.download();
                    //把下载任务添加到集合中
                    mTasks.put(fileInfo.getId(),task);
                    break;
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得Activity传来的参数
        if(ACTION_START.equals(intent.getAction())) {
            DownloadFileInfo fileInfo = (DownloadFileInfo) intent.getSerializableExtra("fileInfo");
            //启动初始化线程
            Log.i("download", "service---"+fileInfo);
            InitThread initThread = new InitThread(fileInfo);
           // initThread.start();
            DownloadTask.sExecutorService.execute(initThread);
        }else if(ACTION_STOP.equals(intent.getAction())) {
            DownloadFileInfo fileInfo = (DownloadFileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("pause", "service---"+fileInfo);
            //从集合中取出下载任务
            DownloadTask task = mTasks.get(fileInfo.getId());
            if(task != null) {
                //停止下载任务
                task.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化的子线程
     */
    class InitThread extends  Thread{
        private DownloadFileInfo mFileInfo = null;

        public InitThread(DownloadFileInfo mFileInfo){
            this.mFileInfo = mFileInfo;
        }

        public void run(){
            Log.i("download", "run()");
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");//设置连接方式
                int length = -1;
                if(conn.getResponseCode() == 200) {
                    //获取文件长度
                    length = conn.getContentLength();
                }
                if(length <= 0) {
                    //文件长度<0说明有问题
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if(!dir.exists()) {
                    //如果不存在这个文件夹，则创建
                    dir.mkdir();
                }
                //获得文件长度
                //在本地创建文件
                File file = new File(dir,mFileInfo.getFileName());
                //这是一种特殊的输出流，它可以在任意位置对文件进行操作，也就是断点续传的核心
                raf = new RandomAccessFile(file,"rwd");
                //设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
              //把文件信息赋值长度后，发送handler
               handler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {if(raf!=null) {

                    raf.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }if(conn !=null) {

                    conn.disconnect();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mTask.isDestrory = true;
    }
}
