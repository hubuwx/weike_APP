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
	 *����ļ��
	 */
	private float leftMax;
	/**
	 * ���Ҫ�ƶ��ľ���
	 */
	private int leftMarg;
	/**
	 * ͼƬ����
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
	 * ��ʼ�����ݺͰ�����
	 */
	private void initData() {
		// TODO Auto-generated method stub
		//��ʼ������
		int ids[] = {R.drawable.guide_11,R.drawable.guide_2,R.drawable.guide_3};
		imageViews = new ArrayList<ImageView>();
		for(int i=0;i<ids.length;i++){
			//ʵ����ͼƬ
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(ids[i]);
			
			//���ͼƬ
			imageViews.add(imageView);
			
			//����ָʾ��
			View point=new View(this);
			point.setBackgroundResource(R.drawable.point_normal);
			//��ӵ�˭�Ĳ�����ȥ���͵���˭�İ�
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(20, 20);
			//���õ�ļ�࣬���ε��������߼��
			if(i!=0){
				params.leftMargin=10;
			}
			
			point.setLayoutParams(params);
			//���ñ���-Ĭ�ϻ�ɫ
			//�����ɫ��ͺ�ɫ��
			ll_point_group.addView(point);
			
		}
		//����������
		viewpager.setAdapter(new MyPagerAdapter());
		
		//�о��룬��������˲��пؼ��Ĵ�С
		//onMeasure-->onLayout-->onDraw();
        red_point.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			

			@Override
			public void onGlobalLayout() {
				
				//ȡ������onLayout����
				red_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				leftMax = ll_point_group.getChildAt(1).getLeft()-ll_point_group.getChildAt(0).getLeft();
				
				//��������� = ��1���������ߵľ���-��0���������ߵľ���
				
			}
		});
        
        //����ҳ��ĸı�
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
        	/**
			 * ��ĳ��ҳ�汻ѡ�е�ʱ��ص�
			 * position����ѡ�е�ҳ����±�
			 */
			@Override
			public void onPageSelected(int position) {
				// ������������ҳ���ʱ����ʾ��ť������ҳ������
				if(position ==imageViews.size()-1){
					btn_start_main.setVisibility(View.VISIBLE);
				}else{
					btn_start_main.setVisibility(View.GONE);
				}
				
			}
			
			/**
			 * position����ǰ����ҳ����±�
			 * positionOffset����Ļ���ƶ��İٷֱ�
			 * positionOffsetPixels������Ļ���ƶ��˶�������
			 */
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				
                // Ҫ�ƶ��ľ��� = ����ļ��*��Ļ�ƶ��İٷֱȣ�����ƶ��İٷֱȣ�				
			    leftMarg = (int) (leftMax *(positionOffset+position));
				System.out.println("���Ҫ�ƶ��ľ���=="+leftMarg+", positionOffset=="+positionOffset+",  positionOffsetPixels=="+positionOffsetPixels);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) red_point.getLayoutParams();
				params.leftMargin = leftMarg;
				red_point.setLayoutParams(params);
			}
			
			/**
			 * ��ViewPager״̬�����仯��ʱ��ص�
			 */
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        //���õ���¼�
		btn_start_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������ҳ��
				startActivity(new Intent(GuideActivity.this,LoginActivity.class));
				//��ʶ�Ѿ�����������ҳ�棬�´ξͲ��ý�������ҳ��
				CacheUtils.putBoolean(GuideActivity.this, SplashActivity.IS_ENTER_MAIN, true);
				//�رյ�ǰҳ��
				finish();
				
			}
		});
	}
	class MyPagerAdapter extends PagerAdapter{
         //��������Ŀ
		public int getCount(){
			return imageViews.size();
		}
		/**
		 * �����൱��getView();
		 * ʵ����ÿ��ҳ��
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = imageViews.get(position);
			container.addView(imageView);//��ӵ�������ViewPager
			return imageView;
		}
		/**
		 * �Ƚ�view�ǲ���instantiateItem�������صĶ���
		 * view ��ǰҳ��
		 * object��instantiateItem�������صĶ���
		 */
		public boolean isViewFromObject(View view, Object object){
			return view ==object;
		}
		/**
		 * ����ĳ��ҳ��
		 */
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
		}
		
	}
	/**
	 * ��ʼ��View
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
