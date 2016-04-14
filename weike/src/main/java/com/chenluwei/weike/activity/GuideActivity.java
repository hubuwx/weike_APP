package com.chenluwei.weike.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.chenluwei.weike.R;
import com.chenluwei.weike.util.SpUtils;

import java.util.ArrayList;

public class GuideActivity extends Activity {
    private  ViewPager viewpager;
    private Button bt_start_main;
    private LinearLayout ll_point_group;
    private ArrayList<ImageView> mImageViews;
    private MyPagerAdapter adapter;
    private View skip_point;
    private float mLeftMax;
    private float mLeftMarg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //获取控件对象
        viewpager = (ViewPager)findViewById(R.id.viewpager);
        bt_start_main = (Button)findViewById(R.id.bt_start_main);
        ll_point_group = (LinearLayout)findViewById(R.id.ll_point_group);
        skip_point = findViewById(R.id.skip_point);
        skip_point.setBackgroundResource(R.drawable.skip_point);
        //初始化数据

        //设置数据
        initData();
    }

    //初始化数据和绑定数据
    private void initData() {
        //初始化数据
        int ids[] = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        mImageViews = new ArrayList<ImageView>();
        for (int i = 0;i<ids.length;i++){
            //实例化图片
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(ids[i]);

            //添加图片
            mImageViews.add(imageView);

            //创建指示点
            View point = new View(this);
            //设置背景：默认灰色
            point.setBackgroundResource(R.drawable.mormal_point);
            //添加到哪个布局去，就导入谁的包
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20,20);
            //调整间距(注意第1个点不用调整)
            if(i != 0) {
                params.leftMargin = 20;
            }
            point.setLayoutParams(params);
            //定义灰色点和红色点
            ll_point_group.addView(point);
        }
        //设置数据
        adapter = new MyPagerAdapter();
        viewpager.setAdapter(adapter);

        //有距离，必须测量了才有控件的大小
        //onMeasure-->onLayout-->onDraw

        skip_point.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //取消监听onLayout方法
                skip_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mLeftMax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();

                //两点间距离计算 = 第2个点的距离父视图左边的距离 - 第1个点距离父视图左边的距离
            }
        });

        //监听页面的改变
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //position:当前的页面下标
            //positionOffset：屏幕上移动的百分比
            //positionOffsetPixels:在屏幕上移动了多少像素

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //要移动的距离 = 两点的间距*屏幕移动的百分比
                mLeftMarg = mLeftMax * (positionOffset + position);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) skip_point.getLayoutParams();
                params.leftMargin = (int) mLeftMarg;
                skip_point.setLayoutParams(params);
            }

            //当某个页面被选中时回调
            //position：被选中的页面的下表
            @Override
            public void onPageSelected(int position) {
                if (position == mImageViews.size() - 1) {
                    bt_start_main.setVisibility(View.VISIBLE);
                } else {
                    bt_start_main.setVisibility(View.GONE);
                }
            }

            //当viewPager状态发生变化时回调
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    //点击按钮进入主界面
    public void toMain(View v) {
          //进入主页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
          //sp存储引导标示(为了使创建快捷方式和进入引导界面只执行一次)
        SpUtils.getInstance(this).save(SpUtils.ENTERMAIN,true);
          //关闭当前页面
            finish();
    }
    class MyPagerAdapter extends PagerAdapter{

        //返回总条目
        @Override
        public int getCount() {
            return mImageViews.size();
        }

        //比较view是不是instantiateItem方法返回的对象
        //view 当前页面
        //object instantiateItem方法返回的对象
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //销毁某个页面
        //注意container参数不要选成View类的
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View)object);
        }


        //功能相当于getView()
        //实例化每个界面
        //注意container参数不要选成View类的
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            container.addView(imageView);//添加到容器中

            return imageView;
        }
    }
}
