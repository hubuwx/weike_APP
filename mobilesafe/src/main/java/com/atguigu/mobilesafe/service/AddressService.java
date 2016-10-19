package com.atguigu.mobilesafe.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.atguigu.mobilesafe.R;
import com.atguigu.mobilesafe.activity.BlackNumActivity;
import com.atguigu.mobilesafe.dao.AddressDao;
import com.atguigu.mobilesafe.dao.BlackNumDao;
import com.atguigu.mobilesafe.util.SpUtils;

import java.lang.reflect.Method;

public class AddressService extends Service {

    private AddressDao addressDao;
    private TelephonyManager tm;
    private WindowManager wm;
    private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    long delayTime = System.currentTimeMillis() - startTime1;
                    if(delayTime < 3000) {
                        // 是否是联系人
                        boolean isContact = isContact(incomingNumber);

                        // 是否是黑名单号码
                        boolean isBlack = blackNumDao.isBlackNum(incomingNumber);

                        if(!isContact && !isBlack) {
                            // 骚扰电话
                            showNotification(incomingNumber);
                        }
                    }
                    removeAddressView();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    startTime1 = System.currentTimeMillis();

                    if (blackNumDao.isBlackNum(incomingNumber)) {
                        // 挂断电话
                        endCall();
                    }else {
                        addAddressView(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    removeAddressView();
                    break;
            }
        }
    };

    // 显示通知栏
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(String incomingNumber) {
        // 获取通知的管理者对象
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建通知
        Intent intent = new Intent(this, BlackNumActivity.class);
        intent.putExtra("number",incomingNumber);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,0);
        Notification notification = new Notification.Builder(this)
                                    .setContentTitle("来电话了")
                                    .setSmallIcon(R.mipmap.logo)
                                    .setContentText("添加"+incomingNumber+"为黑名单号码")
                                    .setContentIntent(pendingIntent)
                                    .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;// 通知点击后自动取消
        // 发送通知
        manager.notify(1,notification);

    }

    // 判断是否是联系人号码
    private boolean isContact(String incomingNumber) {
        boolean isContact = false;

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{incomingNumber}, null);
        isContact = cursor.moveToNext();

        return isContact;
    }

    private long startTime1;

    private void endCall() {
        try {
            Log.e("TAG", "挂断电话");
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);

            ITelephony telephony = ITelephony.Stub.asInterface(iBinder);
            telephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int lastX;
    private int lastY;
    private WindowManager.LayoutParams params;
    private TextView addressView;
    private BlackNumDao blackNumDao;

    private void addAddressView(String incomingNumber) {
        // 获取布局文件对象
        addressView = (TextView) View.inflate(this, R.layout.address_display, null);


        // 触摸事件
        addressView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventX = (int) event.getRawX();
                int eventY = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = eventX;
                        lastY = eventY;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dx = eventX - lastX;
                        int dy = eventY - lastY;

                        // 更新坐标
                        params.x += dx;
                        params.y += dy;
                        wm.updateViewLayout(addressView, params);

                        lastX = eventX;
                        lastY = eventY;
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

        // 获取背景图片
        int index = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX, 0);
        int[] icons = {R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_green,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray};
        addressView.setBackgroundResource(icons[index]);

        // 获取号码归属地
        String location = addressDao.getLocation(incomingNumber);
        addressView.setText(location);

        // 获取设置显示的位置
        int upleft = SpUtils.getInstance(this).get(SpUtils.UP_LEFT, -1);
        int uptop = SpUtils.getInstance(this).get(SpUtils.UP_TOP, -1);
        if (upleft != -1) {
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = upleft;
            params.y = uptop;
        }

        wm.addView(addressView, params);
    }

    private void removeAddressView() {
        if (addressView != null) {
            wm.removeView(addressView);
            addressView = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化变量
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        addressDao = new AddressDao(this);
        blackNumDao = new BlackNumDao(this);

        // 初始化wm布局参数
        params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; // 宽度自适应
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
        params.format = PixelFormat.TRANSLUCENT;// 设置成透明的
        params.type = WindowManager.LayoutParams.TYPE_PHONE; // 使addressView能移动
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //使addressView不用获得焦点

        //监听电话
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
}
