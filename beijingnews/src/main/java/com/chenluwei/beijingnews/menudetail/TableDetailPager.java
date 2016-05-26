package com.chenluwei.beijingnews.menudetail;

import android.content.Context;
import android.content.Intent;
import android.drm.ProcessedData;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chenluwei.beijingnews.R;
import com.chenluwei.beijingnews.base.BasePager;
import com.chenluwei.beijingnews.base.MenuDetailBasePager;
import com.chenluwei.beijingnews.domain.NewsCenterBean2;
import com.chenluwei.beijingnews.domain.TabDetailPagerBean;
import com.chenluwei.beijingnews.utils.CacheUtils;
import com.chenluwei.beijingnews.utils.DensityUtil;
import com.chenluwei.beijingnews.utils.Url;
import com.chenluwei.beijingnews.view.HorizontalScrollViewPager;

import com.chenluwei.refreshlistview_lib.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by lw on 2016/5/22.
 * 页签页面
 */
public class TableDetailPager extends MenuDetailBasePager {
    //用自定义的viewPager进行了反拦截，避免了滑轮播图时会把侧滑菜单拉出来的bug
    @ViewInject(R.id.vp_tabdetail_pager)
    private HorizontalScrollViewPager vp_tabdetail_pager;
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;
    @ViewInject(R.id.lv_tabdetail_pager)
    private RefreshListView lv_tabdetail_pager;
    /**
     * 上一次高亮显示的位置
     */
    private int preSelection;
    /**
     * 加载更多的连接
     */
    private String moreUrl;

    private TabDetailPagerAdapter adapter = new TabDetailPagerAdapter();
    private String url;

    private final NewsCenterBean2.NewsCenterData.ChrildrenData childrenData;
    private String TAG = TableDetailPager.class.getSimpleName();
    /**
     * 顶部轮播图数据
     */
    private List<TabDetailPagerBean.DataEntity.TopnewsEntity> topnews;
    /**
     * 新闻列表数据
     */
    private List<TabDetailPagerBean.DataEntity.NewsEntity> newsLists;

    private ImageOptions imageOptions;
    /**
     * 是否已经加载更多
     */
    private boolean isLoadMore;

    public TableDetailPager(Context context,NewsCenterBean2.NewsCenterData.ChrildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(org.xutils.common.util.DensityUtil.dip2px(120), org.xutils.common.util.DensityUtil.dip2px(80))
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
       View view = View.inflate(context, R.layout.tabdetail_pager,null);
        x.view().inject(this,view);

        View topnewsView = View.inflate(context,R.layout.topnews,null);
        x.view().inject(this, topnewsView);
        //添加头
        lv_tabdetail_pager.addTopNewsView(topnewsView);

        //设置刷新的监听(这个接口是自己写的)
        lv_tabdetail_pager.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                getDataFromNet();
            }

