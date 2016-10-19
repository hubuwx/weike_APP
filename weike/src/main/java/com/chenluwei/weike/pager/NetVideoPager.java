package com.chenluwei.weike.pager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.activity.MainActivity;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.bean.DownloadFileInfo;
import com.chenluwei.weike.bean.FileInfo;
import com.chenluwei.weike.bean.MediaItem;
import com.chenluwei.weike.bean.TabDetailInfo;
import com.chenluwei.weike.db.ThreadDaoImpl;
import com.chenluwei.weike.media.SystemVideoPlayer;
import com.chenluwei.weike.net.APIClient;
import com.chenluwei.weike.service.DownloadService;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.SpUtils;
import com.chenluwei.weike.util.TimeUtils;
import com.chenluwei.weike.util.Url;
import com.chenluwei.weike.view.XListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2016/4/20.
 */
public class NetVideoPager extends BasePager implements XListView.IXListViewListener {
    private XListView lv_video_pager;
    private TextView tv_nodata;
    private ProgressBar pb_loading;

    private ArrayList<MediaItem> mediaItems;
    private MyAdapter adapter;
    private FrameLayout fl_icon;
    private  ImageOptions imageOptions;

    //初始化顶部轮播图相关控件
    @ViewInject(R.id.vp_top_pager)
    private ViewPager vp_top_pager;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;

    private int page =0;//用来记载加载更多时json变化的后缀
    private ViewPager vp_main ;

    private ThreadDaoImpl mDao = null;
    private List imageViews = null;

    private int i;
    /**
     * 上一次被高亮显示的页面的下标位置
     */
    private int preSelectPosition;

    private String topJson;
    private List<TabDetailInfo.DataBean> topDatas = new ArrayList<>();
    private ArrayList<MediaItem> topMedias = new ArrayList<>();

    // 图片标题集合
    private final String[] imageDescriptions = new String[4];

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换到对应的ViewPager指定的页面
            vp_main.setCurrentItem(vp_main.getCurrentItem()+1);

