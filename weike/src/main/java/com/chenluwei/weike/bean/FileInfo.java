package com.chenluwei.weike.bean;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by lw on 2016/4/11.
 */
public class FileInfo extends BmobObject {

    public FileInfo() {

    }

    private BmobFile file;
    private String descripition;

    public FileInfo(String descripition, BmobFile file) {
        this.descripition = descripition;
        this.file = file;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }

    public String getDescripition() {
        return descripition;
    }

    public void setDescripition(String descripition) {
        this.descripition = descripition;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "file=" + file +
                ", descripition='" + descripition + '\'' +
                '}';
    }
}
