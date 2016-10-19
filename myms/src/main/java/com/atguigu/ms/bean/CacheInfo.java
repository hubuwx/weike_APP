package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by lw on 2016/4/8.
 */
public class CacheInfo {
    private String appName;
    private Drawable icon;
    private String packageName;
    private long cacheSize;

    public CacheInfo(String appName, Drawable icon, String packageName, long cacheSize) {
        this.appName = appName;
        this.icon = icon;
        this.packageName = packageName;
        this.cacheSize = cacheSize;
    }

    public CacheInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
