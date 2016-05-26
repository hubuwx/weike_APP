package com.chenluwei.weike.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.adapter.FileListAdapter;
import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.db.ThreadDAO;
import com.chenluwei.weike.db.ThreadDaoImpl;
import com.chenluwei.weike.service.DownloadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadActivity extends AppCompatActivity {

    private DownloadFileInfo fileInfo = null;
    private ListView mLvFile = null;
    private List<DownloadFileInfo> mFileList = null;
    private FileListAdapter mAdapter = null;
    private ThreadDAO mDao = null;
    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        long time = System.currentTimeMillis();
        @Override
        public void onReceive(Context context, Intent intent) {

            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                Log.i("llllllllll", "55555");
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id",0);

                Log.i("download", fileInfo.toString());
                Log.i("download", "finished------" + finished);

                //这里和模板不一样，因为是接收广播才得到了文件信息
                if(System.currentTimeMillis() - time > 800) {
                    mAdapter.updateProgress(id,finished);
                    Log.i("llllllllll", "66666666");
                    time = System.currentTimeMillis();
                }
            }else if(DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                //下载完成
                //更新进度条为0
                fileInfo = (DownloadFileInfo) intent.getSerializableExtra("fileInfo");
                int id = fileInfo.getId();
                mAdapter.updateProgress(id,0);
                Toast.makeText(DownloadActivity.this, fileInfo.getFileName()+"下载完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        //也需要接收下载结束的广播
        filter.addAction(DownloadService.ACTION_FINISH);

        registerReceiver(mReceiver, filter);
        new AsyncTask<Void, Void, List<DownloadFileInfo>>(){

            @Override
            protected void onPreExecute() {
                //初始化控件对象
                mLvFile = (ListView)findViewById(R.id.lv_downLoad);

                Log.i("llllllllll", "1111111");
            }

            @Override
            protected List<DownloadFileInfo> doInBackground(Void... params) {
                //从数据库中获取文件信息
                mDao = new ThreadDaoImpl(DownloadActivity.this);
                Log.i("llllllllll", "222222");
                mFileList = new ArrayList<>();
                List<Map<String, Object>> fileSimpleInfos = mDao.getFileSimpleInfo();
                Log.i("llllllllll", "33333333");
                for (int i = 0; i<fileSimpleInfos.size();i++){
                    Log.i("llllllllll", "44444444");
                    Log.i("downloadfilesimple", "url" + fileSimpleInfos.get(i).get("url"));
                    String url = (String) fileSimpleInfos.get(i).get("url");
                    String fileName = (String) fileSimpleInfos.get(i).get("fileName");
                    int progress = (int) fileSimpleInfos.get(i).get("progress");
                    fileInfo = new DownloadFileInfo(i,url,fileName,0,progress);
                    mFileList.add(fileInfo);
                }
                return mFileList;
            }

            @Override
            protected void onPostExecute(List<DownloadFileInfo> mFileList) {
                //创建文件集合
                if(mFileList.size() != 0) {
                    mAdapter = new FileListAdapter(DownloadActivity.this,mFileList);
                    //设置适配器
                    mLvFile.setAdapter(mAdapter);
                }
            }
        }.execute();









    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
