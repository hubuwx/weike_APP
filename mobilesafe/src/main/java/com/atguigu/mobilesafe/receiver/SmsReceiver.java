package com.atguigu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.atguigu.mobilesafe.dao.BlackNumDao;
import com.atguigu.mobilesafe.util.GpsUtils;
import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object[] pdus = (Object[]) bundle.get("pdus");
        byte[] pdu = (byte[]) pdus[0];
        SmsMessage smsMessage = SmsMessage.createFromPdu(pdu);
        String phoneNum = smsMessage.getOriginatingAddress();
        String message = smsMessage.getMessageBody();

        // 拦截黑名单短信
        BlackNumDao blackNumDao = new BlackNumDao(context);
        boolean isBlack = blackNumDao.isBlackNum(phoneNum);
        if (isBlack) {
            abortBroadcast();
            Log.e("TAG", "拦截到了黑名单短信");
            return;
        }

        // 必须开启防盗保护
        boolean isProtect = SpUtils.getInstance(context).get(SpUtils.PROTECT, false);
        String safeNum = SpUtils.getInstance(context).get(SpUtils.SAFE_NUM, null);
        if (!isProtect || safeNum == null) {
            return;
        }

        if ("#alarm#".equals(message)) {
            abortBroadcast();
            MsUtils.playAlert(context);
        } else if ("#wipedata#".equals(message)) {
            abortBroadcast();
            MsUtils.resetPhone(context);
        } else if ("#lockscreen#".equals(message)) {
            abortBroadcast();
            MsUtils.lockScreen(context);
        } else if ("#location#".equals(message)) {
            abortBroadcast();
            GpsUtils.getLocation(context, safeNum);
        }
    }
}
