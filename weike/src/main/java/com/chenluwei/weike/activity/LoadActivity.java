package com.chenluwei.weike.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.MyUser;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.WkUtils;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class LoadActivity extends Activity implements View.OnClickListener {
    EditText et_username, et_password;
    Button btn_login;
    TextView btn_register;
    /**
     * 用来监听注册完成的广播
     */
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (TextView) findViewById(R.id.btn_register);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_register) {
            Intent intent = new Intent(LoadActivity.this,
                    RegisterActivity.class);
            startActivity(intent);
        } else {
            boolean isNetConnected = WkUtils.getIsConncet(this);
            if(!isNetConnected){
               Toast.makeText(LoadActivity.this, "当前网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }
            //登录
            login();
        }
    }

    /**
     * 登录
     */
    private void login() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(LoadActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoadActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(
                this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final MyUser user = new MyUser();
        user.setUsername(name);
        user.setPassword(password);
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(LoadActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                //finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                Toast.makeText(LoadActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Contants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
                //监听到注册完成的广播则关掉此登录页面
                finish();
            }
        }

    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
