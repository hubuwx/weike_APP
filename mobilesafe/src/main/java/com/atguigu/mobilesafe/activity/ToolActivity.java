package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.SmsUtils;

// 高级工具页面
public class ToolActivity extends Activity implements View.OnClickListener {
    private TextView tv_tool_lock;
    private TextView tv_tool_address;
    private TextView tv_tool_num;
    private TextView tv_tool_sms_back;
    private TextView tv_tool_sms_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        // 获取控件对象
        tv_tool_lock = (TextView)findViewById(R.id.tv_tool_lock);
        tv_tool_address = (TextView)findViewById(R.id.tv_tool_address);
        tv_tool_num = (TextView)findViewById(R.id.tv_tool_num);
        tv_tool_sms_back = (TextView)findViewById(R.id.tv_tool_sms_back);
        tv_tool_sms_save = (TextView)findViewById(R.id.tv_tool_sms_save);

        // 设置点击事件
        tv_tool_lock.setOnClickListener(this);
        tv_tool_address.setOnClickListener(this);
        tv_tool_num.setOnClickListener(this);
        tv_tool_sms_back.setOnClickListener(this);
        tv_tool_sms_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()){
            case R.id.tv_tool_lock://程序锁
                intent = new Intent(ToolActivity.this, AppLockActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_address:// 查询号码归属地
                intent = new Intent(ToolActivity.this, QueryAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_num://常用联系人查询
                intent = new Intent(this, CommonNumbersActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_sms_back://还原
                SmsUtils.restore(this);
                break;
            case R.id.tv_tool_sms_save://保存备份
                SmsUtils.backup(this);
                break;
        }
    }
}
