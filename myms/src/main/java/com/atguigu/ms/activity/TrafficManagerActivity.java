package com.atguigu.ms.activity;

import android.app.ProgressDialog;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.atguigu.ms.R;
import com.atguigu.ms.bean.TrafficInfo;
import com.atguigu.ms.util.MsUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrafficManagerActivity extends AppCompatActivity {
    private TextView tv_traffic_2g_3g;
    private TextView tv_traffic_wifi;
    private SlidingDrawer sd_traffic;
    private ListView lv_traffic;
    private ProgressDialog mPm;
    private List<TrafficInfo> mTrafficInfos;
    private long mMobileTraffic;
    private long mWifiTraffic;
    private TrafficAdapter mTrafficAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        tv_traffic_2g_3g = (TextView)findViewById(R.id.tv_traffic_2g_3g);
        tv_traffic_wifi = (TextView)findViewById(R.id.tv_traffic_wifi);
        sd_traffic = (SlidingDrawer)findViewById(R.id.sd_traffic);
        lv_traffic = (ListView)findViewById(R.id.lv_traffic);

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                mPm = ProgressDialog.show(TrafficManagerActivity.this,null,"正在加载中");
            }

            @Override
            protected Void doInBackground(Void... params) {
                mTrafficInfos = MsUtils.getAllTrafficInfos(TrafficManagerActivity.this);

                Collections.sort(mTrafficInfos, new Comparator<TrafficInfo>() {
                    @Override
                    public int compare(TrafficInfo lhs, TrafficInfo rhs) {
                        return (int) (lhs.getInSize()+lhs.getOutSize()-rhs.getInSize()-rhs.getOutSize());
                    }
                });

//得到手机总的下载流量和上传流量(2g/3g/wifi)
                long totalRxBytes = TrafficStats.getTotalRxBytes();  //receive
                long totalTxBytes = TrafficStats.getTotalTxBytes();
//得到手机的下载流量和上传流量(2g/3g)
                long mobileRxBytes = TrafficStats.getMobileRxBytes();
                long mobileTxBytes = TrafficStats.getMobileTxBytes();

                mMobileTraffic = mobileRxBytes+ mobileTxBytes;
                mWifiTraffic = totalRxBytes+ totalTxBytes - mMobileTraffic;

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mPm.dismiss();

                tv_traffic_2g_3g.setText("2G/3G流量：" + MsUtils.formatSize(TrafficManagerActivity.this, mMobileTraffic));
                tv_traffic_wifi.setText("WIFI流量："+MsUtils.formatSize(TrafficManagerActivity.this,mWifiTraffic));

                mTrafficAdapter = new TrafficAdapter();
                lv_traffic.setAdapter(mTrafficAdapter);

                sd_traffic.animateOpen();

            }
        }.execute();

    }

    private class TrafficAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTrafficInfos == null?0:mTrafficInfos.size();
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
           ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                 convertView = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_manager, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_traffic_icon);
                holder.name = (TextView) convertView.findViewById(R.id.tv_traffic_name);
                holder.inSize = (TextView) convertView.findViewById(R.id.tv_traffic_received);
                holder.outSize = (TextView) convertView.findViewById(R.id.tv_traffic_transmitted);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            TrafficInfo trafficInfo = mTrafficInfos.get(position);
            holder.icon.setImageDrawable(trafficInfo.getIcon());
            holder.name.setText(trafficInfo.getAppName());
            holder.inSize.setText(MsUtils.formatSize(TrafficManagerActivity.this, trafficInfo.getInSize()));
            holder.outSize.setText(MsUtils.formatSize(TrafficManagerActivity.this,trafficInfo.getOutSize()));

            return convertView;
        }

        class ViewHolder{
            ImageView icon;
            TextView name;
            TextView inSize;
            TextView outSize;
        }
    }
}
