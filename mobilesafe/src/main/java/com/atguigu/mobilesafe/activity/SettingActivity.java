package com.atguigu.mobilesafe.activity;

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

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.service.AddressService;
import com.atguigu.mobilesafe.service.AppLockService;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

// 设置中心页面
public class SettingActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_setting_address;
    private TextView tv_setting_address;
    private CheckBox cb_setting_address;

    private RelativeLayout rl_setting_style;
    private TextView tv_setting_style;

    private TextView tv_setting_location;

    private RelativeLayout rl_setting_lock;
    private TextView tv_setting_lock;
    private CheckBox cb_setting_lock;
    private String[] mNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 归属地服务
        rl_setting_address = (RelativeLayout) findViewById(R.id.rl_setting_address);
        tv_setting_address = (TextView) findViewById(R.id.tv_setting_address);
        cb_setting_address = (CheckBox) findViewById(R.id.cb_setting_address);

        // 归属地风格
        rl_setting_style = (RelativeLayout) findViewById(R.id.rl_setting_style);
        tv_setting_style = (TextView) findViewById(R.id.tv_setting_style);

        // 归属地位置
        tv_setting_location = (TextView) findViewById(R.id.tv_setting_location);

        // 程序锁
        rl_setting_lock = (RelativeLayout) findViewById(R.id.rl_setting_lock);
        tv_setting_lock = (TextView) findViewById(R.id.tv_setting_lock);
        cb_setting_lock = (CheckBox) findViewById(R.id.cb_setting_lock);

        // 初始化数据
        mNames = getResources().getStringArray(R.array.style_array);

        // 回显归属地样式设置
        // 从储存中取数据内存
        mStyleIndex = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX, 0);
        // 更新显示
        tv_setting_style.setText(mNames[mStyleIndex]);

        // 归属地服务回显
        boolean isServiceRunning = MsUtils.isServiceRunning(this, AddressService.class.getName());
        if (isServiceRunning) {
            cb_setting_address.setChecked(true);
            tv_setting_address.setText("归属地服务开启");
            tv_setting_address.setTextColor(Color.BLACK);
        }

        // 程序锁服务回显
        isServiceRunning = MsUtils.isServiceRunning(this, AppLockService.class.getName());
        if (isServiceRunning) {
            cb_setting_lock.setChecked(true);
            tv_setting_lock.setText("程序锁服务开启");
            tv_setting_lock.setTextColor(Color.BLACK);
        }

        // 监听点击事件
        rl_setting_style.setOnClickListener(this);
        tv_setting_location.setOnClickListener(this);
        rl_setting_address.setOnClickListener(this);
        rl_setting_lock.setOnClickListener(this);
    }

    // 监听回调
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_style://归属地风格
                showSetStyleDialog();
                break;

            case R.id.tv_setting_location://归属地位置
                Intent intent = new Intent(SettingActivity.this, AddressSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_setting_address://归属地服务
                startOrStopAddressService();
                break;
            case R.id.rl_setting_lock://程序锁服务
                startOrStopAppLockService();
                break;

        }
    }

    // 程序锁服务处理
    private void startOrStopAppLockService() {

        if (cb_setting_lock.isChecked()) {
            cb_setting_lock.setChecked(false);
            tv_setting_lock.setText("程序锁服务关闭");
            tv_setting_lock.setTextColor(Color.RED);
            // 停止服务
            Intent intent = new Intent(SettingActivity.this, AppLockService.class);
            stopService(intent);
        } else {
            cb_setting_lock.setChecked(true);
            tv_setting_lock.setText("程序锁服务开启");
            tv_setting_lock.setTextColor(Color.BLACK);
            // 开启服务
            Intent intent = new Intent(SettingActivity.this, AppLockService.class);
            startService(intent);
        }
    }

    private int mStyleIndex;

    /**
     * 归属地样式设置对话框
     */
    private void showSetStyleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("选择样式")
                .setSingleChoiceItems(mNames, mStyleIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 更新内存
                        mStyleIndex = which;// which下标
                        // 更新显示
                        tv_setting_style.setText(mNames[mStyleIndex]);
                        dialog.dismiss();
                        // 更新存储
                        SpUtils.getInstance(SettingActivity.this).save(SpUtils.STYLE_INDEX, mStyleIndex);

                    }
                })
                .show();
    }

    // 开启或者停止服务
    private void startOrStopAddressService() {

        if (cb_setting_address.isChecked()) {
            cb_setting_address.setChecked(false);
            tv_setting_address.setText("归属地服务关闭");
            tv_setting_address.setTextColor(Color.RED);
            // 停止服务
            Intent intent = new Intent(SettingActivity.this, AddressService.class);
            stopService(intent);
        } else {
            cb_setting_address.setChecked(true);
            tv_setting_address.setText("归属地服务开启");
            tv_setting_address.setTextColor(Color.BLACK);
            // 开启服务
            Intent intent = new Intent(SettingActivity.this, AddressService.class);
            startService(intent);
        }
    }

}
