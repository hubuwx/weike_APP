package com.chenluwei.beijingnews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenluwei.beijingnews.MainActivity;

/**
 * Created by lw on 2016/5/18.
 * Fragment的公共类：LeftMenuFragment和ContentFragment继承该类
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 上下文
     */
    public Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context =  getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 由孩子强制实现，实现特定的效果
     * @return
     */
    public abstract  View initView() ;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 当孩子需要初始化数据的时候，重新该方法
     */
    public void initData() {

    }
}
