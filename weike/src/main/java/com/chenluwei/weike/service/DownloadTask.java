package com.chenluwei.weike.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.bean.ThreadInfo;
import com.chenluwei.weike.db.ThreadDAO;
import com.chenluwei.weike.db.ThreadDaoImpl;
import com.chenluwei.weike.util.SpUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lw on 2016/5/8.
 * 下载任务类
 */
public class DownloadTask {
    private Context mContext = null;
    private DownloadFileInfo mFileInfo = null;
    private ThreadDAO mDao = null;
    private int mFinished = 0;
    private int mThreadCount = 1;//线程数量
    public boolean isPause = false;
    public boolean isDestrory = false;
    private List<DownloadThread> mThreadList = null;
    //线程池
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();



    public DownloadTask(Context mContext, DownloadFileInfo mFileInfo,int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        mDao = new ThreadDaoImpl(mContext);
    }

    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());

        if(threads.size() == 0) {
            //获得每个线程下载的长度
            int length = mFileInfo.getLength()/mThreadCount;
            //初始化每个线程信息对象
            for (int i = 0;i < mThreadCount;i++){
                ThreadInfo threadInfo = new ThreadInfo(i,mFileInfo.getUrl(),length*i,(i+1)*length-1,0);
                if(i == mThreadCount-1) {
                    //最后一段可能有除不尽的情况，就直接设置成总长度为结束位置
                    threadInfo.setEnd(mFileInfo.getLength());
                }
                //添加到线程信息集合中
                threads.add(threadInfo);
                //向数据库里插入线程信息(改了一下，上面一句判断过了)
                //如果目前数据库中没有下载这个文件的线程，则插入这个线程

                    mDao.insertThread(threadInfo,mFileInfo,0);

            }

        }
        mThreadList = new ArrayList<DownloadThread>();
        //启动多个线程进行下载
        for (ThreadInfo info:threads){
            DownloadThread thread = new DownloadThread(info);
            // thread.start();
            //用线程池来启动线程
            DownloadTask.sExecutorService.execute(thread);
            //添加下载线程到集合中，这里便于管理，比如暂停
            mThreadList.add(thread);
        }

    }

    /**
     * 判断是否所有线程都执行完毕
     * 标识为同步的，即同一时间只有一个线程调用此方法
     */
    private synchronized void checkAllThreadsFinished(){
        boolean allFinished = true;
        for (DownloadThread thread:mThreadList){
            //遍历线程集合，判断线程是否都执行完毕
            if(!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if(allFinished) {
            //全部下载完后，删除线程信息
            mDao.deleteThread(mFileInfo.getUrl());
            int fileId = SpUtils.getInstance(mContext).getInt("fileId",0);
            fileId--;
            SpUtils.getInstance(mContext).save("fileId",fileId);
            //全部执行完了，发送一个广播通知这个下载结束
            Intent intent = new Intent(DownloadService.ACTION_FINISH);
            intent.putExtra("fileInfo",mFileInfo);
            mContext.sendBroadcast(intent);
        }
    }


    /**
     * 下载线程
     */
    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo = null;
        public  boolean isFinished = false;//标示线程是否执行完毕
        public DownloadThread(ThreadInfo mThreadInfo){
            this.mThreadInfo = mThreadInfo;
        }
        
        public void run(){

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
               conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
            //设置下载位置
                //???????
                int start = (mThreadInfo.getStart()+mThreadInfo.getFinished());
                Log.i("downloadstart", "start---"+start);
                Log.i("downloadstart", "mThreadInfo.getStart()---"+mThreadInfo.getStart());

                Log.i("download", "start-----" + start);
                conn.setRequestProperty("Range", "bytes="+start+"-"+mThreadInfo.getEnd());
                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH,mFileInfo.getFileName());
                 raf = new RandomAccessFile(file,"rwd");
                //设置文件开始写入的位置
                raf.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                //??????????
                mFinished += mThreadInfo.getFinished();
                Log.i("downloadfinish", "mFinished------"+mFinished);
                //开始下载
                Log.i("download", "getResponseCode"+conn.getResponseCode());
                if(conn.getResponseCode() == 206) {
                    Log.i("download", "200进来了！！！！");
                    //1.读取数据
                    input = conn.getInputStream();
                    byte[] buffer = new byte[1024 << 2];
                    int len = -1;
                    //long time = System.currentTimeMillis();
                    while ((len = input.read(buffer))!=-1){
                        //2.写入文件
                        raf.write(buffer, 0, len);
                        //4.把下载进度发送广播给Activity
                        //累加整个文件的完成进度
                        mFinished += len;
                        //累加每个线程完成的进度
                        mThreadInfo.setFinished(mThreadInfo.getFinished()+len);
                       // if(System.currentTimeMillis() - time > 500) {
                        Log.i("download", "mFinished-----------"+mFinished);
                            Log.i("downloadzong", "mFileInfo.getLength()========"+mFileInfo.getLength());

                            int progress = (int) ((long)mFinished*100/mFileInfo.getLength());
                            Log.i("downloadbaifenbi", "百分比--------"+progress);
                            intent.putExtra("finished",progress);
                            //同时要把文件的id发过去，用来对应进度条
                            //intent.putExtra("id",mFileInfo.getId());
                            intent.putExtra("id",mFileInfo.getId());
                            //发送广播更新进度条
                            mContext.sendBroadcast(intent);
                       // }
                        //3.下载暂停时，保存下载进度，这里改成每个线程的完成进度
                        if(isPause || isDestrory) {
                            mDao.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mThreadInfo.getFinished(),progress);
                            return;
                        }

                    }

                }
                //标示线程执行完毕
                isFinished = true;

                //检查下载任务是否执行完毕
                checkAllThreadsFinished();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                conn.disconnect();
                if(raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                try {
                    if(input != null) {

                    input.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
