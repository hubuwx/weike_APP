package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.dao.AntivirusDao;
import com.atguigu.mobilesafe.util.MsUtils;

import java.util.ArrayList;
import java.util.List;

// 病毒查杀页面
public class AntivirusActivity extends Activity {
    ImageView iv_antivirus_scanning;
    TextView tv_antivirus_status;
    ProgressBar pb_antivirus_progress;
    LinearLayout ll_antivirus_container;
    PackageManager pm;

    List<VirusInfo> virusInfos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        // 初始化控件
        iv_antivirus_scanning = (ImageView) findViewById(R.id.iv_antivirus_scanning);
        tv_antivirus_status = (TextView) findViewById(R.id.tv_antivirus_status);
        pb_antivirus_progress = (ProgressBar) findViewById(R.id.pb_antivirus_progress);
        ll_antivirus_container = (LinearLayout) findViewById(R.id.ll_antivirus_container);

        pm = getPackageManager();
        //1. 显示提示视图
        scanAnimation();
        new Thread() {
            public void run() {
                //2. 启动分线程, 加载数据
                scan();

            }
        }.start();
        //3. 更新界面

    }

    private void scan() {
        //得到所有包(包括已删除但安装目录还存在的)
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        pb_antivirus_progress.setMax(packages.size());
        for (PackageInfo packInfo : packages) {
            final VirusInfo info = new VirusInfo();
            //应用名称
            info.appName = packInfo.applicationInfo.loadLabel(pm).toString();
            //应用包名
            info.packageName = packInfo.packageName;
            //应用签名
            String signature = packInfo.signatures[0].toCharsString();
            String md5 = MsUtils.md5(signature);

            Log.e("TAG", md5 + "__" + info.packageName + "__" + signature);

            boolean virus = AntivirusDao.isVirus(this, md5);
            info.virus = virus;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //显示扫描的信息
                    showInfo(info);
                }
            });

            SystemClock.sleep(50);
        }
    }



    void showInfo(VirusInfo info) {
        tv_antivirus_status.setText("扫描完" + info.appName);
        pb_antivirus_progress.incrementProgressBy(1);

        TextView textView = new TextView(this);
        if (info.virus) {
            textView.setText("发现病毒: " + info.appName);
            textView.setTextColor(Color.RED);

            virusInfos.add(info);
        } else {
            textView.setText("扫描安全: "+info.appName);
            textView.setTextColor(Color.BLACK);
        }

        ll_antivirus_container.addView(textView, 0);

        if (pb_antivirus_progress.getProgress() == pb_antivirus_progress.getMax()) {
            pb_antivirus_progress.setVisibility(View.GONE);
            tv_antivirus_status.setText("扫描完成, 发现" + virusInfos.size() + "个病毒应用");
            iv_antivirus_scanning.clearAnimation();
            //如果有病毒应用, 提示卸载它们
            if (virusInfos.size() > 0) {
                new AlertDialog.Builder(this)
                        .setTitle("卸载病毒应用")
                        .setMessage("是否确定卸载" + virusInfos.size() + "个病毒应用?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (VirusInfo info : virusInfos) {
                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:" + info.packageName));
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }


    // 显示提示视图
    private void scanAnimation() {
        RotateAnimation animation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());//匀速
        animation.setRepeatCount(Animation.INFINITE);//无限次
        iv_antivirus_scanning.startAnimation(animation);
    }

    class VirusInfo {
        String packageName;
        String appName;
        boolean virus; //是否是病毒
    }
}
