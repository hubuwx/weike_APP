package com.atguigu.ms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.adapter.MainAdapter;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

// 主页面
public class MainActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gv_main;
//    private SharedPreferences mSp;
    private AlertDialog mSetAlertDialog;
    private AlertDialog mLoginAlertDialog;

    private int WHAT_2s_EXIT = 1;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){

            if(isFinishing()) {
                return;
            }

            if(msg.what == WHAT_2s_EXIT) {
                isExit = false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取控件对象
        gv_main = (GridView) findViewById(R.id.gv_main);

        // 获取sp对象
//        mSp = getSharedPreferences("ms", Context.MODE_PRIVATE);


        //处理gridview
        MainAdapter mMainAdapter = new MainAdapter(this);
        gv_main.setAdapter(mMainAdapter);

        // item的长按点击事件处理
        gv_main.setOnItemLongClickListener(this);

        // item 的点击事件
        gv_main.setOnItemClickListener(this);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        // 修改名称的处理
        if (position == 0) {
            // 获取当前item的名称
            final TextView tv = (TextView) view.findViewById(R.id.tv_item_name);
            String name = tv.getText().toString().trim();

            // 准备一个EditText
            final EditText editText = new EditText(MainActivity.this);
            editText.setHint(name);
            // 准备一个对话框
            new AlertDialog.Builder(this)
                    .setTitle("修改名称")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取edittext输入名称
                            String newName = editText.getText().toString().trim();

                            // 更新页面
                            tv.setText(newName);
                            // 持久化到存储
//                            mSp.edit().putString("name", newName).commit();
                            SpUtils.getInstance(MainActivity.this).save(SpUtils.NANE,newName);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:// 手机防盗
                // 显示设置密码或登录对话框
                showSetOrLoginDialog();
                break;
            case 1:
                intent = new Intent(MainActivity.this, BlackNumActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MainActivity.this, AppManagerActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, TrafficManagerActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(this, ProcessManagerActivity.class);
                startActivity(intent);
                break;
            case 5:
                break;
            case 6:
                intent = new Intent(this, CacheCleanActivity.class);
                startActivity(intent);
                break;
            case 7:// 高级工具
                intent = new Intent(MainActivity.this, ToolActivity.class);
                startActivity(intent);
                break;
            case 8://设置中心
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    // 显示设置密码或登录对话框
    private void showSetOrLoginDialog() {
        // 获取保存的密码
//        String password = mSp.getString("password", null);
        String password = SpUtils.getInstance(this).get(SpUtils.PASSWORD,null);
        if (password == null) {
            // 显示设置密码对话框
            showSetDialog();
        } else {
            // 显示登录密码对话框
            showLoginDialog();
        }
    }

    private EditText et_login_pwd1;
    private Button bt_login_confirm;
    private Button bt_login_cancel;
    // 显示登录密码对话框
    private void showLoginDialog() {
        // 准备一个view视图
        View view = View.inflate(this, R.layout.dialog_login_pwd, null);

        // 获取控件对象
        et_login_pwd1 = (EditText) view.findViewById(R.id.et_login_pwd1);
        bt_login_confirm = (Button) view.findViewById(R.id.bt_login_confirm);
        bt_login_cancel = (Button) view.findViewById(R.id.bt_login_cancel);

        // 监听按钮处理
        bt_login_confirm.setOnClickListener(this);
        bt_login_cancel.setOnClickListener(this);

        // 创建一个对话框
        mLoginAlertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .show();
    }

    private EditText et_set_pwd1;
    private EditText et_set_pwd2;
    private Button bt_set_confirm;
    private Button bt_set_cancel;

    // 显示设置密码对话框
    private void showSetDialog() {
        // 准备一个view视图
        View view = View.inflate(this, R.layout.dialog_set_pwd, null);

        // 获取控件对象
        et_set_pwd1 = (EditText) view.findViewById(R.id.et_set_pwd1);
        et_set_pwd2 = (EditText) view.findViewById(R.id.et_set_pwd2);
        bt_set_confirm = (Button) view.findViewById(R.id.bt_set_confirm);
        bt_set_cancel = (Button) view.findViewById(R.id.bt_set_cancel);

        // 监听按钮处理
        bt_set_confirm.setOnClickListener(this);
        bt_set_cancel.setOnClickListener(this);

        // 创建一个对话框
        mSetAlertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_set_confirm:// 设置密码的按钮确定
                // 获取输入的密码 和确认密码
                String pwd1 = et_set_pwd1.getText().toString().trim();
                String pwd2 = et_set_pwd2.getText().toString().trim();
                // 校验密码不能为空
                if("".equals(pwd1)) {
                    // 提示
                    MsUtils.showMsg(MainActivity.this,"输入的密码不能为空");
                    return;
                }
                // 两次密码必须一致校验
                if(!pwd1.equals(pwd2)) {
                    // 提示
                    MsUtils.showMsg(MainActivity.this,"两次输入的密码不一致");
                    return;
                }

                // 保存密码
//                mSp.edit().putString("password",pwd1).commit();
                SpUtils.getInstance(MainActivity.this).save(SpUtils.PASSWORD,MsUtils.md5(pwd1));

                // 销毁对话框
                mSetAlertDialog.dismiss();
                break;

            case R.id.bt_set_cancel:// 设置密码的按钮取消
                mSetAlertDialog.dismiss();
                break;

            case R.id.bt_login_confirm:// 登录确定按钮处理
                // 获取输入的密码
                String pwd = et_login_pwd1.getText().toString().trim();
                // 获取保存过的密码
//                String savePwd = mSp.getString("password", null);
                String savePwd =  SpUtils.getInstance(MainActivity.this).get(SpUtils.PASSWORD, null);

                Log.e("pwd",pwd);
                Log.e("savePwd",savePwd);
                String temp = MsUtils.md5("你好帅");
                temp = MsUtils.md5(temp);

                Log.e("nihao",temp);


                // 校验两个密码是否一致
                if(!MsUtils.md5(pwd).equals(savePwd)) {
                    // 不一致  提示密码错误
                    MsUtils.showMsg(MainActivity.this,"输入密码错误");
                    return;
                }else {
                    // 一致       进入防盗流程
                    toProtect();
                }

                // 取消对话框
                mLoginAlertDialog.dismiss();

                break;
            case R.id.bt_login_cancel://取消按钮登录的
                mLoginAlertDialog.dismiss();
                break;
        }
    }

    // 进入防盗保护流程
    private void toProtect() {
        // 获取是否设置完成防盗保护标记
        boolean isComplete = SpUtils.getInstance(this).get(SpUtils.COMPLETE,false);
        if(isComplete) {
            Intent intent = new Intent(MainActivity.this, ProtectInfoActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(MainActivity.this, SetUp1Activity.class);
            startActivity(intent);
        }
    }


    private  boolean isExit = false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {

            if(!isExit) {
                // 修改标记
                isExit = true;

                // 提示再按一次退出
                MsUtils.showMsg(MainActivity.this,"再按一次退出");

                // 发消息
                handler.sendEmptyMessageDelayed(WHAT_2s_EXIT,2000);

                return true;
            }

        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
    }
}
