package com.chenluwei.weike.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lw on 2016/5/8.
 * 用来下载文件的数据库帮助类
 * 定义成单例模式
 */
public class DBHelper extends SQLiteOpenHelper {
    //表明
    private static final String DB_NAME = "download.db";
    private static final int VERSION = 1;
    //创建表的语法
    private static final String SQL_CREATE="create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer, url text, start integer, end integer, finished integer,fileName text,progress integer)";

//    private static final String SQL_CREATE_FILE="create table file_info(_id integer primary key autoincrement," +
//            " url text, fileName text, length integer)";
    //删除表的语法
    private static final String SQL_DROP = "drop table if exists thread_info";
  //  private static final String SQL_DROP_FILE = "drop table if exists file_info";

    private static DBHelper sHelper = null; //静态的对象引用

    /**
     * 通过单例模式获得类的对象
     * @param context
     */
    public static DBHelper getInstance(Context context){
        if(sHelper == null) {
            sHelper = new DBHelper(context);
        }
        return sHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
   //     db.execSQL(SQL_CREATE_FILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新需要先删除再重新创建
        db.execSQL(SQL_DROP);
      //  db.execSQL(SQL_DROP_FILE);
        db.execSQL(SQL_CREATE);
    }
}
