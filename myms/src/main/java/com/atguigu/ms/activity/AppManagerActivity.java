package com.atguigu.ms.activity;

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

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.util.MsUtils;

import java.util.List;
import java.util.Map;

public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView lv_app;
    private TextView tv_app_count;
    private LinearLayout ll_app;
    private List<AppInfo> mSystemInfo;
    private List<AppInfo> mCunstomInfo;
    private AppAdapter mAppAdapter;
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        //        public static int SCROLL_STATE_IDLE = 0;   空闲不滚动
//
//        public static int SCROLL_STATE_TOUCH_SCROLL = 1; 拖动
//
//        public static int SCROLL_STATE_FLING = 2;飞动
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                if (mPw != null && mPw.isShowing()) {
                    mPw.dismiss();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (mCunstomInfo == null) {
                return;
            }

            if (firstVisibleItem <= mCunstomInfo.size()) {
                tv_app_count.setText("用户应用:" + mCunstomInfo.size());
            } else {
                tv_app_count.setText("系统应用:" + mSystemInfo.size());
            }
        }
    };

    private int num;
    private View mPwView;
    private PopupWindow mPw;
    private UnInstallReceiver mUnInstallReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        // 获取控件对象
        lv_app = (ListView) findViewById(R.id.lv_app);
        tv_app_count = (TextView) findViewById(R.id.tv_app_count);
        ll_app = (LinearLayout) findViewById(R.id.ll_app);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                // 显示提示的加载视图
                ll_app.setVisibility(View.VISIBLE);
                tv_app_count.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Map<Boolean, List<AppInfo>> map = MsUtils.getAllAppInfo(AppManagerActivity.this);

                mSystemInfo = map.get(true);
                mCunstomInfo = map.get(false);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //页面处理
                ll_app.setVisibility(View.INVISIBLE);
                tv_app_count.setVisibility(View.VISIBLE);

                tv_app_count.setText("用户应用:" + mCunstomInfo.size());

                // listview显示数据
                mAppAdapter = new AppAdapter();
                lv_app.setAdapter(mAppAdapter);

            }
        }.execute();


        lv_app.setOnScrollListener(scrollListener);

        lv_app.setOnItemClickListener(this);

        // 注册软件卸载广播
        mUnInstallReceiver = new UnInstallReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");// package:包名
        registerReceiver(mUnInstallReceiver, filter);
    }

    private int mPosition;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // 过滤掉两个textview的点击事件
        if (position == 0 || position == mCunstomInfo.size() + 1) {
            return;
        }

        // 保存app的position
        mPosition = position;

        if (mPw == null) {
            mPwView = View.inflate(AppManagerActivity.this, R.layout.popupwindow_layout, null);
            mPwView.findViewById(R.id.ll_app_uninstall).setOnClickListener(this);
            mPwView.findViewById(R.id.ll_app_run).setOnClickListener(this);
            mPwView.findViewById(R.id.ll_app_share).setOnClickListener(this);

            mPw = new PopupWindow(mPwView, view.getWidth() - 120, view.getHeight());
            // 添加背景色才能显示动画
            mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (mPw.isShowing()) {
            mPw.dismiss();
        }

        mPw.showAsDropDown(view, 60, -view.getHeight());


        // 执行动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
        scaleAnimation.setDuration(500);
        mPwView.startAnimation(scaleAnimation);
    }

    @Override
    public void onClick(View v) {

        AppInfo info = (AppInfo) mAppAdapter.getItem(mPosition);

        switch (v.getId()) {
            case R.id.ll_app_uninstall:// 卸载
                uninStallApp(info);
                break;
            case R.id.ll_app_run:// 运行
                startApp(info.getPackageName());
                break;
            case R.id.ll_app_share:// 分享
                shareApp(info.getAppName());
                break;
        }

        if (mPw != null && mPw.isShowing()) {
            mPw.dismiss();
        }
    }

    // 卸载应用
    private void uninStallApp(AppInfo appInfo) {
        if (appInfo.isSystem()) {
            Toast.makeText(this, "系统应用不能卸载!", Toast.LENGTH_SHORT).show();
        } else if (getPackageName().equals(appInfo.getPackageName())) {
            Toast.makeText(this, "当前应用不能卸载!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
            startActivity(intent);
        }
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

    // 分享
    private void shareApp(String appName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");// 纯文本
        //intent.putExtra(Intent.EXTRA_SUBJECT, "应用分享");
        intent.putExtra(Intent.EXTRA_TEXT, "分享一个不错的应用: " + appName); // 内容
        startActivity(intent);
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSystemInfo.size() + mCunstomInfo.size() + 2;
        }

        @Override
        public Object getItem(int position) {

            if (position == 0) {
                return "用户应用:" + mCunstomInfo.size();
            } else if (position <= mCunstomInfo.size()) {
                return mCunstomInfo.get(position - 1);
            } else if (position == mCunstomInfo.size() + 1) {
                return "系统应用" + mSystemInfo.size();
            } else {
                return mSystemInfo.get(position - mCunstomInfo.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object itemData = getItem(position);

            // textVivew处理
            if (position == 0 || position == mCunstomInfo.size() + 1) {
                TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.app_count, null);
                textView.setText((String) itemData);

                return textView;
            }

            // 创建或获取viewholder
            ViewHolder holder;
            if (convertView == null || convertView instanceof TextView) {
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_count, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取当前item数据
            AppInfo info = (AppInfo) itemData;

            // 设置当前item数据
            holder.icon.setImageDrawable(info.getIcon());
            holder.name.setText(info.getAppName());
            // 返回convertView

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
        }
    }

    class UnInstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();// package:包名
            if(dataString != null) {
                String packageName = dataString.substring(dataString.indexOf(":") + 1);

                AppInfo info = new AppInfo(null, packageName, null, false);

                // 更新内存
                mCunstomInfo.remove(info);
                // 更新页面
                mAppAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUnInstallReceiver);
    }
}
