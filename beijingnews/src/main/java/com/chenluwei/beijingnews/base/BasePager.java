package com.chenluwei.beijingnews.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenluwei.beijingnews.MainActivity;
import com.chenluwei.beijingnews.R;

/**
 * Created by lw on 2016/5/18.
 * 公共类或者说基类
 */
public class BasePager {
    /**
     * 代表各个页面
     */
    public View rootView;
    public Context context;
    public TextView tv_title;
    public ImageButton ib_menu;
    public FrameLayout fl_base_content;


    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    private View initView() {
        View view = View.inflate(context, R.layout.basepager,null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        fl_base_content = (FrameLayout) view. findViewById(R.id.fl_base_content);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //2.左侧菜单收起来
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关-->开，开-->关
                //3.切换到对应的页面，新闻详情，专题详情页面，图组详情页面，互动详情页面
            }
        });
        return view;
    }

    /**
     * 当孩子需要初始化数据的时候，重写该方法，并且在适当的时候调用
     */
    public void initData(){

    }
}
