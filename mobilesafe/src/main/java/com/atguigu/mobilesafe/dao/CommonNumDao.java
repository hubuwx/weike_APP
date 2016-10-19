package com.atguigu.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xfzhang on 2016/2/20.
 * 操作commonnum表的dao类
 */
public class CommonNumDao {

    public static List<String> getGroupList(Context context) {
        List<String> list = new ArrayList<>();
        //得到连接
        String path = context.getFilesDir().getAbsolutePath()+"/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //执行query, 得到cusorr
        Cursor cursor = database.rawQuery("select name from classlist", null);
        //遍历cursor, 保存list
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        //关闭
        cursor.close();
        database.close();

        return list;
    }

    public static List<List<String>> getChildList(Context context) {
        List<List<String>> list = new ArrayList<>();

        //得到连接
        String path = context.getFilesDir().getAbsolutePath()+"/commonnum.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        for (int i = 1; i <= 8; i++) {
            List<String> list2 = new ArrayList<>();
            //执行query, 得到cusorr
            Cursor cursor = database.rawQuery("select name,number from table" + i, null);
            //遍历cursor, 保存list
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                list2.add(name + "_" + number);
            }
            //添加到大集合中
            list.add(list2);
            //关闭
            cursor.close();
        }

        //关闭
        database.close();

        return list;
    }
}
