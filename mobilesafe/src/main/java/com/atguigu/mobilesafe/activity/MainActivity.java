package com.atguigu.mobilesafe.activity;

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

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.adapter.MainAdapter;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

// 主页面
public class MainActivity extends Activity implements AdapterView.OnItemLongClickListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int WHAT_EXIT = 1;
    private GridView gw_main;
    private static final String[] NAMES = new String[] {
            "手机防盗", "通讯卫士", "软件管理", "流量管理", "进程管理",
            "手机杀毒", "缓存清理", "高级工具", "设置中心" };

    private static final int[] ICONS = new int[] { R.drawable.widget01,
            R.drawable.widget02,R.drawable.widget03, R.drawable.widget04,
            R.drawable.widget05, R.drawable.widget06,R.drawable.widget07,
            R.drawable.widget08, R.drawable.widget09 };
    private MainAdapter mainAdapter;
//    private SharedPreferences sp;
    private EditText et_set_pwd1;
    private EditText et_set_pwd2;
    private Button bt_set_confim;
    private Button bt_set_cancel;
    private AlertDialog mAletDialog;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == WHAT_EXIT) {
                exitFlag = false;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        sp = getSharedPreferences("ms", Context.MODE_PRIVATE);

        // 获取布局中的view对象
        gw_main = (GridView)findViewById(R.id.gw_main);

        // 初始化gridview显示
        mainAdapter = new MainAdapter(MainActivity.this,NAMES,ICONS);
        gw_main.setAdapter(mainAdapter);

        // 修改名称
        gw_main.setOnItemLongClickListener(this);
        gw_main.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0) {
            // 获取名称
            final TextView tv = (TextView) view.findViewById(R.id.tv_item_main);
            String name = tv.getText().toString().trim();
            // 准备一个editText 回显
            final EditText editText = new EditText(MainActivity.this);
            editText.setHint(name);
            // 创建一个对话框
            new AlertDialog.Builder(this)
                        .setTitle("修改名称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入的名称
                                String newName = editText.getText().toString().trim();

                                // 保存名称
//                                sp.edit().putString("name",newName).commit();
                                SpUtils.getInstance(MainActivity.this).save("name", newName);
                                // 设置显示
                                tv.setText(newName);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        if(v == bt_set_confim){
            // 获取输入的密码
            String pwd1 = et_set_pwd1.getText().toString();
            String pwd2 = et_set_pwd2.getText().toString();
            // 输入的密码不能为空
            if("".equals(pwd1)){
              // 提示
                MsUtils.showMsg(MainActivity.this,"密码输入不能为空");
                return;
            }
            // 校验两次输入的密码必须一致
            if(!pwd1.equals(pwd2)){
                MsUtils.showMsg(MainActivity.this,"两次输入的密码必须一致");
                return;
            }

            // 保存密码
//            sp.edit().putString("password",pwd1).commit();
            SpUtils.getInstance(MainActivity.this).save("password",MsUtils.md5(pwd1));


            mAletDialog.dismiss();

        }else if(v == bt_set_cancel){
            mAletDialog.dismiss();
        }else if(v == bt_login_cancel) {
            mAletDialog.dismiss();
        }else if(v == bt_login_confim) {
            // 校验密码是否一致
//            String password = sp.getString("password", null);
            String password = SpUtils.getInstance(MainActivity.this).getString("password",null);
            String newPassword = et_login_pwd2.getText().toString().trim();
            newPassword = MsUtils.md5(newPassword);
            Log.e("TAG",newPassword);

            if(password != null && password.equals(newPassword)) {
                mAletDialog.dismiss();
                toProtect();
            }else {
                MsUtils.showMsg(MainActivity.this,"输入密码错误");
            }

        }
    }

    // 进入防盗保护流程页面
    private void toProtect() {
        // 获取是否设置完成标记
        boolean config = SpUtils.getInstance(this).getBoolean(SpUtils.CONFIG, false);
        if(config) {
            // 进入手机防盗详情页面
            Intent intent = new Intent(MainActivity.this, ProtectInfoActivity.class);
            startActivity(intent);
        }else {
            // 进入手机防盗设置界面1
            Intent intent = new Intent(MainActivity.this,SetUp1Activity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position){
            case 0:
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
                intent = new Intent(this, AntivirusActivity.class);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(this, CacheCleanActivity.class);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(MainActivity.this, ToolActivity.class);
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showSetOrLoginDialog() {
        // 获取密码是否保存
//        String password = sp.getString("password", null);
        String password = SpUtils.getInstance(MainActivity.this).get("password",null);
        if(password != null) {
            showLoginDialog();
        } else {
            showSetDialog();
        }
    }
    private EditText et_login_pwd2;
    private Button bt_login_confim;
    private Button bt_login_cancel;

    private void showLoginDialog() {
        View convertview = View.inflate(MainActivity.this, R.layout.dialog_login_main, null);
        et_login_pwd2 = (EditText) convertview.findViewById(R.id.et_login_pwd2);
        bt_login_confim = (Button) convertview.findViewById(R.id.bt_login_confim);
        bt_login_cancel = (Button) convertview.findViewById(R.id.bt_login_cancel);

        bt_login_confim.setOnClickListener(this);
        bt_login_cancel.setOnClickListener(this);

        mAletDialog = new AlertDialog.Builder(this)
                .setView(convertview)
                .show();
    }

    private void showSetDialog() {
        View convertview = View.inflate(MainActivity.this, R.layout.dialog_set_main, null);
        et_set_pwd1 = (EditText) convertview.findViewById(R.id.et_set_pwd1);
        et_set_pwd2 = (EditText) convertview.findViewById(R.id.et_set_pwd2);
        bt_set_confim = (Button) convertview.findViewById(R.id.bt_set_confim);
        bt_set_cancel = (Button) convertview.findViewById(R.id.bt_set_cancel);

        bt_set_confim.setOnClickListener(this);
        bt_set_cancel.setOnClickListener(this);

        mAletDialog = new AlertDialog.Builder(this)
                .setView(convertview)
                    .show();
    }

    public boolean exitFlag = false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(!exitFlag) {
                exitFlag = true;
                MsUtils.showMsg(MainActivity.this,"再按一次退出");
                handler.sendEmptyMessageDelayed(WHAT_EXIT,2000);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }
}
