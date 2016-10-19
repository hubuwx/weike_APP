package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by lenovo on 2016/4/1.
 * 软件管理bean对象
 */
public class AppInfo {
    private String appName;// 名称
    private String packageName;// 包名
    private Drawable icon;// 图标
    private boolean isSystem;// 系统应用 用户应用

    public AppInfo(String appName, String packageName, Drawable icon, boolean isSystem) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.isSystem = isSystem;
    }

    public AppInfo() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

        AppInfo info = (AppInfo) o;

        return packageName.equals(info.packageName);

    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }
}
