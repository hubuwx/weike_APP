package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.service.AppLockService;
import com.atguigu.mobilesafe.util.MsUtils;

/**
 * 锁屏页面
 */
public class LockScreenActivity extends Activity implements View.OnClickListener {
    private Button bt_lock_confirm;
    private EditText et_lock_pwd;
    private ImageView iv_lock_icon;
    private TextView tv_lock_name;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        //  获取控件对象
        bt_lock_confirm = (Button)findViewById(R.id.bt_lock_confirm);
        et_lock_pwd = (EditText)findViewById(R.id.et_lock_pwd);
        iv_lock_icon = (ImageView)findViewById(R.id.iv_lock_icon);
        tv_lock_name = (TextView)findViewById(R.id.tv_lock_name);

        bt_lock_confirm.setOnClickListener(this);

        // 获取传递过来的包名
        packageName = getIntent().getStringExtra("packageName");
        // 根据包名获取图标
        PackageManager packageManager = getPackageManager();
        try {
            // 获取APP信息
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            Drawable icon = applicationInfo.loadIcon(packageManager);
            String appName = applicationInfo.loadLabel(packageManager).toString();

            // 设置APP显示
            iv_lock_icon.setImageDrawable(icon);
            tv_lock_name.setText(appName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        String pwd = et_lock_pwd.getText().toString();

        if("123".equals(pwd)) {
            finish();

            // 通知服务不要锁定当前这个应用
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("packageName",packageName);
            startService(intent);

        }else {
            MsUtils.showMsg(LockScreenActivity.this, "密码不正确");
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return true;//点击back不退出当前界面
        }

        return super.onKeyUp(keyCode, event);
    }
}
