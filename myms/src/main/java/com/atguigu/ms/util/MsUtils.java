package com.atguigu.ms.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Debug;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.atguigu.ms.R;
import com.atguigu.ms.activity.TrafficManagerActivity;
import com.atguigu.ms.bean.AppInfo;
import com.atguigu.ms.bean.ProcessInfo;
import com.atguigu.ms.bean.TrafficInfo;
import com.atguigu.ms.service.UpdateWidgetService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/3/25.
 * 系统应用的工具类
 */
public class MsUtils {

    // 获取网络连接状态
    public static boolean isContect(Context context) {
        boolean isContect = false;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            isContect = activeNetworkInfo.isConnected();
        }
        Log.i("Q11", "__" + isContect);
        return isContect;
    }

    // toast封装方法
    public static void showMsg(Context context, String msg) {

        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    // md5加密
    public static String md5(String pwd) {
        StringBuffer sb = new StringBuffer();
        try {
            //创建用于加密的加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");
            //将字符串转换为一个16位的byte[]
            byte[] bytes = digest.digest(pwd.getBytes("utf-8"));
            for(byte b : bytes) {//遍历
                //与255(0xff)做与运算(&)后得到一个255以内的数值
                int number = b & 255;//也可以& 0xff
                //转化为16进制形式的字符串, 不足2位前面补0
                String numberString = Integer.toHexString(number);
                if(numberString.length()==1) {
                    numberString = 0+numberString;
                }
                //连接成密文
                sb.append(numberString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // 获取SIM卡号
    public static String getSimNum(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return manager.getSimSerialNumber();
    }

    // 发送短信
    public static void sendSms(String safeNum, String msg) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(safeNum,null,msg,null,null);
    }

    // 播放报警音乐
    public static void playAlarm(Context context, int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.setVolume(1, 1);
//        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    // 销毁数据
    public static void resetPhone(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.wipeData(0);
    }


    // 远程锁屏
    public static void lockScreen(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.resetPassword("23456", 0);//重新设置密码
        manager.lockNow();//锁屏
    }

    // 判断服务是否开启
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices =
                am.getRunningServices(Integer.MAX_VALUE); //取出所有运行的
        for(ActivityManager.RunningServiceInfo info : runningServices) {
            String serviceClassName = info.service.getClassName();
            if(serviceClassName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    // 获取所有app 信息(系统的和用户的)
    public static Map<Boolean, List<AppInfo>> getAllAppInfo(Context context) {
        SystemClock.sleep(1000);
        Map<Boolean, List<AppInfo>> map  = new HashMap<>();

        List<AppInfo> systemInfos = new ArrayList<>();
        map.put(true,systemInfos);

        List<AppInfo> customInfos = new ArrayList<>();
        map.put(false,customInfos);

        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;
            Drawable icon = ri.loadIcon(packageManager);
            String appName = ri.loadLabel(packageManager).toString();
            boolean isSystemApp = false;

            try {
                isSystemApp = isSystemApp(packageManager, packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppInfo appInfo = new AppInfo(appName,packageName,icon,isSystemApp);

            if (appInfo.isSystem()) {
                systemInfos.add(appInfo);
            } else {
                customInfos.add(appInfo);
            }
        }

        return map;
    }

    private static  boolean isSystemApp(PackageManager pm, String packageName) throws Exception {
        PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
    }

    // 获取所有进程信息
    public static void getAllProcessInfos(Context context,
                                          List<ProcessInfo> systemProcessInfos, List<ProcessInfo> userProcessInfos) {

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            ProcessInfo info = new ProcessInfo();
            // 包名
            String packageName = processInfo.processName;
            info.setPackageName(packageName);
            // 应用占用的内存  bit/byte
            Debug.MemoryInfo memoryInfo = am
                    .getProcessMemoryInfo(new int[] { processInfo.pid })[0];
            long memInfoSize = memoryInfo.getTotalPrivateDirty() * 1024;
            info.setMemSize(memInfoSize);
            try {
                // 图标
                Drawable icon = pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
                info.setIcon(icon);
                // 应用名称
                String name = pm.getPackageInfo(packageName, 0).applicationInfo
                        .loadLabel(pm).toString();
                info.setAppName(name);
                // 是否是系统应用进程
                int flag = pm.getPackageInfo(packageName, 0).applicationInfo.flags;
                if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {// 用户进程
                    info.setIsSystem(false);
                } else {// 系统进程
                    info.setIsSystem(true);
                }
            } catch (Exception e) {//根据包名得到不到PackageInfo
                e.printStackTrace();
                info.setIcon(context.getResources().getDrawable(R.mipmap.logo));
                info.setAppName(packageName);
                info.setIsSystem(true);
            }
            //不同类型的Info保存到不同的集合中
            if(info.isSystem()) {
                systemProcessInfos.add(info);
            } else {
                userProcessInfos.add(info);
            }
        }
    }

    // 获取手机中可用内存大小
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    // 获取手机中总内存大小
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMem(Context context) {
        long totalMem = 0;
        int sysVersion = Build.VERSION.SDK_INT; // 得到当前系统的版本号
        // 下面的方式只能在JELLY_BEAN(16)及以上版本才有用
        if (sysVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memoryInfo);
            totalMem = memoryInfo.totalMem;
        } else {
            try { // 在版本小于16时, 读取/proc/meminfo文件的第一行来获取总大小
                File file = new File("/proc/meminfo");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(fis));

                String result = reader.readLine();// MemTotal: 510484 kB
                result = result.substring(result.indexOf(":") + 1,
                        result.indexOf("k")).trim();// 510484

                reader.close();
                totalMem = Integer.parseInt(result) * 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return totalMem;
    }

    // 格式化
    public static String formatSize(Context context, long byteSize){
        return Formatter.formatFileSize(context, byteSize);
    }

    //获取所有进程数
    public static int getAllProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        return processInfos.size();
    }

    public static void clearAllProcess(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            ProcessInfo info = new ProcessInfo();
            // 包名
            String packageName = processInfo.processName;
            if(context.getPackageManager().equals(packageName)) {
                continue;
            }

            //杀死进程
            am.killBackgroundProcesses(packageName);
        }
    }
    /**
     * 得到应用的所有流量信息
     */
    public static List<TrafficInfo> getAllTrafficInfos(Context context) {
        SystemClock.sleep(1000);
        List<TrafficInfo> list = new ArrayList<TrafficInfo>();
        PackageManager pm = context.getPackageManager();
        //安装的所有应用(包含没有主界面的)
        List<ApplicationInfo> infos = pm.getInstalledApplications(0);
        for(ApplicationInfo info : infos) {
            TrafficInfo trafficInfo = new TrafficInfo();
            //appName
            String appName = info.loadLabel(pm).toString();
            trafficInfo.setAppName(appName);
            //icon
            Drawable icon = info.loadIcon(pm);
            trafficInfo.setIcon(icon);

            int uid = info.uid;   //userID
            //inSize 下载流量
            long inSize = TrafficStats.getUidRxBytes(uid); //receive
            trafficInfo.setInSize(inSize);
            //outSize 上传流量
            long outSize = TrafficStats.getUidTxBytes(uid);
            trafficInfo.setOutSize(outSize);
            list.add(trafficInfo);
        }
        return list;
    }
}
