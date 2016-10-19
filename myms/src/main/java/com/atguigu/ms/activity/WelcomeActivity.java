package com.atguigu.ms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.UpdateInfo;
import com.atguigu.ms.net.APIClicent;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

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

            if(isFinishing()) {
                return;
            }

            switch (msg.what) {
                case WHAT_UPDATE_SUCCESS:// 更新详情成功
                    // 判断版本号是否一致
                    if (mVersion.equals(mUpdateInfo.getVersion())) {
                        // 一致 进入主页面  提示
                        toMainUI();
                        MsUtils.showMsg(WelcomeActivity.this, "是最新版本,不需要更新");
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 不一致 显示下载的dialog
                                showDownloadDialog();
                            }
                        },1000);
                    }
                    break;

                case WHAT_UPDATE_ERROR:// 更新详情失败
                    // 进入主页面 并提示
                    toMainUI();
                    MsUtils.showMsg(WelcomeActivity.this, "下载更新详情失败");
                    break;

                case WHAT_DOWNLOAD_SUCCESS:// 下载apk成功
                    // 隐藏进度条
                    mPb.dismiss();
                    // 启动安装
                    installApk();
                    break;

                case WHAT_DOWNLOAD_ERROR:// 下载apk失败
                    // 进入主页面  提示
                    toMainUI();

                    MsUtils.showMsg(WelcomeActivity.this,"下载apk失败");
                    break;
                case  WHAT_TO_MAIN:// 进入主页面
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    private long mStartTime;

    // 启动安装apk
    private void installApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(mFileApk),"application/vnd.android.package-archive");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==1 && resultCode == RESULT_CANCELED ) {
            toMainUI();
        }
    }

    private ProgressDialog mPb;
    private File mFileApk;

    //显示下载的dialog
    private void showDownloadDialog() {
        new AlertDialog.Builder(this)
                .setTitle("下载最新版本")
                .setMessage(mUpdateInfo.getDesc())
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 开始下载
                        startDownloadApk();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 进入主页面
                        toMainUI();

                    }
                })
                .show();

    }

    // 开始下载
    private void startDownloadApk() {
        // 创建一个progressdialgo
        mPb = new ProgressDialog(this);
        mPb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPb.show();

        // 准备apk下载的存放位置
        mFileApk = new File(getExternalFilesDir(null), "ms.apk");

        // 启动子线程
        new Thread() {
            public void run() {
                try {
                    APIClicent.downloadApk(mPb, mFileApk, mUpdateInfo.getApkUrl());
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT_DOWNLOAD_ERROR);
                }
            }
        }.start();
    }

    private String mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        // 获取开始时间
        mStartTime = System.currentTimeMillis();

        // 获取控件对象
        rl_welcome_root = (RelativeLayout) findViewById(R.id.rl_welcome_root);
        tv_welcome_version = (TextView) findViewById(R.id.tv_welcome_version);

        // 显示加载动画
        showAnimation();

        // 获取程序版本号
        mVersion = getVersion();
        tv_welcome_version.setText("程序版本号:" + mVersion);

        // 将assets下的静态数据库资源拷贝到file 目录下
        copyAllDatabase();

        //检查程序版本更新流程
        checkUpdate();

        // 创建快捷图标
        makeShortCut();
    }

    private void makeShortCut() {
        Boolean isCut = SpUtils.getInstance(this).get(SpUtils.SHORT_CUT, false);
        Log.i("TAG33", isCut.toString());
        if(!isCut) {

            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.qq1));

            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "my qq");

            //隐式意图
            Intent clickIntent = new Intent("com.atguigu.ms.activity.MainActivity");

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);

            sendBroadcast(intent);

            SpUtils.getInstance(this).save(SpUtils.SHORT_CUT,true);
        }
    }

    private UpdateInfo mUpdateInfo = null;

    //检查程序版本更新流程
    private void checkUpdate() {
        // 检查手机是否联网
        boolean isContect = MsUtils.isContect(this);
        Log.e("Q11", "checkUpdate_"+isContect);
        if (!isContect) {
            // 没有网络 进入主页面 提示
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            // 进入主页面
            toMainUI();
        } else {
            // 有网络 获取版本更新信息详情
            new Thread() {
                public void run() {
                    // 获取版本更新详情

                    try {
                        mUpdateInfo = APIClicent.getUpdateInfo();
                        Log.e("tag", mUpdateInfo.getDesc() + "  " + mUpdateInfo.getApkUrl());
                        handler.sendEmptyMessage(WHAT_UPDATE_SUCCESS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 失败
                        handler.sendEmptyMessage(WHAT_UPDATE_ERROR);
                    }
                }
            }.start();
        }

    }

    // 进入主页面
    private void toMainUI() {
        long delayTime = System.currentTimeMillis() - mStartTime;

        if(delayTime > 3000) {
            delayTime = 0;
        }else {
            delayTime = 3000- delayTime;
        }
//Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//startActivity(intent);
        handler.sendEmptyMessageDelayed(WHAT_TO_MAIN, delayTime);

    }

    // 将assets下的静态数据库资源拷贝到file 目录下
    private void copyAllDatabase() {
        new Thread() {
            public void run() {
                copyDatabase("address.db");
                copyDatabase("antivirus.db");
                copyDatabase("commonnum.db");
            }
        }.start();
    }

    // 拷贝文件
    private void copyDatabase(String fileName) {
        // 准备工作
        File file = new File(getFilesDir(), fileName);

        if (file.exists() && file.length() > 0) {
            Log.e("TAG", "已经拷贝完成");
            return;
        }

        InputStream is = null;
        FileOutputStream fos = null;

        try {
            AssetManager manager = getAssets();
            // 获取输入流
            is = manager.open(fileName);
            //创建输出流
            fos = new FileOutputStream(file);
            //边读边写
            byte[] buffer = new byte[2048];
            int len = -1;

            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 获取程序版本号
    private String getVersion() {
        String version = "未知";

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    // 显示加载动画
    private void showAnimation() {
        // 旋转
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        // 缩放

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);

        // 透明
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        // 组合
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        // 执行动画
        rl_welcome_root.startAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 移除所有消息
        handler.removeCallbacksAndMessages(null);
        rl_welcome_root.clearAnimation();
    }
}
