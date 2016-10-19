package com.chenluwei.weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenluwei.weike.activity.MainActivity;
import com.mxn.soul.flowingdrawer_core.MenuFragment;

/**
 * Created by lw on 2016/4/7.
 * 作为抽象基类，LeftMenuFragment和BaseFragment都要继承于它
 *
 */
public abstract class BaseFragment extends MenuFragment {
    public MainActivity mMainActivity;

    //当这个Fragment被创建的时候执行
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }


    //当Fragment特有的View被创建的时候执行
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = iniView();
        return view;
    }

    //让实现它的孩子，一定要有自己独有的视图
    public abstract View iniView();


    //当Activity被创建的时候执行
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //不是抽象方法，也就是说不强制孩子实现
    //当孩子需要实例化数据的时候，再重写这个方法
    private void initData() {

    }
}
