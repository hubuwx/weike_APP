package com.atguigu.ms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.atguigu.ms.R;

// 设置页面1
public class SetUp1Activity extends Activity {
    private TextView tv_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up1);

        // 初始化标题
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("1.欢迎使用手机防盗");

    }

    // 下一步
    public void next(View v) {
        Intent intent = new Intent(SetUp1Activity.this, SetUp2Activity.class);
        startActivity(intent);
        finish();
        // 平移动画
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }
}
