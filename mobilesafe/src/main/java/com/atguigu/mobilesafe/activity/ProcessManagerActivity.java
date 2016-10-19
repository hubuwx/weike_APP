package com.atguigu.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.ProcessInfo;
import com.atguigu.mobilesafe.util.MsUtils;

import java.util.ArrayList;
import java.util.List;

// 进程管理页面
public class ProcessManagerActivity extends Activity implements AdapterView.OnItemClickListener{
    private RelativeLayout rl_process_info;
    private TextView tv_run_process_count;
    private TextView tv_avail_ram;
    private ListView lv_taskmanager;
    private LinearLayout ll_loading;
    private TextView tv_process_count;

    private List<ProcessInfo> userInfos = new ArrayList<>();
    private List<ProcessInfo> systemInfos = new ArrayList<>();
    private long totalMemSize;
    private long availMemSize;
    private ProcessAdapter adapter;
    private ActivityManager activityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        // 初始化控件
        rl_process_info = (RelativeLayout)findViewById(R.id.rl_process_info);
        tv_run_process_count = (TextView) findViewById(R.id.tv_run_process_count);
        tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
        lv_taskmanager = (ListView) findViewById(R.id.lv_taskmanager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);


        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                ll_loading.setVisibility(View.VISIBLE);
                rl_process_info.setVisibility(View.GONE);
                tv_process_count.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                MsUtils.getAllProcessInfos(ProcessManagerActivity.this, systemInfos, userInfos);
                totalMemSize = MsUtils.getTotalMem(ProcessManagerActivity.this);
                availMemSize = MsUtils.getAvailMem(ProcessManagerActivity.this);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ll_loading.setVisibility(View.GONE);
                rl_process_info.setVisibility(View.VISIBLE);
                tv_process_count.setVisibility(View.VISIBLE);
                tv_process_count.setText("用户进程: " + userInfos.size());
                tv_run_process_count.setText("进程数: "+(userInfos.size()+systemInfos.size()));
                tv_avail_ram.setText("剩余/总内存:"+MsUtils.formatSize(ProcessManagerActivity.this, availMemSize)
                        +"/"+MsUtils.formatSize(ProcessManagerActivity.this, totalMemSize));
                adapter = new ProcessAdapter();
                lv_taskmanager.setAdapter(adapter);
            }
        }.execute();

        lv_taskmanager.setOnItemClickListener(this);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

    }

    public void selectAll(View v) {
        //更新内存
        for (ProcessInfo info : userInfos) {
            info.setChecked(true);
        }
        for (ProcessInfo info : systemInfos) {
            info.setChecked(true);
        }
        //更新界面
        adapter.notifyDataSetChanged();
    }

    public void unSelect(View v) {
        //更新内存
        for (ProcessInfo info : userInfos) {
            info.setChecked(!info.isChecked());
        }
        for (ProcessInfo info : systemInfos) {
            info.setChecked(!info.isChecked());
        }
        //更新界面
        adapter.notifyDataSetChanged();
    }

    public void killAll(View v) {
        int killCount = 0; //将要被杀死的进程数
        long freeMemSize = 0;//释放的空间大小

        for (int i = 0; i < userInfos.size(); i++) {
            ProcessInfo processInfo = userInfos.get(i);
            if (processInfo.isChecked()) {
                killCount++;
                freeMemSize += processInfo.getMemSize();
                userInfos.remove(i);
                //杀进程
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                //保证下一个元素会被遍历到
                i--;
            }
        }

        for (int i = 0; i < systemInfos.size(); i++) {
            ProcessInfo processInfo = systemInfos.get(i);
            if (processInfo.isChecked()) {
                killCount++;
                freeMemSize += processInfo.getMemSize();
                systemInfos.remove(i);
                //杀进程
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                //保证下一个元素会被遍历到
                i--;
            }
        }

        //更新界面
        tv_process_count.setText("用户进程: " + userInfos.size());
        tv_run_process_count.setText("进程数: "+(userInfos.size()+systemInfos.size()));
        availMemSize += freeMemSize;
        tv_avail_ram.setText("剩余/总内存:"+MsUtils.formatSize(ProcessManagerActivity.this, availMemSize)
                +"/"+MsUtils.formatSize(ProcessManagerActivity.this, totalMemSize));
        adapter.notifyDataSetChanged();
        MsUtils.showMsg(this, "杀死"+killCount+"个进程, 释放"+MsUtils.formatSize(this, freeMemSize)+"空间!");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 || position == userInfos.size() + 1) {
            return;
        }
        //更新界面
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_item_task_status);
        checkBox.setChecked(!checkBox.isChecked());
        //checkBox.toggle();
        //更新内存
        ProcessInfo processInfo = (ProcessInfo) adapter.getItem(position);
        processInfo.setChecked(checkBox.isChecked());
    }

    class ProcessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userInfos.size()+systemInfos.size()+2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return "用户进程: "+userInfos.size();
            } else if (position <= userInfos.size()) {
                return userInfos.get(position - 1);
            } else if (position == userInfos.size() + 1) {
                return "系统进程: "+systemInfos.size();
            } else {
                return systemInfos.get(position - userInfos.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //得到数据
            Object itemData = getItem(position);

            //返回TextView
            if (position == 0 || position == userInfos.size() + 1) {
                TextView textView = (TextView) View.inflate(ProcessManagerActivity.this, R.layout.app_count, null);
                textView.setText((String)itemData);
                return textView;
            }

            //返回RelativeLayout
            ViewHolder holder = null;
            if (convertView == null || convertView instanceof TextView) {
                holder = new ViewHolder();
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_process_manger, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_task_logo);
                holder.nameTV = (TextView) convertView.findViewById(R.id.tv_item_task_name);
                holder.sizeTV = (TextView) convertView.findViewById(R.id.tv_item_task_mem);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_item_task_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ProcessInfo processInfo = (ProcessInfo) itemData;

            holder.imageView.setImageDrawable(processInfo.getIcon());
            holder.nameTV.setText(processInfo.getAppName());
            holder.sizeTV.setText(MsUtils.formatSize(ProcessManagerActivity.this, processInfo.getMemSize()));

            holder.checkBox.setChecked(processInfo.isChecked());


            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTV;
            TextView sizeTV;
            CheckBox checkBox;
        }
    }
}
