package com.cm.activity;




import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;





public class GuideActivity extends Activity {
	private ViewPager viewpager;
	private Button btn_start_main;
	private LinearLayout ll_point_group;
	private View red_point;
	/**
	 *两点的间距
	 */
	private float leftMax;
	/**
	 * 红点要移动的距离
	 */
	private int leftMarg;
	/**
	 * 图片集合
	 */
	private ArrayList<ImageView> imageViews;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}
	/**
	 * 初始化数据和绑定数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		//初始化数据
		int ids[] = {R.drawable.guide_11,R.drawable.guide_2,R.drawable.guide_3};
		imageViews = new ArrayList<ImageView>();
		for(int i=0;i<ids.length;i++){
			//实例化图片
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(ids[i]);
			
			//添加图片
			imageViews.add(imageView);
			
			//创建指示点
			View point=new View(this);
			point.setBackgroundResource(R.drawable.point_normal);
			//添加到谁的布局里去，就导入谁的包
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(20, 20);
			//设置点的间距，屏蔽第零个点左边间距
			if(i!=0){
				params.leftMargin=10;
			}
			
			point.setLayoutParams(params);
			//设置背景-默认灰色
			//定义灰色点和红色点
			ll_point_group.addView(point);
			
		}
		//设置适配器
		viewpager.setAdapter(new MyPagerAdapter());
		
		//有距离，必须测量了才有控件的大小
		//onMeasure-->onLayout-->onDraw();
        red_point.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			

			@Override
			public void onGlobalLayout() {
				
				//取消监听onLayout方法
				red_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				leftMax = ll_point_group.getChildAt(1).getLeft()-ll_point_group.getChildAt(0).getLeft();
				
				//两点间距计算 = 第1个点距离左边的距离-第0个点距离左边的距离
				
			}
		});
        
        //监听页面的改变
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
        	/**
			 * 当某个页面被选中的时候回调
			 * position：被选中的页面的下标
			 */
			@Override
			public void onPageSelected(int position) {
				// 当滑动第三个页面的时候显示按钮，其他页面隐藏
				if(position ==imageViews.size()-1){
					btn_start_main.setVisibility(View.VISIBLE);
				}else{
					btn_start_main.setVisibility(View.GONE);
				}
				
			}
			
			/**
			 * position：当前画的页面的下标
			 * positionOffset：屏幕上移动的百分比
			 * positionOffsetPixels：在屏幕上移动了多少像素
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				
                // 要移动的距离 = 两点的间距*屏幕移动的百分比（间距移动的百分比）				
			    leftMarg = (int) (leftMax *(positionOffset+position));
				System.out.println("红点要移动的距离=="+leftMarg+", positionOffset=="+positionOffset+",  positionOffsetPixels=="+positionOffsetPixels);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) red_point.getLayoutParams();
				params.leftMargin = leftMarg;
				red_point.setLayoutParams(params);
			}
			
			/**
			 * 当ViewPager状态发生变化的时候回调
			 */
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        //设置点击事件
		btn_start_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入主页面
				startActivity(new Intent(GuideActivity.this,LoginActivity.class));
				//标识已经进入了引导页面，下次就不用进入引导页面
				CacheUtils.putBoolean(GuideActivity.this, SplashActivity.IS_ENTER_MAIN, true);
				//关闭当前页面
				finish();
				
			}
		});
	}
	class MyPagerAdapter extends PagerAdapter{
         //返回总条目
		public int getCount(){
			return imageViews.size();
		}
		/**
		 * 功能相当于getView();
		 * 实例化每个页面
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = imageViews.get(position);
			container.addView(imageView);//添加到容器中ViewPager
			return imageView;
		}
		/**
		 * 比较view是不是instantiateItem方法返回的对象
		 * view 当前页面
		 * object：instantiateItem方法返回的对象
		 */
		public boolean isViewFromObject(View view, Object object){
			return view ==object;
		}
		/**
		 * 销毁某个页面
		 */
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
		}
		
	}
	/**
	 * 初始化View
	 */
	private void initView() {
		setContentView(R.layout.activity_guide);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		btn_start_main = (Button) findViewById(R.id.btn_start_main);
		ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
		red_point = findViewById(R.id.red_point);
		red_point.setBackgroundResource(R.drawable.point_red);
	}

}
