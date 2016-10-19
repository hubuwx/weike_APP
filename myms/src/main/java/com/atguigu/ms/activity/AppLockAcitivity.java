package com.atguigu.ms.activity;

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

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.dao.AppLockDao;
import com.atguigu.ms.service.AppLockService;
import com.atguigu.ms.util.MsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 程序锁页面
public class AppLockAcitivity extends Activity implements AdapterView.OnItemClickListener {
    private static final int WHAT_SHOW_LIST = 1;
    private ListView lv_lock;
    private LinearLayout ll_lock;
    private List<AppInfo> mAppInfos = new ArrayList<>();

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case WHAT_SHOW_LIST://
                    //进度条处理
                    ll_lock.setVisibility(View.INVISIBLE);

                    mAppLockAdapter = new AppLockAdapter();
                    lv_lock.setAdapter(mAppLockAdapter);

                    break;
            }
        }
    };
    private AppLockAdapter mAppLockAdapter;
    private AppLockDao mAppLockDao;
    private List<String> mListLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_lock_acitivity);

        // 获取控件对象
        lv_lock = (ListView)findViewById(R.id.lv_lock);
        ll_lock = (LinearLayout)findViewById(R.id.ll_lock);

        ll_lock.setVisibility(View.VISIBLE);

        new Thread(){
            public void run(){
                Map<Boolean, List<AppInfo>> allAppInfo = MsUtils.getAllAppInfo(AppLockAcitivity.this);

                mAppInfos.addAll(allAppInfo.get(true));
                mAppInfos.addAll(allAppInfo.get(false));

                handler.sendEmptyMessage(WHAT_SHOW_LIST);
            }
        }.start();

        lv_lock.setOnItemClickListener(this);

        // 加锁软件查询
        mAppLockDao = new AppLockDao(this);
        mListLock = mAppLockDao.get();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 执行平移动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0,20,0,0);
        translateAnimation.setDuration(500);
        view.startAnimation(translateAnimation);

        // 获取当前item数据
        AppInfo info = mAppInfos.get(position);

        ImageView lockView = (ImageView) view.findViewById(R.id.iv_item_lock_lock);
        boolean isLock = (boolean) lockView.getTag();
        if(isLock) {
            // 更新内存
            lockView.setTag(false);
            mListLock.remove(info.getPackageName());
            // 更新页面
            lockView.setImageResource(R.drawable.unlock);

            // 更新存储
            mAppLockDao.delete(info.getPackageName());

            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("delete",info.getPackageName());
            startService(intent);
        }else {
            // 更新内存
            lockView.setTag(true);
            mListLock.add(info.getPackageName());
            // 更新页面
            lockView.setImageResource(R.drawable.lock);

            // 更新存储
            mAppLockDao.add(info.getPackageName());

            Intent intent = new Intent(this, AppLockService.class);
            intent.putExtra("add", info.getPackageName());
            startService(intent);
        }
    }

    class AppLockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mAppInfos == null ? 0:mAppInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 获取或创建ViewHolder
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();

                convertView = View.inflate(AppLockAcitivity.this,R.layout.item_lock_listview,null);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_item_lock_icon);
                holder.lock = (ImageView) convertView.findViewById(R.id.iv_item_lock_lock);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_lock_name);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 获取当前item数据
            AppInfo info = mAppInfos.get(position);

            // 设置数据显示
            holder.icon.setImageDrawable(info.getIcon());
            holder.name.setText(info.getAppName());

            // 加锁处理
            if(mListLock.contains(info.getPackageName())){
                holder.lock.setImageResource(R.drawable.lock);
                holder.lock.setTag(true);
            }else {
                holder.lock.setImageResource(R.drawable.unlock);
                holder.lock.setTag(false);
            }
            // 返回convertView

            return convertView;
        }

        class ViewHolder{
            ImageView icon;
            ImageView lock;
            TextView name;

        }
    }
}
