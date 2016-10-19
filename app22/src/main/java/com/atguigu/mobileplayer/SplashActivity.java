package com.atguigu.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class SplashActivity extends Activity {


    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler = new Handler();

    private boolean isStartMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //延迟二秒进入主页面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //主线程
                Log.e(TAG,"当前线程的名称："+Thread.currentThread().getName());
                startMainActivity();
            }
        }, 2000);
    }

    private void startMainActivity() {
        if(!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //关闭启动页面
            finish();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent："+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);//把所有的消息和回调移除
    }
}
