package com.atguigu.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.SystemVideoPlayer;
import com.atguigu.mobileplayer.Utils.CacheUtils;
import com.atguigu.mobileplayer.Utils.Url;
import com.atguigu.mobileplayer.Utils.Utils;
import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/4/20 10:45
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：网络视频
 */
public class NetVideoPager extends BasePager {

    private static final String TAG = NetVideoPager.class.getSimpleName();
    private XListView listview;
    private  MyAdapter myAdapter;

    private TextView tv_nodata;
    private ProgressBar pb_loading;

    private ArrayList<MediaItem> mediaItems;
    private Utils utils;

    private  ImageOptions imageOptions;
    public NetVideoPager(Context context) {
        super(context);
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
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager,null);
        listview = (XListView) view.findViewById(R.id.listview);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置点击某一条的监听
        listview.setOnItemClickListener(new MyOnItemClickListener());
        listview.setPullLoadEnable(true);//设置可以下拉
        listview.setXListViewListener(new MyIXListViewListener());
        return view;
    }

    class MyIXListViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            getData();//下拉刷新，其实就是重新请求数据
            onLoad();
        }

        @Override
        public void onLoadMore() {
            getMoreData();
            onLoad();

        }
    }

    private void getMoreData() {
        {

            RequestParams params = new RequestParams(Url.NET_VIDEO_URL);
            x.http().get(params, new Callback.CommonCallback<String>() {

                //当联网请求成功的时候回调这个方法
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, "联网请求成功==" + result);
                    CacheUtils.putString(context, Url.NET_VIDEO_URL, result);
                    //解析数据
                    processMoreData(result);

                }

                /**
                 * 失败的时候回调
                 * @param ex
                 * @param isOnCallback
                 */
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e(TAG,"联网请求失败=="+ex);
                }

                /**
                 * 取消的时候回调
                 * @param cex
                 */
                @Override
                public void onCancelled(CancelledException cex) {
                    Log.e(TAG,"onCancelled=="+cex);
                }

                //完成的时候回调
                @Override
                public void onFinished() {
                    Log.e(TAG,"onFinished==");
                }
            });

        }
    }

    @Override
    public void initData() {
        super.initData();
        utils = new Utils();
        mediaItems = new ArrayList<>();
        System.out.println("视频页面的数据被初始化了...");
        String json = CacheUtils.getString(context,Url.NET_VIDEO_URL);
        if(!TextUtils.isEmpty(json)){
            processData(json);
        }
        //使用ListView
        //1.在布局中定义或者在代码中创建
        //2.实例化ListView
        //3.准备数据
        getData();
        //4.设置适配器
        //5.写适配器item布局
    }

    /**
     * 得到手机里面视频信息
     */
    private void getData() {

        RequestParams params = new RequestParams(Url.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {

            //当联网请求成功的时候回调这个方法
            @Override
            public void onSuccess(String result) {
                Log.e(TAG,"联网请求成功=="+result);
                CacheUtils.putString(context,Url.NET_VIDEO_URL,result);
                //解析数据
                processData(result);

            }

            /**
             * 失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG,"联网请求失败=="+ex);
                setAdapter();
            }

            /**
             * 取消的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG,"onCancelled=="+cex);
            }

            //完成的时候回调
            @Override
            public void onFinished() {
                Log.e(TAG,"onFinished==");
            }
        });

    }

    private void processMoreData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("trailers");
            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                if(jsonObject != null){
                    MediaItem mediaItem = new MediaItem();
                    String coverImg = jsonObject.optString("coverImg");//图片地址
                    mediaItem.setCoverImg(coverImg);
                    String url = jsonObject.optString("url");//视频播放地址
                    mediaItem.setData(url);
                    String movieName = jsonObject.optString("movieName");
                    mediaItem.setName(movieName);
                    String videoTitle = jsonObject.optString("videoTitle");
                    mediaItem.setVideoTitle(videoTitle);

                    mediaItems.add(mediaItem);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //刷新数据就行了
        myAdapter.notifyDataSetChanged();//刷新
    }

    private void onLoad() {
        listview.stopRefresh();
        listview.stopLoadMore();
        listview.setRefreshTime("刚刚");
    }

    /**
     * 解析和处理数据
     * @param json
     */
    private void processData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("trailers");
            for(int i=0;i<jsonArray.length();i++){

                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                if(jsonObject != null){
                    MediaItem mediaItem = new MediaItem();
                    String coverImg = jsonObject.optString("coverImg");//图片地址
                    mediaItem.setCoverImg(coverImg);
                    String url = jsonObject.optString("url");//视频播放地址
                    mediaItem.setData(url);
                    String movieName = jsonObject.optString("movieName");
                    mediaItem.setName(movieName);
                    String videoTitle = jsonObject.optString("videoTitle");
                    mediaItem.setVideoTitle(videoTitle);

                    mediaItems.add(mediaItem);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setAdapter();

    }

    private void setAdapter() {
        if(mediaItems != null && mediaItems.size() >0){
            //设置适配器
            myAdapter = new MyAdapter();
            listview.setAdapter(myAdapter);
            tv_nodata.setVisibility(View.GONE);
            pb_loading.setVisibility(View.GONE);
        }else{
            tv_nodata.setVisibility(View.VISIBLE);
            pb_loading.setVisibility(View.GONE);
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
                convertView = View.inflate(context,R.layout.item_netvideo2,null);
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
            viewHolder.tv_desc.setText(mediaItem.getVideoTitle());

            //加载图片
            x.image().bind(viewHolder.iv_video_icon,mediaItem.getCoverImg(),imageOptions);

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
            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);

            intent.putExtra("position",position);

            context.startActivity(intent);

        }
    }
}
