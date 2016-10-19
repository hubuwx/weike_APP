package com.atguigu.ms.service;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.ms.R;

public class LockScreenActivity extends AppCompatActivity {
    ImageView iv_lock_app_icon;
    TextView tv_lock_app_name;
    EditText et_lock_pwd;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        //得到空间对象
        iv_lock_app_icon = (ImageView)findViewById(R.id.iv_lock_app_icon);
        tv_lock_app_name = (TextView)findViewById(R.id.tv_lock_app_name);
        et_lock_pwd = (EditText)findViewById(R.id.et_lock_pwd);

        //得到传来的包名
        Intent intent = getIntent();
        mPackageName = intent.getStringExtra("packageName");

        PackageManager manager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(mPackageName, 0);
            Drawable icon = applicationInfo.loadIcon(manager);
            String name = applicationInfo.loadLabel(manager).toString();

            iv_lock_app_icon.setImageDrawable(icon);
            tv_lock_app_name.setText(name);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

    //判断密码是否正确
    public void confirm(View v) {
        String pWd = et_lock_pwd.getText().toString().trim();
        if("123".equals(pWd)) {
            finish();

            Intent intent = new Intent(this,AppLockService.class);
            intent.putExtra("packageName",mPackageName);
            startService(intent);
        }else {
            Toast.makeText(LockScreenActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
        }

    }

    //防止按返回键退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            return  true;
        }
        return super.onKeyUp(keyCode, event);
    }


}
