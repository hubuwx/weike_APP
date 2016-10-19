package com.atguigu.ms.activity;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.CacheInfo;
import com.atguigu.ms.util.MsUtils;

import java.lang.reflect.Method;
import java.util.List;

public class CacheCleanActivity extends AppCompatActivity {
    private static final int WHAT_SHOW = 1;
    private TextView tv_cache_clean_status;
    private ProgressBar pb_cache_clean;
    private LinearLayout ll_cache_clean_contaner;
    private PackageManager mPm;
    private  long mCacheSize;


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Log.i("TAG", "进入handler1");
            switch (msg.what) {
                case  WHAT_SHOW:

                    //获得一次的应用信息
                    final CacheInfo ci = (CacheInfo) msg.obj;

                    pb_cache_clean.incrementProgressBy(1);
                    tv_cache_clean_status.setText("扫描："+ci.getAppName());

                    //获取一次的View对象
                    View view = View.inflate(CacheCleanActivity.this, R.layout.item_cache_clean,null);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

                    //给一次的View填充信息
                    iv_icon.setImageDrawable(ci.getIcon());
                    tv_name.setText(ci.getAppName());
                    tv_cache.setText(MsUtils.formatSize(CacheCleanActivity.this, ci.getCacheSize()));

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //通过隐式意图来跳转界面从而清除缓存
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ci.getPackageName()));
                            startActivity(intent);
                        }
                    });

                    ll_cache_clean_contaner.addView(view,0);

                    mCacheSize += ci.getCacheSize();
                    if(pb_cache_clean.getMax() == pb_cache_clean.getProgress()) {
                        tv_cache_clean_status.setText("共扫描"+pb_cache_clean.getMax()+"项数据,一共"
                                + MsUtils.formatSize(CacheCleanActivity.this,mCacheSize));
                        pb_cache_clean.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);

        //获取控制对象
        tv_cache_clean_status = (TextView)findViewById(R.id.tv_cache_clean_status);
        pb_cache_clean = (ProgressBar)findViewById(R.id.pb_cache_clean);
        ll_cache_clean_contaner = (LinearLayout)findViewById(R.id.ll_cache_clean_contaner);

        //获取管理者
        mPm = getPackageManager();

        tv_cache_clean_status.setText("扫描中");

        new Thread(){
            public void run(){
                Log.i("TAG", "Thread");
                List<ApplicationInfo> applications = mPm.getInstalledApplications(0);
                //获取进度条的最大值
                pb_cache_clean.setMax(applications.size());
                for (ApplicationInfo info:applications){
                    //创建bean对象
                    CacheInfo cacheInfo = new CacheInfo();

                    //包名
                    String packageName = info.packageName;
                    cacheInfo.setPackageName(packageName);

                    //应用名
                    String appName = info.loadLabel(mPm).toString();
                    cacheInfo.setAppName(appName);

                    // 图标
                    Drawable icon = info.loadIcon(mPm);
                    cacheInfo.setIcon(icon);

                    // 缓存大小
                    getCacheSize(cacheInfo);
                    //睡一会儿，否则获取太快
                    SystemClock.sleep(100);
                }
            }
        }.start();

    }

    // 获取cacheInfo  反射 +aidl
    private void getCacheSize(final CacheInfo cacheInfo) {
  //      void getPackageSizeInfo(String packageName, IPackageStatsObserver observer)
        try {
            Log.i("TAG", "getCacheSize");
            Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class );
            method.invoke(mPm, cacheInfo.getPackageName(), new IPackageStatsObserver.Stub() {
                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    long cacheSize = pStats.cacheSize;

                    cacheInfo.setCacheSize(cacheSize);
                    Message message = Message.obtain();
                    message.what = WHAT_SHOW;
                    message.obj = cacheInfo;
                    handler.sendMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
