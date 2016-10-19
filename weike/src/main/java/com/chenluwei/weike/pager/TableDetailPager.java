package com.chenluwei.weike.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenluwei.weike.R;
import com.chenluwei.weike.activity.VideoListActivity;
import com.chenluwei.weike.base.BasePager;
import com.chenluwei.weike.bean.TabDetailInfo;
import com.chenluwei.weike.util.Contants;
import com.chenluwei.weike.util.SpUtils;
import com.chenluwei.weike.util.TimeUtils;
import com.chenluwei.weike.util.Url;
import com.chenluwei.weike.view.XListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.security.Policy;
import java.util.List;

/**
 * Created by lw on 2016/6/9.
 */
public class TableDetailPager extends BasePager implements XListView.IXListViewListener {
    @ViewInject(R.id.lv_tabdetail_pager)
    private XListView lv_tabdetail_pager;

    private TabDetailPagerAdapter adapter;
    /**
     * 该页面Json的URl
     */
    String jsonUrl;
    //判断是否在加载更多
    private boolean isLoadMore;
    List<TabDetailInfo.DataBean> contentLists;
    private ImageOptions imageOptions;
    private int page = 0; //用来记载加载更多时json变化的后缀


    public TableDetailPager(Context context, String jsonUrl) {
        super(context);
        this.jsonUrl = jsonUrl;
        imageOptions = new ImageOptions.Builder()
                .setSize(org.xutils.common.util.DensityUtil.dip2px(110), org.xutils.common.util.DensityUtil.dip2px(90))
                .setRadius(org.xutils.common.util.DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
//                .setLoadingDrawableId(R.drawable.news_pic_default)
//                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        x.view().inject(this, view);
        //设置可以下拉
        lv_tabdetail_pager.setPullLoadEnable(true);
        lv_tabdetail_pager.setPullRefreshEnable(true);
        //设置下拉的监听
        lv_tabdetail_pager.setXListViewListener(this);
        lv_tabdetail_pager.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    /**
     * 点击事件的监听，进入分集列表
     */
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(context, "position==="+(position-1), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, VideoListActivity.class);
            //获取第二层Json并传到分集列表界面
            String videoJsonUrl = Contants.LEFT_STRING+contentLists.get(position-1).getPlid()+Contants.right_string;
            intent.putExtra("videoJsonUrl",videoJsonUrl);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        adapter = new TabDetailPagerAdapter();
        String saveJson = SpUtils.getInstance(context).getString(jsonUrl, null);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(jsonUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess" + result);
                //CacheUtils.putString(context, url, result);//缓存数据
                SpUtils.getInstance(context).save(jsonUrl, result);
                processData(result);
                // lv_tabdetail_pager.onRefreshFinish(true);//更新刷新时间
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError" + ex.getMessage());
                // lv_tabdetail_pager.onRefreshFinish(false);//
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("TAG", "onCancelled" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.e("TAG", "onFinished");
            }
        });
    }


    /**
     * 解析json数据，把数据绑定到ui上
     *
     * @param json
     */
    private void processData(String json) {
        //解析数据
        TabDetailInfo tabDetailInfo = new Gson().fromJson(json, TabDetailInfo.class);
        if(!isLoadMore) {

            //获取新闻列表数据

        contentLists = tabDetailInfo.getData();


            //设置新闻列表适配器
            lv_tabdetail_pager.setAdapter(adapter);
        }else {
            //加载更多(待补充)
            //contentLists.addAll(tabDetailPagerBean.getData().getNews());
            //刷新适配器
            Log.e("TAGddd", "加载更多");
            contentLists.addAll(tabDetailInfo.getData());
            adapter.notifyDataSetChanged();
            isLoadMore = false;
        }

    }
    /**
     * XListView下拉刷新时的监听
     */
    @Override
    public void onRefresh() {
        getDataFromNet();//下拉刷新，其实就是重新请求数据
        onLoad();
    }

    private void onLoad() {
        lv_tabdetail_pager.stopRefresh();
        lv_tabdetail_pager.stopLoadMore();
        lv_tabdetail_pager.setRefreshTime("刚刚");
    }

    /**
     * XListView加载更多时的监听
     */
    @Override
    public void onLoadMore() {
        Log.e("TAGaaa", "onLoadMore");
        page+=10;


        getMoreData();
        onLoad();
    }

    private void getMoreData() {
        Log.e("TAgaaa", "getMoreData");
        RequestParams params = new RequestParams(jsonUrl+page);
        Log.e("TAgaaa", jsonUrl + page);
        x.http().get(params, new Callback.CommonCallback<String>() {

            //当联网请求成功的时候回调这个方法
            @Override
            public void onSuccess(String result) {
                Log.e("TAgaaa", result);
                //缓存
                // SpUtils.getInstance(context).save(Url.NET_VIDEO_URL,result);

                //解析数据
                isLoadMore = true;
                processData(result);

            }

            /**
             * 失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAgaaa", "onError"+ex.getMessage());
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


    class TabDetailPagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contentLists.size();
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
            Viewholder holder;
            if (convertView == null) {
                holder = new Viewholder();
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);

            } else {
                holder = (Viewholder) convertView.getTag();
            }


            //根据位置得到数据



            holder.tv_title.setText(contentLists.get(position).getTitle());
            TimeUtils timeUtils = new TimeUtils();

            holder.tv_time.setText(timeUtils.stringForTime((int) contentLists.get(position).getPublishTime()));
            holder.tv_content.setText(contentLists.get(position).getQuantity());
            holder.iv_icon.setBackgroundResource(R.drawable.pic_item_list_default);
            //设置图片
            //先转换成genymotion可以识别的地址
            String imageUrl = contentLists.get(position).getPicUrl();
            x.image().bind(holder.iv_icon, imageUrl, imageOptions);
            //Glide.with(context).load(newsEntity.getListimage()).into(viewHolder.iv_icon);

//            // String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
//
//            if (idArray.contains(newsEntity.getId() + "")) {
//                //设置灰色
//                holder.tv_title.setTextColor(Color.GRAY);
//            } else {
//                holder.tv_title.setTextColor(Color.BLACK);
//            }

            return convertView;
        }

    }


    public static class Viewholder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
    }
}