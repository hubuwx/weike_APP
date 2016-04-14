package com.chenluwei.weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenluwei.weike.activity.MainActivity;

/**正文Fragment
 * Created by lw on 2016/4/7.
 */
public class ContentFragment extends BaseFragment {

    //创建特有的view
    @Override
    public View iniView() {
        TextView textView = new TextView(mMainActivity);
        textView.setText("正文Fragment");
        textView.setTextSize(20);

        return textView;
    }
}
