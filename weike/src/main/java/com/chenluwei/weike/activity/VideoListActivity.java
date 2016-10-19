package com.chenluwei.weike.activity;

import android.app.Activity;
import android.content.Intent;
import android.drm.ProcessedData;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.media.SystemVideoPlayer;
import com.chenluwei.weike.util.DensityUtil;
import com.chenluwei.weike.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by lw on 2016/6/11.
 */
public class VideoListActivity extends Activity {
    private ListView lv_videoList;
    ArrayList<MediaItem> mediaItes;
    private VideoAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);
        initView();
        initData();
    }

    private void initData() {
        adapter = new VideoAdapter();
        mediaItes = new ArrayList<>();
        String videoJsonUrl = getIntent().getStringExtra("videoJsonUrl");
        getDataFromNet(videoJsonUrl);
    }

    private void getDataFromNet(String videoJsonUrl) {
        RequestParams params = new RequestParams(videoJsonUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "聯網成功");
                processedData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processedData(String json) {
       mediaItes = peaseJson(json);
       lv_videoList.setAdapter(adapter);
    }

    private ArrayList<MediaItem> peaseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.optJSONObject("data");
            JSONArray videoList = data.optJSONArray("videoList");
            for (int i = 0;i<videoList.length();i++){
                MediaItem mediaItem = new MediaItem();
                JSONObject videoJson = (JSONObject) videoList.get(i);
                String title = videoJson.optString("title");
                //v-mp4SdUrl : "http://mov.bn.netease.com/open-movie/nos/mp4/2016/04/25/SBKJ0L3MR_sd.mp4"
                String url = videoJson.optString("mp4SdUrl");
                String webUrl = videoJson.getString("webUrl");
                mediaItem.setName(title);
                mediaItem.setData(url);
                mediaItem.setDes(webUrl);
                mediaItems.add(mediaItem);
            }
            return mediaItems;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lv_videoList = (ListView)findViewById(R.id.lv_videoList);

        lv_videoList.setOnItemClickListener(new  MyOnItemClickListener());
        lv_videoList.setOnItemLongClickListener(new MyOnItemLongClickListener());
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 如果已经开启,则先关闭掉
            if (pw != null && pw.isShowing() ) {
                pw.dismiss();
                return;
            }
            //传递视频列表给播放器
            Intent intent = new Intent(VideoListActivity.this, SystemVideoPlayer.class);
            //这里进行了序列化
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",  mediaItes);
            //传播放列表
            intent.putExtras(bundle);
            //因为播放要知道是哪个视频，所以还要传一下位置(这里注意XlistView多算了一条，要减1)
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }


    private class VideoAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mediaItes.size();
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
                convertView = View.inflate(VideoListActivity.this,R.layout.item_video_play,null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_video_name);
//                holder.iv_play = (ImageView) convertView.findViewById(R.id.iv_play);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            MediaItem mediaItem = mediaItes.get(position);
            Log.e("TAG222222", "第"+(position+1)+"集:"+mediaItem.getName());
            Log.e("TAG222222", holder.toString());
            holder.tv_name.setText("第"+(position+1)+"集:"+mediaItem.getName());

            return convertView;
        }

        class ViewHolder{
            TextView tv_name;
//            ImageView iv_play;
        }
    }
    private PopupWindow pw;
    private View pwView;
    private int position;


    private void showShare(int position) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        if(mediaItes.get(position).getName() == null ||mediaItes.get(position).getDes()== null ) {
            Toast.makeText(VideoListActivity.this, "有值为空", Toast.LENGTH_SHORT).show();

        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("这里是标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mediaItes.get(position).getDes());
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.lagou.com/upload/logo/2651276c8b3940ddb0dedd35f6f0729d.jpg");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mediaItes.get(position).getDes());

// 启动分享GUI
        oks.show(this);
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            // 创建
            if (pw == null) {
                pwView = View.inflate(VideoListActivity.this, R.layout.popupwindow_layout, null);
                // 点击事件

                pwView.findViewById(R.id.ll_soft_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (pw != null && pw.isShowing()) {
                            pw.dismiss();
                        }
                        showShare(position);

                    }
                });

                pw = new PopupWindow(pwView, DensityUtil.dip2px(VideoListActivity.this,80), view.getHeight() + 40);
                pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景色为透明
            }

            // 如果已经开启,则先关闭掉
            if (pw.isShowing()) {
                pw.dismiss();
            }

            // 显示位置设置
            pw.showAsDropDown(view, 200, -view.getHeight() - 20);

            // 显示动画
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
            scaleAnimation.setDuration(500);
            pwView.startAnimation(scaleAnimation);

            return true;
        }


    }

}
