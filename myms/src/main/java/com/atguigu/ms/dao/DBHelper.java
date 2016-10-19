package com.atguigu.ms.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2016/3/30.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "ms.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建黑名单数据库
        db.execSQL("create table black_num(_id Integer primary key autoincrement,number varchar)");
        // 创建程序锁表
        db.execSQL("create table app_lock(_id Integer primary key autoincrement,package_name varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 创建程序锁表
        db.execSQL("create table app_lock(_id Integer primary key autoincrement,package_name varchar)");
    }
}
