package com.atguigu.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xfzhang on 2016/2/22.
 * 操作antivirus库的dao类
 */
public class AntivirusDao {
    /**
     * 判断指定签名所对应的应用是否是病毒应用
     * @param context
     * @param md5
     * @return
     */
    public static boolean isVirus(Context context, String md5) {
        boolean virus = false;

        //得到连接
        String path = context.getFilesDir().getAbsolutePath()+"/antivirus.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //执行query, 得到cusorr
        Cursor cursor = database.rawQuery("select * from datable where md5=?", new String[]{md5});
        virus = cursor.moveToNext();
        cursor.close();
        database.close();
        return virus;
    }
}
