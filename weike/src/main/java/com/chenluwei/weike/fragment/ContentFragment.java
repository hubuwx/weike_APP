package com.chenluwei.weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenluwei.weike.activity.MainActivity;
import com.chenluwei.weike.base.BasePager;

import java.util.List;

/**正文Fragment
 * Created by lw on 2016/4/7.
 */
public class ContentFragment extends BaseFragment {
    private List<BasePager> basePagers;
    private int position;
    public ContentFragment(List<BasePager> basePagers,int position) {
        this.basePagers = basePagers;
        this.position = position;
    }

    //创建特有的view
    @Override
    public View iniView() {
        BasePager basePager = getBasePager();
        if(basePager.rootView!=null) {
            return basePager.rootView;
        }
        return null;
    }

    /**
     *
     得到相应pager的对象，并数据初始化
     */

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        //注意要判断该页面是否被已经初始化过
        if(basePager != null && !basePager.isInitData) {
            //数据初始化
            basePager.initData();
            basePager.isInitData = true;
        }

        return basePager;
    }

}
