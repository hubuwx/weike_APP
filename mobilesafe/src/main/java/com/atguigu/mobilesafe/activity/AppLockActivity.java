package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.AppInfo;
import com.atguigu.mobilesafe.dao.AppLockDao;
import com.atguigu.mobilesafe.service.AppLockService;
import com.atguigu.mobilesafe.util.MsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 程序锁页面
public class AppLockActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final int WHAT_APP_INFO = 1;
    private ListView lv_lock;
    private LinearLayout ll_lock_loading;
    private List<AppInfo> appInfos = new ArrayList<>();

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == WHAT_APP_INFO) {
                ll_lock_loading.setVisibility(View.GONE);
                appLockAdapter = new AppLockAdapter();
                lv_lock.setAdapter(appLockAdapter);
            }
        }
    };
    private AppLockAdapter appLockAdapter;
    private AppLockDao appLockDao;
    private List<String> appLockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        // 获取控件对象
        lv_lock = (ListView) findViewById(R.id.lv_lock);
        ll_lock_loading = (LinearLayout) findViewById(R.id.ll_lock_loading);

        appLockDao = new AppLockDao(this);
        appLockList = appLockDao.getAll();

        // 初始化显示
        ll_lock_loading.setVisibility(View.VISIBLE);

        new Thread() {
            public void run() {
                Map<Boolean, List<AppInfo>> allAppInfo = MsUtils.getAllAppInfo(AppLockActivity.this);

                appInfos.addAll(allAppInfo.get(false));
                appInfos.addAll(allAppInfo.get(true));

                handler.sendEmptyMessage(WHAT_APP_INFO);
            }
        }.start();

        // 每个条目的点击事件
        lv_lock.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 执行动画
        TranslateAnimation translaeAnimation = new TranslateAnimation(0,20,0,0);
        translaeAnimation.setDuration(500);
        view.startAnimation(translaeAnimation);

        // 得到当前锁状态
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_lock);
        boolean lock = (boolean) imageView.getTag();
        if(lock) {
            // 修改内存
            imageView.setTag(false);
            appLockList.remove(position);
            // 修改显示
            imageView.setImageResource(R.drawable.unlock);
            // 存储
            appLockDao.delete(appInfos.get(position).getPackageName());

            // 通知服务
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("delete",appInfos.get(position).getPackageName());
            startService(intent);

        }else {
            // 修改内存
            imageView.setTag(true);
            appLockList.add(appInfos.get(position).getPackageName());
            // 修改显示
            imageView.setImageResource(R.drawable.lock);
            // 存储
            appLockDao.add(appInfos.get(position).getPackageName());

            // 通知服务
            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("add", appInfos.get(position).getPackageName());
            startService(intent);
        }
    }

    // 程序锁的适配器
    class AppLockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return appInfos == null ? 0 : appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(AppLockActivity.this, R.layout.item_lock, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
                holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_item_lock);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取当前item数据
            AppInfo appInfo = appInfos.get(position);
            // 赋值操作
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getAppName());

            // 程序锁的处理
            if (appLockList.contains(appInfo.getPackageName())) {
                holder.iv_lock.setImageResource(R.drawable.lock);
                holder.iv_lock.setTag(true);
            }else {
                holder.iv_lock.setImageResource(R.drawable.unlock);
                holder.iv_lock.setTag(false);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            ImageView iv_lock;
            TextView tv_name;
        }
    }
}
