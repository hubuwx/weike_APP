package com.atguigu.ms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.atguigu.ms.R;
import com.atguigu.ms.dao.BlackNumDao;
import com.atguigu.ms.util.GpsUtils;
import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取短信内容
        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        byte[] pdu= (byte[]) pdus[0];
        SmsMessage message = SmsMessage.createFromPdu(pdu);

        String phoneNum = message.getDisplayOriginatingAddress();
        String msg = message.getMessageBody();

        //
        BlackNumDao blackNumDao = new BlackNumDao(context);
        boolean isBlackNum = blackNumDao.isBlackNum(phoneNum);
        if(isBlackNum) {
            abortBroadcast();
            Log.e("Tag","拦截朝鲜核武器成功");

            return;
        }

        // 开启保护
        // 获取到安全号码
        boolean isProtect = SpUtils.getInstance(context).get(SpUtils.PROTECT, false);
        String safeNum = SpUtils.getInstance(context).get(SpUtils.SAFE_NUM, null);

        if(!isProtect || safeNum == null) {
            return;
        }

        if("#alarm#".equals(msg)) {// 报警音乐
            abortBroadcast();
            MsUtils.playAlarm(context, R.raw.alarm);

        }else if("#wipedata#".equals(msg)) {// 销毁数据
            abortBroadcast();
            MsUtils.resetPhone(context);

        }else if("#lockscreen#".equals(msg)) {//锁屏
            abortBroadcast();
            MsUtils.lockScreen(context);

        }else if("#location#".equals(msg)) {// 位置
            abortBroadcast();
            GpsUtils.getLocation(context, safeNum);
        }

    }
}
