package com.atguigu.ms.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.atguigu.ms.service.UpdateWidgetService;

/**
 * Created by lw on 2016/4/6.
 * 接收widget工具添加到桌面或从桌面移除等广播的receiver
 */
public class ProcessWidget extends AppWidgetProvider {
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context,UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Intent intent = new Intent(context,UpdateWidgetService.class);
        context.startService(intent);
    }
}
