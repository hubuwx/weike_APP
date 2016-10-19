package com.chenluwei.weike.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.db.ThreadDaoImpl;
import com.chenluwei.weike.media.SystemVideoPlayer;
import com.chenluwei.weike.util.SpUtils;
import com.chenluwei.weike.util.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    EditText et_serach;
    ImageView iv_search;
    TextView tv_search_go;
    ListView listView;
    TextView tv_search_info;
    private ArrayList<MediaItem> mediaItems;
    private MyAdapter adapter;
    private ImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(70))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.vedio_default)
                .setFailureDrawableId(R.drawable.vedio_default)
                .build();

        et_serach = (EditText)findViewById(R.id.et_serach);
        iv_search = (ImageView)findViewById(R.id.iv_search);
        tv_search_go = (TextView)findViewById(R.id.tv_search_go);
        listView = (ListView)findViewById(R.id.listView);
        tv_search_info = (TextView)findViewById(R.id.tv_search_info);

        iv_search.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_search:
                    break;
                case R.id.tv_search_go:
                    getData();
                    break;
            }
        }
    }

    private void getData() {
        RequestParams params = new RequestParams(Url.NET_VIDEO_URL);
        x.http ().get(params, new Callback.CommonCallback<String>() {
            //当联网成功的时候回调这个方法
            @Override
            public void onSuccess(String result) {
                Log.e("Json", "onSuccess");
                // 解析数据

                processData(result);
            }

            /**
             * 当联网失败的时候请求
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(SearchActivity.this, "联网失败", Toast.LENGTH_SHORT).show();
            }

            /**
             * 取消的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {

            }

            /**
             * 完成的时候回调
             */
            @Override
            public void onFinished() {

            }
        });

    }

    private void processData(String json) {
        try {
            //集合创建放到这里是为了防止本地缓存数据重复添加
            mediaItems = new ArrayList<MediaItem>();

            JSONObject object = new JSONObject(json);//目前的结构是：对象里套数组又套对象
            JSONArray jsonArray = object.optJSONArray("data");
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if(jsonObject != null) {
                    final MediaItem mediaItem = new MediaItem();
                    //视频描述
                    String description = jsonObject.optString("description");
                    mediaItem.setTitle(description);//注意这里是用title存储描述，不是写错
                    //视频标题
                    String title = jsonObject.optString("title");
                    mediaItem.setName(title);
                    //视频图片url
                    String picUrl = jsonObject.optString("picUrl");
                    mediaItem.setCoverImg(picUrl);
                    //视频创建时间
                    long createTime = jsonObject.optLong("publishTime");
                    Log.i("ccccc", "createTime"+createTime);
                    mediaItem.setCreateTime(createTime);
                    //视频时长
                    String quantity = jsonObject.optString("quantity");
                    mediaItem.setQuantity(quantity);//这里不用duration字段，因为这里是字符串型
                    //视频观看总人数
                    Long viewcount = jsonObject.optLong("viewcount");
                    mediaItem.setCountView(viewcount);
                    //这里是用来查找相应的二级json的id
                    final String plid = jsonObject.optString("plid");
                    mediaItem.setPlid(plid);
                    Log.e("ccccc", mediaItem.toString());
                    //定义二层json
                    String videoJsonString = "http://c.open.163.com/mob/" + plid + "/getMoviesForAndroid.do";



                    //发送消息
                    RequestParams params = new RequestParams(videoJsonString);
                    x.http().get(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e("videoUrl", "onSuccess22222");

                            processVideoData(result, mediaItem);
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Log.e("videoUrl", "onError22222");

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });


                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processVideoData(String result, MediaItem mediaItem) {
        String videoUrl = null;
        String des = null;
        try {

            JSONObject object2 = new JSONObject(result);
            des = object2.optJSONObject("data").optString("description");
            JSONObject videoJson = (JSONObject) object2.optJSONObject("data").optJSONArray("videoList").get(0);
            //这是得到最终层的json对象

            videoUrl = (String) videoJson.opt("mp4HdUrl");
            mediaItem.setData(videoUrl);
            Log.e("videoUrl",videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mediaItem.setTitle(des);
        mediaItem.setData(videoUrl);
        Log.e("cccc", mediaItem.getData());




            mediaItems.add(mediaItem);

            //填充适配器
            setAdapter();


    }

    private void setAdapter() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //设置适配器
            Log.e("ccccc", "setAdapter---enter");

            //填充适配器
            listView.setAdapter(adapter);



        } else {
           tv_search_info.setVisibility(View.VISIBLE);
        }
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mediaItems.size();
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
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(SearchActivity.this,R.layout.item_search,null);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.iv_video_icon = (ImageView) convertView.findViewById(R.id.iv_video_icon);
                viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();

            }

            //根据位置得到对应数据
            MediaItem mediaItem = mediaItems.get(position);
            viewHolder.tv_name.setText(mediaItem.getName());
            viewHolder.tv_desc.setText(mediaItem.getTitle());

            //加载图片
            x.image().bind(viewHolder.iv_video_icon, mediaItem.getCoverImg(), imageOptions);

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_video_icon;
        TextView tv_name;
        TextView tv_desc;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//            MediaItem mediaItem = mediaItems.get(position);
//            Toast.makeText(context,mediaItem.toString(),Toast.LENGTH_SHORT).show();
            //吊起系统自带的播放器播放视频
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //传一个播放地址给视频播放器播放
//            Intent intent = new Intent(context, SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //传递视频列表给播放器
            Intent intent = new Intent(SearchActivity.this, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);

            intent.putExtra("position",position);

            startActivity(intent);

        }
    }

}