            @Override
            public void onLoadMore() {
                if (TextUtils.isEmpty(moreUrl)) {
                    //没有更多
                    Toast.makeText(context, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    lv_tabdetail_pager.onRefreshFinish(false);
                    isLoadMore = false;
                } else {
                    //加载更多
                    getMoreDataFromNet();
                }
            }
        });
        //设置点击item的监听
        lv_tabdetail_pager.setOnItemClickListener(new MyOnItemClickListener());
        //设置轮播图的监听
        return view;
    }
    private static final String  READ_ARRAY_ID = "read_array_id";
    class  MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TabDetailPagerBean.DataEntity.NewsEntity newsEntity = newsLists.get(position - 1);
            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);//3511,222
            //当前这个id没有被保存
            if(!idArray.contains(newsEntity.getId()+"")) {
                //保存
                String values = idArray+newsEntity.getId()+",";
                CacheUtils.putString(context,READ_ARRAY_ID,values);
                //适配器刷新
                adapter.notifyDataSetChanged();
            }

            Intent intent = new Intent(context,NewsDetailActivity.class);
            //要传入的web网页地址
            Log.e("newsEntity.getUrl()", "newsEntity.getUrl()===="+newsEntity.getUrl());
            String replaceString = newsEntity.getUrl().replace("10.0.2.2", "192.168.56.1");
            intent.putExtra("url", replaceString);
            context.startActivity(intent);
        }
    }

    /**
     * 加载更多数据
     */
    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess" + result);
                //CacheUtils.putString(context, url, result);//缓存数据
                isLoadMore = true;
                processData(result);
                lv_tabdetail_pager.onRefreshFinish(true);//更新刷新时间
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError"+ex.getMessage());
                lv_tabdetail_pager.onRefreshFinish(false);//
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled"+cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinished");
            }
        });

    }

    @Override
    public void initData() {
        super.initData();
        //得到第二层json连接
        url = Url.BASE_URL + childrenData.getUrl();
        String saveJson = CacheUtils.getString(context, url);
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
    }


    /**
     * 解析json数据，把数据绑定到ui上
     * @param json
     */
    private void processData(String json) {
        //解析数据
        TabDetailPagerBean tabDetailPagerBean = paseJson(json);
        String more = tabDetailPagerBean.getData().getMore();//加载更多的连接
        if(TextUtils.isEmpty(more)) {
            moreUrl="";
        }else {
            moreUrl = Url.BASE_URL+more;
        }
        
        if(!isLoadMore) {
            //默认
            //设置顶部新闻的数据
            //1.准备数据
            topnews = tabDetailPagerBean.getData().getTopnews();
            //2.设置适配器
            vp_tabdetail_pager.setAdapter(new MyPagerAdapter());
            //添加底部的点
            addPoint();

            //设置轮播图改变的监听
            vp_tabdetail_pager.addOnPageChangeListener(new MyOnPageChangeListener());
            //获取新闻列表数据
            newsLists =tabDetailPagerBean.getData().getNews();
            //设置新闻列表适配器
            lv_tabdetail_pager.setAdapter(adapter);
        }else {
            //加载更多
            newsLists.addAll(tabDetailPagerBean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();
            isLoadMore = false;
        }
        //开始循环切换ViewPager
        if(internalHandler == null) {
            internalHandler = new InternalHandler();
        }
        //因为会调用两次processdata，所以先把之前的移除掉
        internalHandler.removeCallbacksAndMessages(null);
        //重新发任务
        internalHandler.postDelayed(new MyRunnable(), 4000);
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
            int item = (vp_tabdetail_pager.getCurrentItem()+1)%topnews.size();
            vp_tabdetail_pager.setCurrentItem(item);
            //循环发送任务
            internalHandler.postDelayed(new MyRunnable(),4000);
        }
    }


    class TabDetailPagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return newsLists.size();
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
            if(convertView == null) {
                holder = new Viewholder();
                convertView = View.inflate(context,R.layout.item_tabdetail_pager,null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);

            }else {
                holder = (Viewholder) convertView.getTag();
            }


            //根据位置得到数据
            TabDetailPagerBean.DataEntity.NewsEntity newsEntity = newsLists.get(position);
            holder.tv_title.setText(newsEntity.getTitle());
            holder.tv_time.setText(newsEntity.getPubdate());

            holder.iv_icon.setBackgroundResource(R.drawable.pic_item_list_default);
            //设置图片
            //先转换成genymotion可以识别的地址
            String imageUrl = newsEntity.getListimage().replace("10.0.2.2","192.168.56.1");
            x.image().bind(holder.iv_icon,imageUrl,imageOptions);
            //Glide.with(context).load(newsEntity.getListimage()).into(viewHolder.iv_icon);

            String idArray = CacheUtils.getString(context,READ_ARRAY_ID);
            if(idArray.contains(newsEntity.getId()+"")) {
                //设置灰色
                holder.tv_title.setTextColor(Color.GRAY);
            }else {
                holder.tv_title.setTextColor(Color.BLACK);
            }

            return convertView;
        }


    }
    public static class Viewholder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }
    private void addPoint() {
        //先移除之前所有的点
        ll_point_group.removeAllViews();
        //添加顶部新闻红点
        for (int i = 0;i<topnews.size();i++){
            ImageView point = new ImageView(context);
            point.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 5),DensityUtil.dip2px(context,5));
            point.setLayoutParams(params);
            if(i == 0) {
                point.setEnabled(true);//高亮显示
            }else {
                point.setEnabled(false);//
                params.leftMargin = DensityUtil.dip2px(context,8);
            }

            //添加到线性布局里
            ll_point_group.addView(point);
        }
        //设置默认的第一个文本
        tv_title.setText(topnews.get(preSelection).getTitle());
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            tv_title.setText(topnews.get(preSelection).getTitle());
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
            }else if(state == ViewPager.SCROLL_STATE_IDLE && isDraging) {
                //静止，发送消息
                isDraging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }else if(state == ViewPager.SCROLL_STATE_SETTLING && isDraging) {
                isDraging=false;
                //滑动
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(),4000);
            }
        }
    }

    private boolean isDraging = false;


    class MyPagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置默认图片
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            container.addView(imageView);
            //联网请求图片,先得到
            TabDetailPagerBean.DataEntity.TopnewsEntity topnewsEntity = topnews.get(position);
            //转换成genymotion可识别的url
            String topImage_Genymotion = topnewsEntity.getTopimage().replace("10.0.2.2", "192.168.56.1");
            x.image().bind(imageView, topImage_Genymotion);

            //设置触摸事件的监听
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN :
                            //移除消息
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
//                        当前控件(子控件，儿子)收到前驱事件(ACTION_MOVE或者ACTION_MOVE)后，
//                         它的父控件(老爸)突然插手，截断事件的传递，这时，当前控件就会收到ACTION_CANCEL
                        case MotionEvent.ACTION_UP:
                            //重新发消息
                            internalHandler.postDelayed(new MyRunnable(),4000);
                            break;
                    }
                    return true;
                }
            });
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            //super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private TabDetailPagerBean paseJson(String json) {
        return new Gson().fromJson(json,TabDetailPagerBean.class);
    }

    /**
     * 联网请求数据
     */
    private void getDataFromNet(){
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess"+result);
                CacheUtils.putString(context, url, result);//缓存数据
                processData(result);
                lv_tabdetail_pager.onRefreshFinish(true);//更新刷新时间
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError"+ex.getMessage());
                lv_tabdetail_pager.onRefreshFinish(false);//
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onCancelled"+cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinished");
            }
        });
    }
}
