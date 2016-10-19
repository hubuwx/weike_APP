package com.atguigu.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by miao on 2016/3/21.
 * 查询电话归属地
 */
public class AddressDao {
    private Context mContext;

    public AddressDao(Context context) {
        mContext = context;
    }

    //
    public String getLocation(String phoneNum) {
        String location = "未知";
        // 获取数据库连接
        String path = mContext.getFilesDir().getAbsolutePath() + "/address.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        String reg = "^1[345678]\\d{9}$";
        if (phoneNum.matches(reg)) {
            // 查询归属地
            String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
            String phoneNum7 = phoneNum.substring(0, 7);
            Cursor cursor = database.rawQuery(sql, new String[]{phoneNum7});
            // 遍历
            if (cursor.moveToNext()) {
                location = cursor.getString(0);
            }
            // 关闭资源
            cursor.close();
            database.close();

            return location;
        } else {
            switch (phoneNum.length()) {
                case 3:
                    location = "匪警号码";
                    break;
                case 4:
                    location = "模拟器";
                    break;
                case 5:
                    location = "客服电话";
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    location = "本地号码";
                    break;
                default:
                    //            	外地座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
                    if (phoneNum.length() >= 10 && phoneNum.startsWith("0")) {
                        String phoneNum23 = phoneNum.substring(1, 3);
                        String sql = "select location from data2 where area=?";
                        Cursor cursor = database.rawQuery(sql, new String[]{phoneNum23});
                        if (cursor.moveToNext()) {
                            String tempLocation = cursor.getString(0);
                            location = tempLocation.substring(0, tempLocation.length() - 2) + "座机";
                        } else {
                            String phoneNum234 = phoneNum.substring(1, 4);
                            cursor = database.rawQuery(sql, new String[]{phoneNum234});
                            if (cursor.moveToNext()) {
                                String tempLocation = cursor.getString(0);
                                location = tempLocation.substring(0, tempLocation.length() - 2) + "座机";
                            }
                        }

                        cursor.close();

                        return location;
                    }
                    break;
            }
        }
        return location;
    }
}
