package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.util.MsUtils;

import java.lang.reflect.Method;
import java.util.List;

// 缓存清理页面
public class CacheCleanActivity extends Activity {

    private static final int WHAT_SHOW_INFO = 1;
    private TextView tv_cache_clean_status;
    private ProgressBar pb_cache_clean;
    private LinearLayout ll_cache_clean_container;
    private long totalCacheSize = 0;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_SHOW_INFO) {
                //得到数据
                final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                //统计
                totalCacheSize += cacheInfo.cacheSize;
                //更新界面
                tv_cache_clean_status.setText("扫描"+cacheInfo.appName);
                pb_cache_clean.incrementProgressBy(1);
                //显示缓存相关信息
                //1. 加载布局
                View view = View.inflate(CacheCleanActivity.this, R.layout.item_cache_clean, null);
                //2. 设置数据
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
                imageView.setImageDrawable(cacheInfo.icon);
                TextView nameTV = (TextView) view.findViewById(R.id.tv_name);
                nameTV.setText(cacheInfo.appName);
                TextView cacheTV = (TextView) view.findViewById(R.id.tv_cache);
                cacheTV.setText(MsUtils.formatSize(CacheCleanActivity.this, cacheInfo.cacheSize));
                view.findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + cacheInfo.packageName));
                        startActivity(intent);
                    }
                });
                //3. 添加到ll_cache_clean_container
                ll_cache_clean_container.addView(view, 0);//添加在第一位

                //处理扫描完成
                if (pb_cache_clean.getProgress() == pb_cache_clean.getMax()) {
                    pb_cache_clean.setVisibility(View.GONE);
                    tv_cache_clean_status.setText("共扫描"+pb_cache_clean.getMax()+"项缓存数据, 总大小为"
                            + MsUtils.formatSize(CacheCleanActivity.this, totalCacheSize));
                }
            }
        }
    };
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);

        pm = getPackageManager();
        tv_cache_clean_status = (TextView)findViewById(R.id.tv_cache_clean_status);
        pb_cache_clean = (ProgressBar)findViewById(R.id.pb_cache_clean);
        ll_cache_clean_container = (LinearLayout)findViewById(R.id.ll_cache_clean_container);

        tv_cache_clean_status.setText("准备开始扫描...");
        new Thread(){
            public void run(){
                //得到安装的所有应用
                List<ApplicationInfo> applications = pm.getInstalledApplications(0);
                pb_cache_clean.setMax(applications.size());
                for (ApplicationInfo info : applications) {
                    CacheInfo cacheInfo = new CacheInfo();
                    //packageName
                    String packageName = info.packageName;
                    cacheInfo.packageName = packageName;
                    //appName
                    String appName = info.loadLabel(pm).toString();
                    cacheInfo.appName = appName;
                    //icon
                    Drawable icon = info.loadIcon(pm);
                    cacheInfo.icon = icon;
                    //cacheSize
                    getCacheSize(cacheInfo);

                    SystemClock.sleep(50);
                }
            }
        }.start();
    }

    /**
     * 获取应用缓存大小
     * @param cacheInfo
     * 反射 + AIDL
     */
    private void getCacheSize(final CacheInfo cacheInfo) {
        //反射调用 PackageManager void getPackageSizeInfo(String packageName, IPackageStatsObserver observer)
        try {
            Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            method.invoke(pm, cacheInfo.packageName, new IPackageStatsObserver.Stub(){
                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    long cacheSize = pStats.cacheSize;
                    cacheInfo.cacheSize = cacheSize;
                    //更新界面
                    Message msg = Message.obtain();
                    msg.what = WHAT_SHOW_INFO;
                    msg.obj = cacheInfo;
                    handler.sendMessage(msg);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CacheInfo {
        String packageName;
        String appName;
        Drawable icon;
        long cacheSize;
    }
}
