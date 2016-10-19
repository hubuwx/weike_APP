package com.chenluwei.weike.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.MyUser;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.WkUtils;

import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity {
    Button btn_register;
    EditText et_username, et_password, et_pwd_again,et_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_pwd_again = (EditText) findViewById(R.id.et_pwd_again);
        et_email = (EditText) findViewById(R.id.et_email);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                register();
            }
        });
    }

    /**
     * 注册用户
     */
    private void register() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_pwd_again.getText().toString();
        String email = et_email.getText().toString();

        if (TextUtils.isEmpty(name)) {
           Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
           Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd_again)) {
            Toast.makeText(RegisterActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd_again.equals(password)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNetConnected = WkUtils.getIsConncet(this);
        if(!isNetConnected){
            Toast.makeText(RegisterActivity.this, "当前网络不可用，请检查您的网络", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final MyUser user = new MyUser();
        user.setUsername(name);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                //发广播通知登陆页面退出
                sendBroadcast(new Intent(Contants.ACTION_REGISTER_SUCCESS_FINISH));
                // 启动主页
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("user", user);

                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
