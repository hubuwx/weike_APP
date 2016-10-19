package com.atguigu.ms.activity;

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

import com.atguigu.ms.R;
import com.atguigu.ms.receiver.MyAdminReceiver;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

public class SetUp4Activity extends Activity {
    private TextView tv_setup4_active;
    private CheckBox cb_setup4_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up4);

        // 获取对象
        tv_setup4_active = (TextView)findViewById(R.id.tv_setup4_active);
        cb_setup4_protect = (CheckBox)findViewById(R.id.cb_setup4_protect);

        // 回显
        boolean isProtect = SpUtils.getInstance(this).get(SpUtils.PROTECT,false);
        if(isProtect) {
            cb_setup4_protect.setText("已开启保护");
            cb_setup4_protect.setTextColor(Color.BLACK);
            cb_setup4_protect.setChecked(true);
        }

        // 监听处理
        cb_setup4_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_setup4_protect.setText("已开启保护");
                    cb_setup4_protect.setTextColor(Color.BLACK);

                    // 保存
                    SpUtils.getInstance(SetUp4Activity.this).save(SpUtils.PROTECT, true);
                } else {
                    cb_setup4_protect.setText("未开启保护");
                    cb_setup4_protect.setTextColor(Color.RED);

                    // 保存
                    SpUtils.getInstance(SetUp4Activity.this).save(SpUtils.PROTECT, false);
                }
            }
        });
        
        // 激活处理
        boolean isActive = isActive();
        if(isActive) {
            tv_setup4_active.setText("已经激活");
            tv_setup4_active.setTextColor(Color.BLACK);
        }
    }


    // 上一步
    public void previous(View v) {
        Intent intent = new Intent(SetUp4Activity.this, SetUp3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    // 下一步
    public void confirm(View v) {
        // 激活处理
        boolean isActive = isActive();
        if(isActive) {
            Intent intent = new Intent(SetUp4Activity.this, ProtectInfoActivity.class);
            startActivity(intent);
            finish();

            // 平移动画
            overridePendingTransition(R.anim.right_in, R.anim.left_out);

            // 保存配置完成
            SpUtils.getInstance(this).save(SpUtils.COMPLETE,true);

        }else {
            // 启动激活界面
            ComponentName componentName = new ComponentName(this, MyAdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(SetUp4Activity.this, ProtectInfoActivity.class);
            startActivity(intent);
            finish();

            // 平移动画
            overridePendingTransition(R.anim.right_in, R.anim.left_out);

            // 保存配置完成
            SpUtils.getInstance(this).save(SpUtils.COMPLETE, true);

        }else if(requestCode == 1 && resultCode == RESULT_CANCELED) {
            MsUtils.showMsg(SetUp4Activity.this,"必须激活");
        }
    }

    // 应用是否激活
    private boolean isActive() {
        DevicePolicyManager manager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName name = new ComponentName(this, MyAdminReceiver.class);
        return manager.isAdminActive(name);
    }



}
