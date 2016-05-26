package com.chenluwei.weike.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.service.DownloadService;

import java.util.List;

/**
 * Created by lw on 2016/5/9.
 */
public class FileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<DownloadFileInfo> mList;

    public FileListAdapter(Context context, List<DownloadFileInfo> fileInfos)
    { Log.i("uuuuuuuu", "1111111");
        this.mContext = context;
        this.mList = fileInfos;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DownloadFileInfo fileInfo = mList.get(position);
        Log.i("uuuuuuuu", "2222222");
        if(convertView == null) {
            Log.i("uuuuuuuu", "3333333");
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_download,null);
            holder.mFileName =(TextView) convertView.findViewById(R.id.tv_fileName);
            holder.mProgressBar= (ProgressBar) convertView.findViewById(R.id.pb_progress);
            holder.mStartBtn =  (ImageView) convertView.findViewById(R.id.btn_start);
            holder.mStopBtn =  (ImageView) convertView.findViewById(R.id.btn_stop);
            holder.mProgressBar.setMax(100);
            holder.mFileName.setText(fileInfo.getFileName());
            convertView.setTag(holder);
        }else {
            Log.i("uuuuuuuu", "44444444");
            holder = (ViewHolder) convertView.getTag();
        }


        holder.mProgressBar.setProgress(fileInfo.getFinished());
        holder.mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileInfo == null) {
                    Log.i("download", "return了");
                    return;
                }
            Log.i("uuuuuuuu", "55555555");
                // 通过Intent传递参数给Service
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                mContext.startService(intent);
            }
        });

        holder.mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileInfo == null) {
                    Log.i("download", "return了");
                    return;
                }
                // 通过Intent传递参数给Service
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileInfo", fileInfo);
                mContext.startService(intent);
            }
        });
        return convertView;
        
    }

    /**
     * 更新列表项中的进度条
     */
    public  void updateProgress(int id,int progress){
        DownloadFileInfo fileInfo = mList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }
    static class ViewHolder{
        TextView mFileName;
        ProgressBar mProgressBar;
        ImageView mStartBtn;
        ImageView mStopBtn;

    }
}