            handler.sendEmptyMessageDelayed(0,3000);
        }
    };


    public NetVideoPager(Context context, String topJson) {
        super(context);
        this.topJson = topJson;
        //以下是从Xutils案例中复制来的设置代码
        imageOptions = new ImageOptions.Builder()

                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.

                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.home_scroll_default)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.home_scroll_default)//加载失败后默认显示图片
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);

        lv_video_pager = (XListView) view.findViewById(R.id.lv_video_pager);

        //初始化顶部轮播图的内容
        View topnewsView = View.inflate(context, R.layout.topnews, null);
        x.view().inject(this,topnewsView);
        lv_video_pager.setCacheColorHint(Color.TRANSPARENT);
        lv_video_pager.addHeaderView(topnewsView);
        //设置点击某一条的监听
       // lv_video_pager.setOnItemClickListener(new MyOnItemClickListener());
        //设置可以下拉
        lv_video_pager.setPullLoadEnable(true);
        //设置下拉的监听
        lv_video_pager.setXListViewListener(this);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        timeUtils = new TimeUtils();
        //获取文本缓存
        String json = SpUtils.getInstance(context).getString(Url.NET_VIDEO_URL, null);


        if(json != null) {
            Log.e("cccccc", json);
            processData(json);
        }
         //使用listView
        //1.在布局中定义或者在代码中创建
        //2.实例化ListView
        //3.准备数据
        getData();
        //4.设置适配器
        adapter = new MyAdapter();
        //5.写适配器item布局
    }

    /**
     * 得到手机里面的视频信息(通过内容提供者)
     */
    private void getData() {
        RequestParams params = new RequestParams(Url.NET_VIDEO_URL);
        x.http ().get(params, new Callback.CommonCallback<String>() {
            //当联网成功的时候回调这个方法
            @Override
            public void onSuccess(String result) {
                Log.e("Json", "onSuccess");
                // 解析数据
                SpUtils.getInstance(context).save(Url.NET_VIDEO_URL, result);

                processData(result);
            }

            /**
             * 当联网失败的时候请求
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //因为有缓存
                setAdapter();
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
          mediaItems = new ArrayList<>();


            TabDetailInfo tabDetailInfo = new Gson().fromJson(topJson, TabDetailInfo.class);
            //解析顶部轮播图的数据
            topDatas =  tabDetailInfo.getData();

           // 给顶部数据解析成mediam的类，以便于点击的时候传入播放器
                for (int i = 0;i<topDatas.size();i++) {
                    MediaItem mediaItem = new MediaItem();
                    TabDetailInfo.DataBean dataBean = topDatas.get(i);
                    mediaItem.setName(dataBean.getTitle());
                    topMedias.add(mediaItem);
                }
////                //第二层Json
////                String topVideoJson = Contants.LEFT_STRING+dataBean.getPlid()+Contants.right_string;
////                RequestParams topParams = new RequestParams();
////                x.http().get(topParams, new Callback.CommonCallback<Object>() {
////                    @Override
////                    public void onSuccess(Object result) {
////
////                    }
////
////                    @Override
////                    public void onError(Throwable ex, boolean isOnCallback) {
////
////                    }
////
////                    @Override
////                    public void onCancelled(CancelledException cex) {
////
////                    }
////
////                    @Override
////                    public void onFinished() {
////
////                    }
////                });
//            }

            //解析listview数据
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

                  //从本地缓存获取第二层Json
                   String videoJson = SpUtils.getInstance(context).getString(mediaItem.getPlid(),null);
                   if(videoJson != null) {
                       processVideoData(videoJson, mediaItem);
                   }

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
            pb_loading.setVisibility(View.GONE);
            //设置轮播图改变的监听
            vp_top_pager.setAdapter(new MyPagerAdapter());
            vp_top_pager.addOnPageChangeListener(new MyOnPageChangeListener());
            //开始循环切换ViewPager
            if(internalHandler == null) {
                internalHandler = new InternalHandler();
            }
            //因为会调用两次processdata，所以先把之前的移除掉
            internalHandler.removeCallbacksAndMessages(null);
            //重新发任务
            internalHandler.postDelayed(new MyRunnable(), 4000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    class MyRunnable implements Runnable{

        @Override
        public void run() {
            //在任务里面发消息
            internalHandler.sendEmptyMessage(0);
        }
    }

    private InternalHandler internalHandler;
    class InternalHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换到下一个页面
            int item = (vp_top_pager.getCurrentItem()+1)%5;
            vp_top_pager.setCurrentItem(item);
            //循环发送任务
            internalHandler.postDelayed(new MyRunnable(),4000);
        }
    }
    private void processMoreVideoData(String result, MediaItem mediaItem){
        String videoUrl = null;
        String des = null;
        try {
            //存储第二层json
           // SpUtils.getInstance(context).save("videoJson",result);
            JSONObject object2 = new JSONObject(result);
            Log.e("videoUrl", object2.toString());
            des = object2.optJSONObject("data").optString("description");
            JSONObject videoJson = (JSONObject) object2.optJSONObject("data").optJSONArray("videoList").get(0);
            //这是得到最终层的json对象

            videoUrl = (String) videoJson.opt("mp4HdUrl");
            mediaItem.setData(videoUrl);
            mediaItem.setTitle(des);
            mediaItems.add(mediaItem);

            Log.e("videoUrl", videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private void processVideoData(String result, MediaItem mediaItem) {



        String videoUrl = null;
        String des = null;
        try {
            //存储第二层json
            SpUtils.getInstance(context).save(mediaItem.getPlid(), result);
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

        //这个位置是否已经本地缓存过的标志
        boolean isbuffer = false;
        for (int i = 0;i<mediaItems.size();i++){
            String prePlid = mediaItems.get(i).getPlid();

            if(prePlid.equals(mediaItem.getPlid())) {
                //用网络获取的对象的plid在集合里遍历，如果已经存在，则只是替换，而不添加(终于解决了)
                mediaItems.set(i,mediaItem);
                isbuffer = true;
            }
        }

        //当确认没有进行过本地缓存，才在集合里添加(以免造成重复添加同一份)
        if(!isbuffer) {
            mediaItems.add(mediaItem);

            //填充适配器
            setAdapter();
        }else {
            adapter.notifyDataSetChanged();
        }

    }

    private void setAdapter() {

        if (mediaItems != null && mediaItems.size() > 0) {
            //设置适配器
            Log.e("ccccc", "setAdapter---enter");
//            pb_loading.setVisibility(View.GONE);
            tv_nodata.setVisibility(View.GONE);
            //填充适配器
            lv_video_pager.setAdapter(adapter);
            //填充顶部轮播图的适配器
            //添加底部的点
            addPoint();



        } else {
            tv_nodata.setVisibility(View.VISIBLE);
            //pb_loading.setVisibility(View.GONE);
        }
    }
    /**
     * 上一次高亮显示的位置
     */
    private int preSelection;
    private void addPoint() {
        //先移除之前所有的点
        ll_point_group.removeAllViews();
        //添加顶部新闻红点
        for (int i = 0;i<5;i++){
            ImageView point = new ImageView(context);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(com.chenluwei.weike.util.DensityUtil.dip2px(context, 5),com.chenluwei.weike.util.DensityUtil.dip2px(context, 5));
            point.setLayoutParams(params);
            if(i == 0) {
                point.setEnabled(true);//高亮显示
            }else {
                point.setEnabled(false);//
                params.leftMargin = com.chenluwei.weike.util.DensityUtil.dip2px(context, 8);
            }

            //添加到线性布局里
            ll_point_group.addView(point);
        }
        //设置默认的第一个文本
        tv_title.setText(topDatas.get(preSelection).getTitle());
    }
    private boolean isDraging = false;
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.i("TAccc","onPageScrolled");
            if(position > topDatas.size()) {
                return;
            }
            Log.i("TAccc","onPageScrolled能进来");
            tv_title.setText(topDatas.get(preSelection).getTitle());
            //把上一次的设置为默认
            ll_point_group.getChildAt(preSelection).setEnabled(false);
            //把当前的设置高亮
          ll_point_group.getChildAt(position).setEnabled(true);
            preSelection = position;
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_DRAGGING) {
                //拖拽，移除消息
                internalHandler.removeCallbacksAndMessages(null);
                isDraging = true;
            }else if(state == ViewPager.SCROLL_STATE_IDLE ) {
                //静止，发送消息
                isDraging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }else if(state == ViewPager.SCROLL_STATE_SETTLING) {
                isDraging=false;
                //滑动
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }
        }
    }
    class MyPagerAdapter extends PagerAdapter{

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(context);
            //设置默认图片
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            container.addView(imageView);
            //获取网络图片地址

            if(position < topDatas.size()) {

                String imgUrl = topDatas.get(position).getPicUrl();
                x.image().bind(imageView, imgUrl);
            }
//            设置触摸事件的监听
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //移除消息
                            internalHandler.removeCallbacksAndMessages(null);
                            //这里返回false是为了不消费掉，让点击监听能有响应
                            return false;

//                        当前控件(子控件，儿子)收到前驱事件(ACTION_MOVE或者ACTION_MOVE)后，
//                         它的父控件(老爸)突然插手，截断事件的传递，这时，当前控件就会收到ACTION_CANCEL
                        case MotionEvent.ACTION_UP:
                            //重新发消息
                            internalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
                    }
                    return false;
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("onClick", "onClick--------"+position);

                    String videoJson = Contants.LEFT_STRING + topDatas.get(position).getPlid()+Contants.right_string;
                    RequestParams params = new RequestParams(videoJson);
                    x.http().get(params, new Callback.CommonCallback<String>() {


                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONObject videoInfo = jsonObject.optJSONObject("data").optJSONArray("videoList").optJSONObject(0);
                                String videoUrl = (String) videoInfo.optString("mp4HdUrl");
                                topMedias.get(position).setData(videoUrl);
                                //传递视频列表给播放器
                                Intent intent = new Intent(context, SystemVideoPlayer.class);
                                //这里进行了序列化
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("videolist", topMedias);
                                //传播放列表
                                intent.putExtras(bundle);
                                //因为播放要知道是哪个视频，所以还要传一下位置(这里注意XlistView多算了一条，要减1)
                                intent.putExtra("position", position);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }



    TimeUtils timeUtils;



    /**
     * XListView下拉刷新时的监听
     */
    @Override
    public void onRefresh() {

        getData();//下拉刷新，其实就是重新请求数据
        onLoad();
    }

    /**
     * XListView加载更多时的监听
     */
    @Override
    public void onLoadMore() {
        Log.e("TAGaaa", "在第一个onLoadMore");
        page+=10;


                getMoreData();
                onLoad();

    }

    private void getMoreData() {

        RequestParams params = new RequestParams(Url.NET_VIDEO_URL+page);
        Log.e("RequestParams", Url.NET_VIDEO_URL+page);
        x.http().get(params, new Callback.CommonCallback<String>() {

            //当联网请求成功的时候回调这个方法
            @Override
            public void onSuccess(String result) {
                Log.e("RequestParams", result);
                //缓存
                // SpUtils.getInstance(context).save(Url.NET_VIDEO_URL,result);

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

            }

            /**
             * 取消的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {

            }

            //完成的时候回调
            @Override
            public void onFinished() {

            }
        });

    }

    private void processMoreData(String json) {
        try {


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

                    //从本地缓存获取第二层Json
//                    String videoJson = SpUtils.getInstance(context).getString("videoJson",null);
//                    if(videoJson != null) {
//                        processVideoData(videoJson, mediaItem);
//                    }

                    //联网第二层json
                    RequestParams params = new RequestParams(videoJsonString);
                    x.http().get(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e("more", "onSuccess22222");

                            processMoreVideoData(result, mediaItem);
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


        //刷新数据就行了
        adapter.notifyDataSetChanged();

    }

    private void onLoad() {
        lv_video_pager.stopRefresh();
        lv_video_pager.stopLoadMore();
        lv_video_pager.setRefreshTime("刚刚");
    }
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.e("sizeeeeee", mediaItems.size()+"");
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_net_videopager, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.iv_video_icon = (ImageView) convertView.findViewById(R.id.iv_video_icon);
                holder.bt_download = (Button) convertView.findViewById(R.id.bt_download);
                holder.pb_download_progress = (ProgressBar) convertView.findViewById(R.id.pb_download_progress);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final MediaItem mediaItem = mediaItems.get(position);
            Log.e("mediaItem", mediaItem.getName());
            Log.e("mediaItem", mediaItem.getCoverImg());
            Log.e("mediaItem", mediaItem.getData());
            Log.i("onloading",  "getview----"+mediaItem.getData());
            String name = mediaItem.getName();
            String des = mediaItem.getTitle();

            holder.tv_name.setText(name);
            holder.tv_desc.setText(des);
            x.image().bind(holder.iv_video_icon, mediaItem.getCoverImg(), imageOptions);
            pb_loading = holder.pb_download_progress;
            holder.iv_video_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //传递视频列表给播放器
                    Intent intent = new Intent(context, SystemVideoPlayer.class);
                    //这里进行了序列化
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videolist", mediaItems);
                    //传播放列表
                    intent.putExtras(bundle);
                    //因为播放要知道是哪个视频，所以还要传一下位置(这里注意XlistView多算了一条，要减1)
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });

            holder.bt_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setMessage("是否下载该视频？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(mediaItem.getData() == null) {
                                        Toast.makeText(context, "下载视频失败", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String fileName = mediaItem.getData().substring(mediaItem.getData().lastIndexOf("/") + 1);//f17.jpg
                                    mDao = new ThreadDaoImpl(context);
                                    List<Map<String, Object>> fileSimpleInfos = mDao.getFileSimpleInfo();
                                    for(int i = 0;i<fileSimpleInfos.size();i++){
                                        if(fileName.equals(fileSimpleInfos.get(i).get("fileName"))) {
                                           Toast.makeText(context, "亲！此视频已经加入下载列表", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    //创建文件信息对象
                                    int id = SpUtils.getInstance(context).getInt("fileId", 0);

                                    DownloadFileInfo downloadFileInfo = new DownloadFileInfo(id,mediaItem.getData(),fileName,0,0);
                                    id++;
                                    SpUtils.getInstance(context).save("fileId",id);

                                    downloadVideo(downloadFileInfo);


                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            });


            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_desc;
            ImageView iv_video_icon;
            Button bt_download;
            ProgressBar pb_download_progress;
        }
    }

    private void downloadVideo(DownloadFileInfo fileInfo) {
        //通过intent把参数传给Service
        Log.i("download", "downloadVideo()");
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_START);
        intent.putExtra("fileInfo", fileInfo);
        context.startService(intent);

    }




}
