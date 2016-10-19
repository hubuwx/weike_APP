package com.cm.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;


public class SplashActivity extends Activity {

	private RelativeLayout rl_splash_rootview;
	
	/**
	 * 是否进入主页面的标识
	 */
	public static final String IS_ENTER_MAIN = "is_enter_main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		TestUtils test = new TestUtils();
//		int result = test.add(99, 1);
//		System.out.println("result ====================="+result);
		//隐藏标题
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		rl_splash_rootview = (RelativeLayout) findViewById(R.id.rl_splash_rootview);

		// 旋转动画 旋转的度数，旋转的中心点
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);// 设置动画的时长；
		ra.setFillAfter(true);// 设置停留在动画播放完成的状态
		// 拉伸动画
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(500);
		sa.setFillAfter(true);
		// 渐变动画 从透明到完全不透明
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(2000);
		aa.setFillAfter(true);
		

		// 动画集合 -动画同时播放需要,添加动画不分先后顺序
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(ra);
		set.addAnimation(sa);
		
		
		//监听动画播放完成
		set.setAnimationListener(new MyAnimationListener());
		
		
		//播放动画集合
		rl_splash_rootview.startAnimation(set);

	}
	
	class MyAnimationListener implements AnimationListener{

		
		/**
		 * 当动画开始播放的时候执行
		 * @param animation
		 */
		@Override
		public void onAnimationStart(Animation animation) {
			
		}

		/**
		 * 当动画播放结束的时候执行
		 * @param animation
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			/**
			 * 判断是否已经进入过主页面，如果没有进入过，就进入引导页面，否则就直跳转到主页面；
			 *  下次进来直接进入主页面
			 */
			boolean isEnterMain = CacheUtils.getBoolean(SplashActivity.this, IS_ENTER_MAIN, false);
			if(isEnterMain){
				//曾经已经进入过主页面--直接进入主页面
				startActivity(new Intent(SplashActivity.this,LoginActivity.class));
				//把当前页面（欢迎页面）关闭
				finish();
			}else{
				//进入引导页面
				Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
				startActivity(intent);
				
				//把当前页面（欢迎页面）关闭
				finish();
			}
			
//			Toast.makeText(getApplicationContext(), "动画播放完成，要进入引导页面了", 1).show();
			
		}
		/**
		 * 当动画播重复播放的时候执行
		 * @param animation
		 */
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
	}

}
