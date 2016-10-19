package com.chenluwei.refreshlistview_lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lw on 2016/5/24.
 */
public class RefreshListView extends ListView{

    /**
     * 整个头部，包括下拉刷新部分和顶部轮播图
     */
    private LinearLayout headerView;

    private ImageView iv_header_arrow;

    private ProgressBar pb_header_status;

    private TextView tv_header_satsus;
    private TextView tv_header_time;



    /**
     * 下拉刷新控件
     */
    private View ll_pull_down_refresh;
    /**
     * 下拉刷新控件的高
     */
    private int refreshHeight;

    /**
     * 顶部轮播图
     */
    private View topnewsView;


    /**
     下拉刷新状态
     */
    public static final int PULL_DONW_REFRESH = 0;

    /**
     手势刷新状态
     */
    public static final int RELEASE_REFRESH = 1;


    /**
     正在刷新状态
     */
    public static final int REFRESHING = 2;


    private int currentState = PULL_DONW_REFRESH;

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView(context);
        initFootView(context);
        initAnimation();
    }

    private View footerView;

    private int footerViewHeight;

    /**
     * 初始化footView
     * @param context
     */
    private void initFootView(Context context) {
        footerView = View.inflate(context,R.layout.refresh_footer,null);
        footerView.measure(0,0);
        footerViewHeight = footerView.getMeasuredHeight();
        //footerView.setPadding(0,0,0,0);
        footerView.setPadding(0,-footerViewHeight,0,0);
        addFooterView(footerView);

        //设置滚动监听
        setOnScrollListener(new MyOnScrollListener());
    }

    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;
    class MyOnScrollListener implements OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //当静止或者惯性滚动
            if(scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                //并且是最后一个的时候
                if(getLastVisiblePosition() == getCount()-1) {
                    //加载更多
                    isLoadMore = true;
                    //显示加载更多的布局
                    footerView.setPadding(8,8,8,8);
                    //调用接口
                    if(mOnRefreshListener != null) {
                        mOnRefreshListener.onLoadMore();
                    }

                }
                
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    private Animation upAnimation;
    private Animation downAnimation;

    /**
     * 初始化动画
     */
    private void initAnimation() {
        upAnimation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180,-360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }

    /**
     * 初始化下拉刷新的头部视图
     * @param context
     */
    private void initHeaderView(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header,null);
        ll_pull_down_refresh = headerView.findViewById(R.id.ll_pull_down_refresh);
        iv_header_arrow = (ImageView)headerView.findViewById(R.id.iv_header_arrow);
        pb_header_status = (ProgressBar)headerView.findViewById(R.id.pb_header_status);
        tv_header_time = (TextView)headerView.findViewById(R.id.tv_header_time);
        tv_header_satsus = (TextView)headerView.findViewById(R.id.tv_header_satsus);



//        View.setPading(0,-控件的高，0,0);//完全隐藏
//        View.setPading(0,0，0,0);//完全显示
//        View.setPading(0,控件的高，0,0);//2倍高完全显示
        ll_pull_down_refresh.measure(0,0);//调用这个方法了才会回调onMeasure方法，从而使下面这句得到值
        refreshHeight = ll_pull_down_refresh.getMeasuredHeight();//也没有值，要先测量
        ll_pull_down_refresh.setPadding(0, -refreshHeight, 0, 0);
        //以头的方式加载进入此listView
        addHeaderView(headerView);
    }

    /**
     * 记录启动坐标
     */
    private float startY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //1.记录起始坐标
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.记录结束值
                float endY = ev.getY();
                //3.计算偏移量
                float distanceY = endY - startY;
                //判断顶部轮播图是否完全显示
                boolean isDisplayTopNews = isDisplayTopNews();
                //如果没有完全显示，则跳出，不会下拉刷新
                if(!isDisplayTopNews){
                    break;
                }


                if(distanceY > 0){
                    int padingTop = (int) (-refreshHeight + distanceY);
                    Log.e("padingTop", "padingTop==" + padingTop);
                    if(padingTop > 0 && currentState != RELEASE_REFRESH) {
                        //显示手松刷新
                        currentState = RELEASE_REFRESH;

                        //更新状态
                        refreshHeaderStatus();

                    }else if(padingTop <0 && currentState != PULL_DONW_REFRESH) {
                        //显示下拉刷新
                        currentState = PULL_DONW_REFRESH;

                        //更新状态
                        refreshHeaderStatus();



                    }


                    //动态设置隐藏和显示
                    ll_pull_down_refresh.setPadding(0, padingTop, 0, 0);//动态显示

                }
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                if(currentState == PULL_DONW_REFRESH) {
                    //隐藏
                    ll_pull_down_refresh.setPadding(0,-refreshHeight,0,0);
                    
                }else if(currentState == RELEASE_REFRESH) {
                    //状态改为正在刷新
                    currentState = REFRESHING;
                    ll_pull_down_refresh.setPadding(10,10,10,10);//正常一倍显示(有10px边距)
                    refreshHeaderStatus();
                    //回调接口
                    if(mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshHeaderStatus() {
        switch (currentState){
            case PULL_DONW_REFRESH://下拉刷新
                pb_header_status.setVisibility(View.GONE);
                iv_header_arrow.setVisibility(View.VISIBLE);
                iv_header_arrow.startAnimation(downAnimation);
                tv_header_satsus.setText("下拉刷新...");
                break;
            case RELEASE_REFRESH://手松刷新
                pb_header_status.setVisibility(View.GONE);
                iv_header_arrow.setVisibility(View.VISIBLE);
                iv_header_arrow.startAnimation(upAnimation);
                tv_header_satsus.setText("手松刷新...");
                break;
            case REFRESHING://正在刷新
                //动画停止掉
                iv_header_arrow.clearAnimation();
                pb_header_status.setVisibility(View.VISIBLE);
                iv_header_arrow.setVisibility(View.GONE);
                tv_header_satsus.setText("正在刷新");

                break;
        }
    }


    private float mListViewOnScreen = -1;
    /**
     * 判断顶部轮播图是否完全显示
     * 当ListView在屏幕上的Y轴坐标小于或者等于顶部轮播图在Y 轴的坐标的时候，就是完全显示
     * @return
     */
    private boolean isDisplayTopNews() {

        if(topnewsView != null) {
            int[] location = new int[2];
            //得到listView在屏幕上的y轴坐标
            if(mListViewOnScreen == -1) {
                this.getLocationOnScreen(location);//得到listview在屏幕上的坐标，
                mListViewOnScreen = location[1];//得到y轴上的坐标
            }
            //得到顶部轮播图在屏幕上的坐标
            topnewsView.getLocationOnScreen(location);
            float mtopnewsViewOnScreenY = location[1];

            return mListViewOnScreen <= mtopnewsViewOnScreenY;
        }
        return true;
    }

    /**
     * 添加顶部轮播图部分
     * @param topnewsView
     */
    public void addTopNewsView(View topnewsView) {
        this.topnewsView = topnewsView;
        if(topnewsView != null) {
            headerView.addView(topnewsView);
        }
    }

    /**
     * 下拉刷新完成，把状态设置为默认
     * @param isSuccess
     */
    public void onRefreshFinish(boolean isSuccess) {

        if(isLoadMore) {
            //加载更多
            isLoadMore = false;
            footerView.setPadding(0,-footerViewHeight,0,0);

        }else {
            //下拉刷新
            currentState = PULL_DONW_REFRESH;
            pb_header_status.setVisibility(View.GONE);
            iv_header_arrow.setVisibility(View.VISIBLE);
            ll_pull_down_refresh.setPadding(0,-refreshHeight,0,0);

            if(isSuccess) {
                //更新刷新时间
                tv_header_time.setText("刷新时间："+getSystemTime());
            }else {
                tv_header_time.setText("上次更新时间："+getSystemTime());
            }
        }



    }

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyy-mm-dd HH:mm:ss");
        return format.format(new Date());


    }

    /**
     * 下拉刷新的接口
     */
    public interface OnRefreshListener{

        /**
         当下拉刷新的时候回调这个方法

         */
        public void onPullDownRefresh();

        /**
         * 当加载更多的时候回调这个方法
         */
        public void onLoadMore();

    }

    private OnRefreshListener mOnRefreshListener;

    /***
     设置刷新的监听：下拉刷新和加载更多
     */
    public void setOnRefreshListener(OnRefreshListener l){
        mOnRefreshListener = l;
    }



}
