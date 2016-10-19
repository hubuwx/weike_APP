package com.atguigu.ms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.service.AddressService;
import com.atguigu.ms.service.AppLockService;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

// 设置中心页面
public class SettingActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_set_style;
    private TextView tv_set_style;
    private String[] mItems;
    private int mStyleIndex;
    private RelativeLayout rl_set_address;
    private TextView tv_set_address;
    private CheckBox cb_set_address;
    private TextView tv_set_location;
    private RelativeLayout rl_set_lock;
    private TextView tv_set_lock;
    private CheckBox cb_set_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 获取控件对象
        // 程序锁
        rl_set_lock = (RelativeLayout)findViewById(R.id.rl_set_lock);
        tv_set_lock = (TextView)findViewById(R.id.tv_set_lock);
        cb_set_lock = (CheckBox)findViewById(R.id.cb_set_lock);

        // 样式
        rl_set_style = (RelativeLayout)findViewById(R.id.rl_set_style);
        tv_set_style = (TextView)findViewById(R.id.tv_set_style);

        // 归属地服务
        rl_set_address = (RelativeLayout)findViewById(R.id.rl_set_address);
        tv_set_address = (TextView)findViewById(R.id.tv_set_address);
        cb_set_address = (CheckBox)findViewById(R.id.cb_set_address);

        // 来电显示位置设置
        tv_set_location = (TextView)findViewById(R.id.tv_set_location);

        // 获取数组资源
        mItems = getResources().getStringArray(R.array.StyleSlt);

        // 回显
        mStyleIndex = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX,0);
        tv_set_style.setText(mItems[mStyleIndex]);

        // 回显归属服务
        boolean isServiceRunning = MsUtils.isServiceRunning(this, AddressService.class.getName());
        if(isServiceRunning) {
            tv_set_address.setText("归属地服务开启");
            tv_set_address.setTextColor(Color.BLACK);
            cb_set_address.setChecked(true);
        }

        // 回显程序锁服务
       isServiceRunning = MsUtils.isServiceRunning(this, AppLockService.class.getName());
        if(isServiceRunning) {
            tv_set_lock.setText("程序锁服务开启");
            tv_set_lock.setTextColor(Color.BLACK);
            cb_set_lock.setChecked(true);
        }


        // 设置监听
        rl_set_style.setOnClickListener(this);
        rl_set_address.setOnClickListener(this);
        tv_set_location.setOnClickListener(this);
        rl_set_lock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_set_style:// 风格
                showStyleDialog();
                break;
            case R.id.rl_set_address:// 归属地服务处理
                addressService();
                break;
            case  R.id.tv_set_location:
                Intent intent = new Intent(SettingActivity.this, AddressSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_set_lock:// 程序锁
                lockService();
                break;
        }
    }
    // 程序锁服务处理
    private void lockService() {
        if(cb_set_lock.isChecked()) {
            tv_set_lock.setText("程序锁服务关闭");
            tv_set_lock.setTextColor(Color.RED);
            cb_set_lock.setChecked(false);
            // 关闭服务
            Intent intent = new Intent(this,AppLockService.class);
            stopService(intent);

        }else {
            tv_set_lock.setText("程序锁服务开启");
            tv_set_lock.setTextColor(Color.BLACK);
            cb_set_lock.setChecked(true);

            // 开启服务
            Intent intent = new Intent(this,AppLockService.class);
            startService(intent);
        }
    }

    // 开关服务处理
    private void addressService() {
        if(cb_set_address.isChecked()) {
            tv_set_address.setText("归属地服务关闭");
            tv_set_address.setTextColor(Color.RED);
            cb_set_address.setChecked(false);
            // 关闭服务
            Intent intent = new Intent(this,AddressService.class);
            stopService(intent);

        }else {
            tv_set_address.setText("归属地服务开启");
            tv_set_address.setTextColor(Color.BLACK);
            cb_set_address.setChecked(true);

            // 开启服务
            Intent intent = new Intent(this,AddressService.class);
            startService(intent);
        }
    }

    // 设置样式
    private void showStyleDialog() {
        new AlertDialog.Builder(this)
                    .setTitle("选择样式")
                    .setSingleChoiceItems(mItems, mStyleIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 内存变化
                            mStyleIndex = which;
                            // 页面变化
                            tv_set_style.setText(mItems[mStyleIndex]);
                            dialog.dismiss();
                            // 存储变化
                            SpUtils.getInstance(SettingActivity.this).save(SpUtils.STYLE_INDEX,mStyleIndex);
                        }
                    })
                    .show();
    }
}
