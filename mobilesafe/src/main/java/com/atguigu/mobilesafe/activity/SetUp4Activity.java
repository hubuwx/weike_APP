package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.receiver.MyDeviceAdminReceiver;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * 设置页面4
 */
public class SetUp4Activity extends Activity {
    private CheckBox cb_setup4;
    private TextView tv_setup4_active;
    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up4);

        cb_setup4 = (CheckBox)findViewById(R.id.cb_setup4);
        tv_setup4_active = (TextView)findViewById(R.id.tv_setup4_active);

        isActive = isActive();
        if(isActive) {
            tv_setup4_active.setText("防盗保护已经激活");
            tv_setup4_active.setTextColor(Color.BLACK);
        }

        // 设置SIM卡
        boolean protect = SpUtils.getInstance(this).getBoolean(SpUtils.PROTECT, false);

        if(protect) {
            cb_setup4.setChecked(true);
            cb_setup4.setTextColor(Color.BLACK);
            cb_setup4.setText("已经开启保护");
        } else {
            cb_setup4.setChecked(false);
            cb_setup4.setTextColor(Color.RED);
            cb_setup4.setText("手机没有开启保护");
        }

        // 监听SIM绑定
        cb_setup4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_setup4.setTextColor(Color.BLACK);
                    cb_setup4.setText("已经开启保护");
                    // 保存
                    SpUtils.getInstance(SetUp4Activity.this).save(SpUtils.PROTECT, true);
                } else {
                    cb_setup4.setTextColor(Color.RED);
                    cb_setup4.setText("手机没有开启保护");
                    SpUtils.getInstance(SetUp4Activity.this).remove(SpUtils.PROTECT);
                }
            }
        });
    }

    public void previous(View v) {

        Intent intent = new Intent(SetUp4Activity.this, SetUp3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
    public void confirm(View v) {
        // 判断是否已经激活
        if(isActive) {
            // 激活 进入防盗信息页面
            Intent intent = new Intent(SetUp4Activity.this, ProtectInfoActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }else {
            // 未激活 启动激活(带回调)
            ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            // 进入防盗信息详情页面
            Intent intent = new Intent(SetUp4Activity.this, ProtectInfoActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            //提示必须激活
            MsUtils.showMsg(SetUp4Activity.this,"必须激活防盗保护");
        }
    }

    private boolean isActive() {
        DevicePolicyManager manager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName name = new ComponentName(this, MyDeviceAdminReceiver.class);
        return manager.isAdminActive(name);
    }

}
