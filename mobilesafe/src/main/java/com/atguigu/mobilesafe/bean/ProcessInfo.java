package com.atguigu.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by xfzhang on 2016/2/19.
 * 进程信息
 */
public class ProcessInfo {
    private String packageName;
    private String appName;
    private Drawable icon;
    private boolean isSystem;
    private int memSize;
    private boolean checked;//标识是否勾选

    public ProcessInfo() {
    }

    public ProcessInfo(String packageName, String appName, Drawable icon, boolean isSystem, int memSize) {

        this.packageName = packageName;
        this.appName = appName;
        this.icon = icon;
        this.isSystem = isSystem;
        this.memSize = memSize;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", icon=" + icon +
                ", isSystem=" + isSystem +
                ", memSize=" + memSize +
                '}';
    }
}
