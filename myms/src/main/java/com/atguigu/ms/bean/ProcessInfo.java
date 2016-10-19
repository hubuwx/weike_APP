package com.atguigu.ms.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by lw on 2016/4/5.
 */

    public class ProcessInfo {
        private String packageName;
        private String appName;
        private Drawable icon;
        private long memSize;
        private boolean isSystem;
        private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public ProcessInfo(String packageName, String appName, Drawable icon, long memSize, boolean isSystem) {
            this.packageName = packageName;
            this.appName = appName;
            this.icon = icon;
            this.memSize = memSize;
            this.isSystem = isSystem;
        }

        public ProcessInfo() {
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

        public long getMemSize() {
            return memSize;
        }

        public void setMemSize(long memSize) {
            this.memSize = memSize;
        }

        public boolean isSystem() {
            return isSystem;
        }

        public void setIsSystem(boolean isSystem) {
            this.isSystem = isSystem;
        }

        @Override
        public String toString() {
            return "ProcessInfo{" +
                    "packageName='" + packageName + '\'' +
                    ", appName='" + appName + '\'' +
                    ", icon=" + icon +
                    ", memSize=" + memSize +
                    ", isSystem=" + isSystem +
                    '}';
        }
}
