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
	 * �Ƿ������ҳ��ı�ʶ
	 */
	public static final String IS_ENTER_MAIN = "is_enter_main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		TestUtils test = new TestUtils();
//		int result = test.add(99, 1);
//		System.out.println("result ====================="+result);
		//���ر���
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		rl_splash_rootview = (RelativeLayout) findViewById(R.id.rl_splash_rootview);

		// ��ת���� ��ת�Ķ�������ת�����ĵ�
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);// ���ö�����ʱ����
		ra.setFillAfter(true);// ����ͣ���ڶ���������ɵ�״̬
		// ���춯��
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(500);
		sa.setFillAfter(true);
		// ���䶯�� ��͸������ȫ��͸��
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(2000);
		aa.setFillAfter(true);
		

		// �������� -����ͬʱ������Ҫ,��Ӷ��������Ⱥ�˳��
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(ra);
		set.addAnimation(sa);
		
		
		//���������������
		set.setAnimationListener(new MyAnimationListener());
		
		
		//���Ŷ�������
		rl_splash_rootview.startAnimation(set);

	}
	
	class MyAnimationListener implements AnimationListener{

		
		/**
		 * ��������ʼ���ŵ�ʱ��ִ��
		 * @param animation
		 */
		@Override
		public void onAnimationStart(Animation animation) {
			
		}

		/**
		 * ���������Ž�����ʱ��ִ��
		 * @param animation
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			/**
			 * �ж��Ƿ��Ѿ��������ҳ�棬���û�н�������ͽ�������ҳ�棬�����ֱ��ת����ҳ�棻
			 *  �´ν���ֱ�ӽ�����ҳ��
			 */
			boolean isEnterMain = CacheUtils.getBoolean(SplashActivity.this, IS_ENTER_MAIN, false);
			if(isEnterMain){
				//�����Ѿ��������ҳ��--ֱ�ӽ�����ҳ��
				startActivity(new Intent(SplashActivity.this,LoginActivity.class));
				//�ѵ�ǰҳ�棨��ӭҳ�棩�ر�
				finish();
			}else{
				//��������ҳ��
				Intent intent = new Intent(SplashActivity.this,GuideActivity.class);
				startActivity(intent);
				
				//�ѵ�ǰҳ�棨��ӭҳ�棩�ر�
				finish();
			}
			
//			Toast.makeText(getApplicationContext(), "����������ɣ�Ҫ��������ҳ����", 1).show();
			
		}
		/**
		 * ���������ظ����ŵ�ʱ��ִ��
		 * @param animation
		 */
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
	}

}
