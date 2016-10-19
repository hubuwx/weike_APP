package com.atguigu.mobilesafe.activity;

import android.app.Activity;
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

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.bean.TrafficInfo;
import com.atguigu.mobilesafe.util.MsUtils;

import java.util.Collections;
import java.util.List;

//流量统计界面
public class TrafficManagerActivity extends Activity {
    private TextView tv_traffic_2g_3g;
    private TextView tv_traffic_wifi;
    private SlidingDrawer sd_traffic;
    private ListView lv_traffic;
    private List<TrafficInfo> data;
    private TrafficAdapter adapter;
    private ProgressDialog pd;

    private long mobileTraffic;
    private long wifiTraffic;
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
                pd = ProgressDialog.show(TrafficManagerActivity.this, null, "正在加载中...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                data = MsUtils.getAllTrafficInfos(TrafficManagerActivity.this);
                //对data进行排序(按总的流量大小)
                Collections.sort(data);
                /*
                Collections.sort(data, new Comparator<TrafficInfo>() {
                    @Override
                    public int compare(TrafficInfo lhs, TrafficInfo rhs) {
                        return (int) (lhs.getOutSize()+lhs.getInSize()-rhs.getOutSize()-rhs.getInSize());
                    }
                });
                */
                //得到手机总的下载流量和上传流量(2g/3g/wifi)
                long totalRxBytes = TrafficStats.getTotalRxBytes();  //receive
                long totalTxBytes = TrafficStats.getTotalTxBytes();
                //得到手机的下载流量和上传流量(2g/3g)
                long mobileRxBytes = TrafficStats.getMobileRxBytes();
                long mobileTxBytes = TrafficStats.getMobileTxBytes();

                mobileTraffic = mobileRxBytes+mobileTxBytes;
                wifiTraffic = totalRxBytes+totalTxBytes-mobileTraffic;

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                tv_traffic_2g_3g.setText("2G/3G流量: "+MsUtils.formatSize(TrafficManagerActivity.this, mobileTraffic));
                tv_traffic_wifi.setText("WIFI流量: "+ MsUtils.formatSize(TrafficManagerActivity.this, wifiTraffic));

                adapter = new TrafficAdapter();
                lv_traffic.setAdapter(adapter);
                pd.dismiss();

                //带动画打开sd_traffic
                sd_traffic.animateOpen();
            }
        }.execute();
    }

    class TrafficAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
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
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_manager, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_traffic_icon);
                holder.nameTV = (TextView) convertView.findViewById(R.id.tv_traffic_name);
                holder.outTV = (TextView) convertView.findViewById(R.id.tv_traffic_transmitted);
                holder.inTV = (TextView) convertView.findViewById(R.id.tv_traffic_received);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TrafficInfo info = data.get(position);

            holder.imageView.setImageDrawable(info.getIcon());
            holder.nameTV.setText(info.getAppName());
            holder.outTV.setText(MsUtils.formatSize(TrafficManagerActivity.this, info.getOutSize()));
            holder.inTV.setText(MsUtils.formatSize(TrafficManagerActivity.this, info.getInSize()));

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTV;
            TextView outTV;
            TextView inTV;
        }
    }
}
