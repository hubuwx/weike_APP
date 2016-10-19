package com.atguigu.ms.service;

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
import com.atguigu.ms.R;
import com.atguigu.ms.activity.BlackNumActivity;
import com.atguigu.ms.dao.AddressDao;
import com.atguigu.ms.dao.BlackNumDao;
import com.atguigu.ms.util.SpUtils;

import java.lang.reflect.Method;

// 归属地服务
public class AddressService extends Service implements View.OnTouchListener {

    private TelephonyManager mTm;
    private WindowManager mWm;
    private PhoneStateListener listener = new PhoneStateListener() {
        //        * @see TelephonyManager#CALL_STATE_IDLE
//        * @see TelephonyManager#CALL_STATE_RINGING
//        * @see TelephonyManager#CALL_STATE_OFFHOOK
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲或挂断
                    long delayTime = System.currentTimeMillis() - mStartTime;

                    if(delayTime < 3000) {
                    // 是否是黑名单号码
                        boolean isBlackNum = mBlackNumDao.isBlackNum(incomingNumber);
                        
                        // 是否是联系人  
                        boolean isContact = isContact(incomingNumber);
                        
                        // 判断
                        if(!isBlackNum && !isContact) {
                            // 显示通知栏
                            showNotification(incomingNumber);
                        }
                    }

                    removeAddressView();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:// 响铃
                    // 记录电话开始时间
                    mStartTime = System.currentTimeMillis();
                    
                    if (mBlackNumDao.isBlackNum(incomingNumber)) {
                        endCall();
                    } else {
                        addAddressView(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// 通话
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

        // 填写通知栏内容
        Intent intent = new Intent(this, BlackNumActivity.class);

        Log.e("TAG","number:"+incomingNumber);

        intent.putExtra("number", incomingNumber);
        // FLAG_UPDATE_CURRENT 可以发送通知
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("路见不平一声吼!")
                .setContentText("点击添加" + incomingNumber + "为黑名单号码")
                .setContentIntent(pendingIntent)
                .build();

//        notification.flags = Notification.FLAG_NO_CLEAR;// 点击不取消
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后取消

        // 发送通知
        manager.notify(1,notification);

    }

    // 判断是否是联系人号码
    private boolean isContact(String incomingNumber) {
        boolean isContact = false;
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{incomingNumber}, null);
        isContact = cursor.moveToNext();
        cursor.close();
        
        return isContact;
    }

    private long mStartTime;

    // 挂断电话
    private void endCall() {

        try {
//            public static IBinder getService(String name)
//            android.os.ServiceManager
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView mTextView;
    private AddressDao mAddressDao;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private BlackNumDao mBlackNumDao;

    // 添加显示
    private void addAddressView(String incomingNumber) {
        // 添加布局
        mTextView = (TextView) View.inflate(this, R.layout.address_view, null);

        // 监听touch事件
        mTextView.setOnTouchListener(this);


        // 显示来电归属地
        String address = mAddressDao.getAddress(incomingNumber);
        mTextView.setText(address);

        // 回显背景
        int index = SpUtils.getInstance(this).get(SpUtils.STYLE_INDEX, 0);
        int[] icons = {R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_green, R.drawable.call_locate_blue, R.drawable.call_locate_gray};
        mTextView.setBackgroundResource(icons[index]);

        // 回显位置
        int upleft = SpUtils.getInstance(this).get(SpUtils.UPLEFT, -1);
        int uptop = SpUtils.getInstance(this).get(SpUtils.UPTOP, -1);
        if (upleft != -1) {
            // 更新位置
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = upleft;
            params.y = uptop;
        }

        // 添加view到window
        mWm.addView(mTextView, params);

    }

    // 移除显示
    private void removeAddressView() {

        if (mTextView != null) {
            mWm.removeView(mTextView);
            mTextView = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mAddressDao = new AddressDao(this);
        mBlackNumDao = new BlackNumDao(this);

        // 设置window参数
        params.width = WindowManager.LayoutParams.WRAP_CONTENT; // 宽度自适应
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度自适应
        params.format = PixelFormat.TRANSLUCENT;// 设置成透明的
        params.type = WindowManager.LayoutParams.TYPE_PHONE; // 使addressView能移动
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //使addressView不用获得焦点

        mTm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除监听
        mTm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private int mLastX;
    private int mLastY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 最新点的坐标
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:// 按下

                mLastX = eventX;
                mLastY = eventY;
                break;

            case MotionEvent.ACTION_MOVE:// 移动
                // 移动的距离
                int dx = eventX - mLastX;
                int dy = eventY - mLastY;

                params.x += dx;
                params.y += dy;

                mWm.updateViewLayout(mTextView, params);
                //
                mLastX = eventX;
                mLastY = eventY;
                break;
        }

        return true;
    }
}


