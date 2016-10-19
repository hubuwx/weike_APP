package com.atguigu.ms.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.bean.ProcessInfo;
import com.atguigu.ms.util.MsUtils;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private RelativeLayout rl_process;
    private TextView tv_run_process_count;
    private TextView tv_avail_ram;
    private TextView tv_app_count;
    private ListView lv_taskmanager;
    private LinearLayout ll_loading;
    private List<ProcessInfo> mSysetmProcessInfos = new ArrayList<>();
    private List<ProcessInfo> mCustomProcessInfos= new ArrayList<>();
    private long mAvailMem;
    private long mTotalMem;
    private ActivityManager mAm;
    private ProcessAdapter mProcessAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        // 获取控件对象
        rl_process = (RelativeLayout)findViewById(R.id.rl_process);
        tv_run_process_count = (TextView)findViewById(R.id.tv_run_process_count);
        tv_avail_ram = (TextView)findViewById(R.id.tv_avail_ram);
        tv_app_count = (TextView)findViewById(R.id.tv_app_count);
        lv_taskmanager = (ListView)findViewById(R.id.lv_taskmanager);
        ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                ll_loading.setVisibility(View.VISIBLE);
                rl_process.setVisibility(View.INVISIBLE);
                tv_app_count.setVisibility(View.INVISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                MsUtils.getAllProcessInfos(ProcessManagerActivity.this,mSysetmProcessInfos,mCustomProcessInfos);
                for (ProcessInfo info:mCustomProcessInfos){
                    Log.i("TAGAA", info.toString());
                }

                for (ProcessInfo info:mSysetmProcessInfos){
                    Log.i("TAGAA",info.toString());
                }
                //可用内存大小
                mAvailMem = MsUtils.getAvailMem(ProcessManagerActivity.this);
                //总内存大小
                mTotalMem = MsUtils.getTotalMem(ProcessManagerActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ll_loading.setVisibility(View.INVISIBLE);
                rl_process.setVisibility(View.VISIBLE);
                tv_app_count.setVisibility(View.VISIBLE);

                tv_run_process_count.setText("进程数：" + (mSysetmProcessInfos.size() + mCustomProcessInfos.size()));
                //这里注意要格式化
                tv_avail_ram.setText("剩余/总内存："+MsUtils.formatSize(ProcessManagerActivity.this,mAvailMem)+"/"+MsUtils.formatSize(ProcessManagerActivity.this,mTotalMem));
                tv_app_count.setText("用户应用："+mCustomProcessInfos.size());

                mProcessAdapter = new ProcessAdapter();
                lv_taskmanager.setAdapter(mProcessAdapter);
            }
        }.execute();

        lv_taskmanager.setOnItemClickListener(this);

        lv_taskmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(mCustomProcessInfos == null) {
                    return;
                }
                if(firstVisibleItem <= mCustomProcessInfos.size()) {
                    tv_app_count.setText("用户进程："+mCustomProcessInfos.size());
                }else {
                    tv_app_count.setText("系统集成："+mSysetmProcessInfos.size());
                }
            }
        });
    }

    //监听点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0 || position == mCustomProcessInfos.size()+1) {
            return;
        }

        ProcessInfo info = (ProcessInfo) mProcessAdapter.getItem(position);

        //更新页面
        CheckBox cb = (CheckBox) view.findViewById(R.id.cb_item_task_status);
       cb.setChecked(!cb.isChecked());

        //更新内存
        info.setIsChecked(cb.isChecked());
        mProcessAdapter.notifyDataSetChanged();
    }

    //全选
    public void selectAll(View v) {
        for (ProcessInfo info:mCustomProcessInfos){
            info.setIsChecked(true);
        }

        for (ProcessInfo info:mSysetmProcessInfos){
            info.setIsChecked(true);
        }

        //记得更新
        mProcessAdapter.notifyDataSetChanged();
    }

    //反选
    public void unSelect(View v) {
        for (ProcessInfo info:mCustomProcessInfos){
            info.setIsChecked(!info.isChecked());
        }

        for (ProcessInfo info:mSysetmProcessInfos){
            info.setIsChecked(!info.isChecked());
        }

        mProcessAdapter.notifyDataSetChanged();
    }
    //一键清理
    public void killAll(View v) {
        int killCount = 0;
        long freeMemSize = 0;

        for (int i = 0; i < mCustomProcessInfos.size(); i++) {
            ProcessInfo info = mCustomProcessInfos.get(i);

            if (info.isChecked()) {
                killCount++;
                freeMemSize += info.getMemSize();

                mAm.killBackgroundProcesses(info.getPackageName());

                mCustomProcessInfos.remove(i);
                i--;
            }
        }
        for (int i = 0;i<mSysetmProcessInfos.size();i++){
            ProcessInfo info = mSysetmProcessInfos.get(i);
            if(info.isChecked()) {
                killCount++;
                freeMemSize+= info.getMemSize();

                mAm.killBackgroundProcesses(info.getPackageName());

                mSysetmProcessInfos.remove(i);
                i--;
            }
        }
        tv_run_process_count.setText("进程数:" + (mSysetmProcessInfos.size() + mCustomProcessInfos.size()));
        mAvailMem +=freeMemSize;
        tv_avail_ram.setText("剩余/总内存:" + MsUtils.formatSize(ProcessManagerActivity.this, mAvailMem) + "/" +
                MsUtils.formatSize(ProcessManagerActivity.this, mTotalMem));
        tv_app_count.setText("用户进程:"+mCustomProcessInfos.size());


        mProcessAdapter.notifyDataSetChanged();

        MsUtils.showMsg(ProcessManagerActivity.this,"杀死"+killCount+"进程,释放"+MsUtils.formatSize(ProcessManagerActivity.this,freeMemSize)+"内存");
    }
    //
     class ProcessAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mCustomProcessInfos.size()+mSysetmProcessInfos.size()+2;
        }

        @Override
        public Object getItem(int position) {
            if(position == 0) {
                return "用户进程："+mCustomProcessInfos.size();
            }else if(position <= mCustomProcessInfos.size()) {
                return  mCustomProcessInfos.get(position - 1);
            }else if(position == mCustomProcessInfos.size()+1) {
                return "系统进程"+mSysetmProcessInfos.size();
            }else {
                return mSysetmProcessInfos.get(position - mCustomProcessInfos.size() -2);
            }

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object itemData = getItem(position);
            if(position == 0 || position == mCustomProcessInfos.size()+1) {
                TextView tv = (TextView) View.inflate(ProcessManagerActivity.this, R.layout.app_count, null);
                tv.setText((String)itemData);
                return tv;
            }

            ViewHolder holder;
            if(convertView == null||convertView instanceof TextView) {
                holder = new ViewHolder();
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_process_manger,null);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_item_task_logo);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_task_name);
                holder.memSize = (TextView) convertView.findViewById(R.id.tv_item_task_mem);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_item_task_status);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            ProcessInfo pInfo = (ProcessInfo) itemData;
            holder.icon.setImageDrawable(pInfo.getIcon());
            holder.name.setText(pInfo.getAppName());
            holder.memSize.setText(MsUtils.formatSize(ProcessManagerActivity.this, pInfo.getMemSize()));

            holder.checkBox.setChecked(pInfo.isChecked());
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView  memSize;
            CheckBox checkBox;
        }
    }
}
