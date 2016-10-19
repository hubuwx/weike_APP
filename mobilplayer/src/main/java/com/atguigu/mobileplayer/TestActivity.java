package com.atguigu.mobileplayer;

import android.app.Activity;
import android.os.Bundle;

/**
 * 作者：杨光福 on 2016/4/22 09:26
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：xxxx
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        System.out.println("TestActivity--onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("TestActivity--onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("TestActivity--onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("TestActivity--onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("TestActivity--onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("TestActivity--onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("TestActivity--onDestroy");
    }
}
