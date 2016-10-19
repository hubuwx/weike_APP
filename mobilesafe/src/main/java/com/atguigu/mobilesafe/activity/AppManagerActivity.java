package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.AppInfo;
import com.atguigu.mobilesafe.util.MsUtils;

import java.util.List;
import java.util.Map;

// 软件管理页面
public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView lv_soft;
    private LinearLayout ll_soft_loading;
    private TextView tv_soft_count;
    private List<AppInfo> systemInfo;
    private List<AppInfo> customInfo;
    private AppAdapter appAdapter;
    private AbsListView.OnScrollListener listener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 开始滚动 取消popuwindow
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (pw != null && pw.isShowing()) {
                    pw.dismiss();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (customInfo == null) {
                return;
            }

            if (firstVisibleItem < customInfo.size() + 1) {
                tv_soft_count.setText("用户程序:" + customInfo.size());
            } else {
                tv_soft_count.setText("系统程序:" + systemInfo.size());
            }
        }
    };
    private UninstallReceiver uninstallReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        // 获取控件对象
        lv_soft = (ListView) findViewById(R.id.lv_soft);
        ll_soft_loading = (LinearLayout) findViewById(R.id.ll_soft_loading);
        tv_soft_count = (TextView) findViewById(R.id.tv_soft_count);

        // 获取数据
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                ll_soft_loading.setVisibility(View.VISIBLE);
                tv_soft_count.setVisibility(View.INVISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Map<Boolean, List<AppInfo>> map = MsUtils.getAllAppInfo(AppManagerActivity.this);
                systemInfo = map.get(true);
                customInfo = map.get(false);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ll_soft_loading.setVisibility(View.INVISIBLE);
                tv_soft_count.setVisibility(View.VISIBLE);
                tv_soft_count.setText("用户应用:");

                // 初始化listview
                appAdapter = new AppAdapter();
                lv_soft.setAdapter(appAdapter);
            }
        }.execute();

        // listView的滚动监听
        lv_soft.setOnScrollListener(listener);

        // listView的条目点击事件
        lv_soft.setOnItemClickListener(this);

        // 注册一个卸载应用的广播接收者
        uninstallReceiver = new UninstallReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");//package:com.atguigu.ms
        registerReceiver(uninstallReceiver, filter);
    }

    private PopupWindow pw;
    private View pwView;
    private int position;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0 || position == customInfo.size() + 1) {
            return;
        }
        this.position = position;
        // 创建
        if (pw == null) {
            pwView = View.inflate(AppManagerActivity.this, R.layout.popupwindow_layout, null);
            // 点击事件
            pwView.findViewById(R.id.ll_soft_uninstall).setOnClickListener(this);
            pwView.findViewById(R.id.ll_soft_running).setOnClickListener(this);
            pwView.findViewById(R.id.ll_soft_share).setOnClickListener(this);

            pw = new PopupWindow(pwView, view.getWidth() - 100, view.getHeight() + 40);
            pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景色为透明
        }

        // 如果已经开启,则先关闭掉
        if (pw.isShowing()) {
            pw.dismiss();
        }

        // 显示位置设置
        pw.showAsDropDown(view, 50, -view.getHeight() - 20);

        // 显示动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
        scaleAnimation.setDuration(500);
        pwView.startAnimation(scaleAnimation);
    }

    @Override
    public void onClick(View v) {

        AppInfo appInfo = (AppInfo) appAdapter.getItem(position);

        switch (v.getId()) {
            case R.id.ll_soft_uninstall://卸载
                uninStallApp(appInfo);
                break;
            case R.id.ll_soft_running://运行
                startApp(appInfo.getPackageName());
                break;
            case R.id.ll_soft_share://分享
                shareApp(appInfo.getAppName());
                break;
        }

        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
    }

    // 分享应用
    private void shareApp(String appName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");// 纯文本
        //intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
        intent.putExtra(Intent.EXTRA_TEXT, "分享一个不错的应用: " + appName); // 内容
        startActivity(intent);
    }

    // 启动应用
    private void startApp(String packageName) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            MsUtils.showMsg(this, "此应用无法启动");
        } else {
            startActivity(intent);
        }
    }

    // 卸载应用
    private void uninStallApp(AppInfo appInfo) {
        if (appInfo.isSystem()) {
            Toast.makeText(this, "系统应用不能卸载!", Toast.LENGTH_LONG).show();
        } else if (getPackageName().equals(appInfo.getPackageName())) {
            Toast.makeText(this, "当前应用不能卸载!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
            startActivity(intent);
        }
    }


    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return systemInfo.size() + customInfo.size() + 2;
        }

        @Override
        public Object getItem(int position) {

            if (position == 0) {
                return "用户应用:" + customInfo.size();
            } else if (position <= customInfo.size()) {
                return customInfo.get(position - 1);
            } else if (position == customInfo.size() + 1) {
                return "系统应用:" + systemInfo.size();
            } else {
                return systemInfo.get(position - customInfo.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Object itemData = getItem(position);

            if (position == 0 || position == customInfo.size() + 1) {
                TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.app_count, null);
                textView.setText((String) itemData);
                return textView;
            }

            ViewHolder holder = null;
            if (convertView == null || convertView instanceof TextView) {
                holder = new ViewHolder();
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app, null);

                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_app_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取当前item的数据
            AppInfo appInfo = (AppInfo) itemData;

            holder.imageView.setImageDrawable(appInfo.getIcon());
            holder.textView.setText(appInfo.getAppName());

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

    // 监听应用软件的卸载
    class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            if(dataString != null) {
                AppInfo appInfo = new AppInfo();
                String packageName = dataString.substring(dataString.indexOf(":")+1);
                appInfo.setPackageName(packageName);

                customInfo.remove(appInfo);
                appAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uninstallReceiver);
    }
}
