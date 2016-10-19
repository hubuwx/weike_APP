package com.atguigu.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by miao on 2016/3/22.
 * app应用详情 bean
 */
public class AppInfo {
    private String packageName;
    private String appName;
    private Drawable icon;
    private boolean isSystem;

    public AppInfo(String packageName, String appName, Drawable icon, boolean isSystem) {
        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.isSystem = isSystem;
    }

    public AppInfo() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfo appInfo = (AppInfo) o;

        return packageName.equals(appInfo.packageName);
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
