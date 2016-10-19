package com.atguigu.ms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.atguigu.ms.util.MsUtils;
import com.atguigu.ms.util.SpUtils;

// 开机广播接收者
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 安全号码  开启保护  SIM卡号
        String safeNum = SpUtils.getInstance(context).get(SpUtils.SAFE_NUM,null);
        boolean isProtect =SpUtils.getInstance(context).get(SpUtils.PROTECT,false);
        String simNum = SpUtils.getInstance(context).get(SpUtils.SIM_NUM,null);

        if(safeNum== null || !isProtect || simNum == null) {
            Log.e("TAG","开机广播 保护失败");
            return;
        }

        // 获取当前SIM卡号码
        String currentSim = MsUtils.getSimNum(context)+"123";

        if(!simNum.equals(currentSim)) {
            MsUtils.sendSms(safeNum,"sim card changed!");
        }

    }
}
