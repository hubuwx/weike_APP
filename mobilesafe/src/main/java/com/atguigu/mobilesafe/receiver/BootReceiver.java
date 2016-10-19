package com.atguigu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.atguigu.mobilesafe.util.MsUtils;
import com.atguigu.mobilesafe.util.SpUtils;

/**
 * 监听开机启动
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取是否开启
        boolean isProtect = SpUtils.getInstance(context).getBoolean(SpUtils.PROTECT, false);
        String safeNum = SpUtils.getInstance(context).getString(SpUtils.SAFE_NUM, null);
        if (!isProtect || safeNum == null) {
            return;
        }

        // 获取sim卡
        String saveSimNum = SpUtils.getInstance(context).getString(SpUtils.SIM_NUM, null);
        String simNumber = MsUtils.getSimNumber(context)+"a";
        if(!saveSimNum.equals(simNumber)) {
            MsUtils.sendSmsMsg(safeNum,"sim card change");
        }
    }


}
