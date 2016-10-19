package com.atguigu.ms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.util.SmsUtils;

import org.w3c.dom.Text;

// 高级工具
public class ToolActivity extends Activity implements View.OnClickListener {
    private TextView tv_tool_adress;
    private  TextView tv_tool_lock;
    private TextView tv_tool_common_num;
    private TextView tv_tool_storage;
    private TextView tv_tool_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        // 获取控件对象
        tv_tool_adress = (TextView)findViewById(R.id.tv_tool_adress);
        tv_tool_lock = (TextView)findViewById(R.id.tv_tool_lock);
        tv_tool_common_num = (TextView)findViewById(R.id.tv_tool_common_num);
        tv_tool_storage = (TextView)findViewById(R.id.tv_tool_storage);
        tv_tool_back = (TextView) findViewById(R.id.tv_tool_back);

        // 监听事件
        tv_tool_adress.setOnClickListener(this);
        tv_tool_lock.setOnClickListener(this);
        tv_tool_common_num.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.tv_tool_adress:// 查询归属地
                intent = new Intent(ToolActivity.this, QueryAdressActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_lock:// 程序锁
               intent = new Intent(ToolActivity.this, AppLockAcitivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_common_num://常用电话
                intent = new Intent(ToolActivity.this,CommonNumbersAcitivity.class);
                startActivity(intent);
                break;
            case R.id.tv_tool_storage://备份短信
                SmsUtils.restore(this);
                break;
            case R.id.tv_tool_back:
                SmsUtils.backup(this);
                break;


        }
    }
}
