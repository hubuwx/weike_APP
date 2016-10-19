package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * 保护详情页面
 */
public class ProtectInfoActivity extends Activity {
    private TextView tv_protect_num;
    private ImageView iv_protect_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_info);

        tv_protect_num = (TextView)findViewById(R.id.tv_protect_num);
        iv_protect_lock = (ImageView)findViewById(R.id.iv_protect_lock);

        // 获取安全号码
        String safeNum = SpUtils.getInstance(this).get(SpUtils.SAFE_NUM, null);
        if(safeNum!= null) {
            tv_protect_num.setText(safeNum);
        }
        // 获取是否保护完成状态
        boolean protect = SpUtils.getInstance(this).get(SpUtils.PROTECT, false);
        if(protect) {
            iv_protect_lock.setImageResource(R.drawable.lock);
        }
    }

    public void startSetUp(View v) {
        Intent intent = new Intent(ProtectInfoActivity.this, SetUp1Activity.class);
        startActivity(intent);
    }
}
