package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.UpdateInfo;
import com.atguigu.mobilesafe.net.APIClicent;
import com.atguigu.mobilesafe.util.MsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

// 欢迎页面
public class WelcomeActivity extends Activity {
    private static final int WHAT_UPDATE_SUCCESS = 1;
    private static final int WHAT_UPDATE_ERROR = 2;
    private static final int WHAT_DOWNLOAD_SUCCESS = 3;
    private static final int WHAT_DOWNLOAD_ERROR = 4;
    private static final int WHAT_TO_MAIN = 5;
    private RelativeLayout rl_welcome_root;
    private TextView tv_welcome_version;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UPDATE_SUCCESS:
                    // 判断版本号是否一致
                    if (version.equals(mInfo.getVersion())) {
                        // 一致 提示已经是最新版本 并进入主页面
                        MsUtils.showMsg(WelcomeActivity.this, "已经是最新版本");
                        toMainUI();
                    }
                    // 模拟网络延时
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 不是最新版本
                            showUpdateDialog();
                        }
                    }, 2000);

                    break;
                case WHAT_UPDATE_ERROR:
                    // 提示请求失败 进入主页面
                    MsUtils.showMsg(WelcomeActivity.this, "获取更新信息失败");
                    toMainUI();
                    break;
                case WHAT_DOWNLOAD_SUCCESS:
                    // 隐藏进度条
                    mPb.dismiss();
                    // 启动安装
                    installApk();
                    break;

                case WHAT_DOWNLOAD_ERROR:
                    // 隐藏进度条 提示下载失败 进入主页面
                    mPb.dismiss();
                    MsUtils.showMsg(WelcomeActivity.this, "下载apk失败");
                    toMainUI();
                    break;
                case WHAT_TO_MAIN:
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    private File apkFile;
    private long mStartTime;

    private void installApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            // 进入主页面
            toMainUI();
        }
    }

    private ProgressDialog mPb;

    private void showUpdateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("下载最新程序版本")
                .setMessage(mInfo.getDesc())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownloadApk();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 取消对话框  进入主页面
                        dialog.dismiss();
                        toMainUI();
                    }
                })
                .show();
    }

    private void startDownloadApk() {
        // 准备下载的progressdialog
        mPb = new ProgressDialog(WelcomeActivity.this);
        mPb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPb.show();

        // 准备下载apk的路径
        apkFile = new File(getExternalFilesDir(null), "ms.apk");
        // 访问网络开始下载
        new Thread() {
            public void run() {
                try {
                    APIClicent.downloadAPK(mPb, apkFile, mInfo.getApkUrl());
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
                }
            }
        }.start();
    }

    /**
     * 进入主页面
     */
    private void toMainUI() {
        long time = System.currentTimeMillis() - mStartTime;
        long delayTime = 0;
        if (time < 2000) {
            delayTime = 2000 - time;
        } else {
            delayTime = 0;
        }

        handler.sendEmptyMessageDelayed(WHAT_TO_MAIN, delayTime);

    }

    private UpdateInfo mInfo;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        // 获取开始时间
        mStartTime = System.currentTimeMillis();


        // 获取布局文件中的对象
        rl_welcome_root = (RelativeLayout) findViewById(R.id.rl_welcome_root);
        tv_welcome_version = (TextView) findViewById(R.id.tv_welcome_version);

        // 欢迎页面动画显示
        showAnimation();

        // 获取程序版本号
        version = getVersion();
        tv_welcome_version.setText("程序版本号:" + version);

        // 拷贝assets文件夹下的文件到file文件中
        copyAllDatabase();

        // 检查程序版本更新
        checkUpdate();
    }

    private void checkUpdate() {
        boolean isconnect = MsUtils.isConnected(WelcomeActivity.this);
        // 判断当前是否有网络
        if (!isconnect) {
            // 没有网络,提示 并进入主页面
            MsUtils.showMsg(WelcomeActivity.this, "获取更新信息失败");
        }
        // 有网络 获取更新数据
        new Thread() {
            public void run() {
                try {
                    mInfo = APIClicent.getUpdateInfo();
                    Log.e("TAG", mInfo.getVersion() + "  " + mInfo.getApkUrl() + " " + mInfo.getDesc());
                    handler.sendEmptyMessage(WHAT_UPDATE_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_UPDATE_ERROR);
                }
            }
        }.start();

    }

    private void copyAllDatabase() {
        new Thread() {
            public void run() {
                copyDatabase("address.db");
                copyDatabase("antivirus.db");
                copyDatabase("commonnum.db");

            }
        }.start();
    }

    private void copyDatabase(String fileName) {
        // 0 准备一个file
        File file = new File(getFilesDir(), fileName);
        if(file.exists() && file.length() > 0) {
            return;
        }

        AssetManager manager = getAssets();
        try {

            // 1 获取输入流
            InputStream is = manager.open(fileName);
            // 2 创建输出流
            FileOutputStream os = new FileOutputStream(file);
            // 3 边读边写
            byte[] buffer = new byte[2048];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            // 4 关闭资源

            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取程序版本号
     *
     * @return
     */
    private String getVersion() {
        String version = null;
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    private void showAnimation() {
        // 旋转
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        // 缩放
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        // 渐变
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);

        //动画合集
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        rl_welcome_root.startAnimation(animationSet);
    }
}
