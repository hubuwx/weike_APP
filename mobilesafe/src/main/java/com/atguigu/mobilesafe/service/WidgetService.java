package com.atguigu.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.receiver.ProcessWidget;
import com.atguigu.mobilesafe.util.MsUtils;

/**
 * 用来更新桌面应用小工具的Service
 */
public class WidgetService extends Service {

    private AppWidgetManager manager;
    private RemoteViews remoteViews;
    private ComponentName provider;
    private ScreeenReceiver receiver;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Log.i("TAG", "WidgetService 更新widget");
            if (msg.what == 2) {
                updateWidget();
                //发送延迟消息更新widget
                handler.sendEmptyMessageDelayed(2, 3000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "onCreate()");

        manager = AppWidgetManager.getInstance(this);

        provider = new ComponentName(this, ProcessWidget.class);
        remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget_view);
        //设置点击监听
        Intent intent = new Intent(this, WidgetService.class);
        intent.putExtra("action", "clear");
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_widget_clear, pendingIntent);

        updateWidget();

        //发送延迟消息更新widget
        handler.sendEmptyMessageDelayed(2, 3000);

        //注册receiver
        receiver = new ScreeenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);  //开屏
        filter.addAction(Intent.ACTION_SCREEN_OFF); //锁屏
        registerReceiver(receiver, filter);

    }

    /**
     * 更新widget
     */
    private void updateWidget() {
        //设置文本
        int processCount = MsUtils.getAllProcessCount(this);
        remoteViews.setTextViewText(R.id.tv_widget_process_count, "当前进程数: "+processCount);
        long availMem = MsUtils.getAvailMem(this);
        remoteViews.setTextViewText(R.id.tv_widget_process_memory, "可用内存: "+MsUtils.formatSize(this, availMem));
        //更新widget
        manager.updateAppWidget(provider,remoteViews);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        if ("clear".equals(action)) {
            //清理进程
            MsUtils.clearAllProcesses(this);
            //更新widget
            updateWidget();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy()");
        handler.removeCallbacksAndMessages(null);//移除所有未处理的消息
        //解注册receiver
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    接收开屏和锁屏广播的receiver
     */
    class ScreeenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {//开
                //启动更新
                handler.sendEmptyMessage(2);
            } else {//关
                //停止更新
                handler.removeCallbacksAndMessages(null);
            }
        }
    }
}
