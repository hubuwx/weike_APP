package com.atguigu.ms.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.atguigu.ms.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

// 程序锁服务
public class AppLockService extends Service {
    private boolean flag = true;
    private List<String> mLockList = new ArrayList<>();
    private List<String> mUnlockList = new ArrayList<>();
    private ActivityManager mAm;

    public AppLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //为了调用获取栈顶activity的方法，要创建一个manager
        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //获取所有加锁dao
        AppLockDao dao = new AppLockDao(this);
        //放到list里
        mLockList = dao.get();
        new Thread(){
            public void run(){
                while(flag) {
                    final String topPackageName = getTopPackageName();
                    if(mLockList.contains(topPackageName) && !mUnlockList.contains(topPackageName)) {
                        startLockScreen(topPackageName);
                    }

                    SystemClock.sleep(300);
                }
            }
        }.start();
        Log.e("TAG", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String packageName = intent.getStringExtra("packageName");

        if(mUnlockList != null) {
            mUnlockList.add(packageName);
        }

        //当管理锁的界面发生改变时通过以下代码改变服务
        String addPackageName = intent.getStringExtra("add");
        if(mLockList != null) {
          mLockList.add(addPackageName);
        }

        String deletePackageName = intent.getStringExtra("delete");
        if(mLockList != null) {
            mLockList.remove(deletePackageName);
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void startLockScreen(String topPackageName) {
        Intent intent = new Intent(this, LockScreenActivity.class);
        //需要显示应用名和图片，要把包名传过去
        intent.putExtra("packageName",topPackageName);
// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        Log.e("TAG", "onDestroy");
    }
    
    //获取顶部包名的方法
    private String getTopPackageName() {

        return mAm.getRunningTasks(1).get(0).topActivity.getPackageName();
    }
}
