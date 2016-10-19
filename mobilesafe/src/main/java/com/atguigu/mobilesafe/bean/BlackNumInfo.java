package com.atguigu.mobilesafe.bean;

/**
 * Created by miao on 2016/3/22.
 */
public class BlackNumInfo {
    private int id;
    private String number;

    public BlackNumInfo(int id, String number) {
        this.id = id;
        this.number = number;
    }

    public BlackNumInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
