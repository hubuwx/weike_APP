package com.atguigu.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.atguigu.mobilesafe.activity.LockScreenActivity;
import com.atguigu.mobilesafe.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

public class AppLockService extends Service {

    private List<String> lockAppInfo;
    private ActivityManager am;
    private List<String> unLockPackageNames = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private boolean flag = true;

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        AppLockDao appLockDao = new AppLockDao(this);
        lockAppInfo = appLockDao.getAll();

        new Thread(){
            public void run(){
                while (flag){
                    // 获取栈顶启动的app包名
                    String name = getTopPackageName();
                    if(lockAppInfo.contains(name) && !unLockPackageNames.contains(name)) {
                        // 启动程序锁
                        startLock(name);
                    }

                    //休息一会
                    SystemClock.sleep(200);
                }
            }
        }.start();
    }

    // 启动程序锁页面
    private void startLock(String name) {
        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName",name);
        startActivity(intent);
    }

    // 获取栈顶应用程序的包名
    private String getTopPackageName() {
        return  am.getRunningTasks(1).get(0).topActivity.getPackageName();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Tag","app_lock_ondestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String packageName = intent.getStringExtra("packageName");
        if(packageName !=null ) {
            unLockPackageNames.add(packageName);
        }



        return super.onStartCommand(intent, flags, startId);
    }
}
