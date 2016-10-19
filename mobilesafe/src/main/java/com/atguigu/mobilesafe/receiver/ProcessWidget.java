package com.atguigu.mobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.atguigu.mobilesafe.service.WidgetService;

/**
 * Created by lenovo on 2016/3/23.
 */
public class ProcessWidget extends AppWidgetProvider {

    //长按产生widget
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //启动服务
        context.startService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //停止服务
        context.stopService(new Intent(context, WidgetService.class));
    }
}
