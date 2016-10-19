package com.atguigu.ms.service;

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
import android.widget.RemoteViews;

import com.atguigu.ms.R;
import com.atguigu.ms.receiver.ProcessWidget;
import com.atguigu.ms.util.MsUtils;

public class UpdateWidgetService extends Service {
    private static final int WHAT_UPDATA = 1;
    private AppWidgetManager manager;
    private ComponentName mProvider;
    private RemoteViews mRemoteView;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case WHAT_UPDATA :
                    //更新
                    updateWidget();
                    //再次发送消息
                    handler.sendEmptyMessageDelayed(WHAT_UPDATA,3000);
                    break;
            }
        }
    };
    private ScreenReceiver mScreenReceiver;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建用于更新widget的管理器
        manager = AppWidgetManager.getInstance(this);
        mProvider = new ComponentName(this,ProcessWidget.class);


        mRemoteView = new RemoteViews(getPackageName(), R.layout.process_widget);

        Intent intent = new Intent(this,UpdateWidgetService.class);
        intent.putExtra("action", "clear");
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateWidget();
        handler.sendEmptyMessageDelayed(WHAT_UPDATA, 3000);

        //注册屏幕锁屏开屏的广播接收器
        mScreenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, filter);

        mRemoteView.setOnClickPendingIntent(R.id.btn_widget_clear, pendingIntent);
        //获取小部件信息
    }

    private void updateWidget() {

        int processCount = MsUtils.getAllProcessCount(this);
        long memory = MsUtils.getAvailMem(this);
        mRemoteView.setTextViewText(R.id.tv_widget_process_count,"当前进程数："+processCount);
        mRemoteView.setTextViewText(R.id.tv_widget_process_memory,"可用内存："+MsUtils.formatSize(this,memory));
        manager.updateAppWidget(mProvider, mRemoteView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("action");
        //判断是第一次创建服务发送的还是点击按钮发送的
        if("clear".equals(action)) {
            //清理进程
            MsUtils.clearAllProcess(this);
            //刷新界面
            updateWidget();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)) {
                handler.sendEmptyMessage(WHAT_UPDATA);
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)) {
                handler.removeCallbacksAndMessages(null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(mScreenReceiver);
    }
}
