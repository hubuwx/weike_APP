package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.atguigu.mobilesafe.R;

/**
 * 设置页面1
 */
public class SetUp1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up1);
    }

    public void next(View v) {
        Intent intent = new Intent(SetUp1Activity.this,SetUp2Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
        finish();
    }
}
