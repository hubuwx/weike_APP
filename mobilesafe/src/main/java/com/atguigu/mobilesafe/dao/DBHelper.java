package com.atguigu.mobilesafe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by miao on 2016/3/22.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "ms.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL("create table black_num(_id integer primary key autoincrement, number varchar)");

       db.execSQL("create table app_lock(_id integer primary key autoincrement, package_name varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table app_lock(_id integer primary key autoincrement, package_name varchar)");
    }
}
