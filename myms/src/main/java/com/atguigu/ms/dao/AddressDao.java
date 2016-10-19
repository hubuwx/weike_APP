package com.atguigu.ms.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lenovo on 2016/3/29.
 */
public class AddressDao {
    private Context mContext;

    public AddressDao(Context context) {
        mContext = context;
    }

    // 查询号码归属地
    public String getAddress(String number) {
        String address = "未知";

        // 获取数据库
        String path = mContext.getFilesDir().getAbsolutePath() + "/address.db";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        String reg = "^1[345678]\\d{9}$";

        if(number.matches(reg)) {// 符合手机号码规则
//        	手机号: 共11位数字, 第一个为: 1, 第二位为: 3—8, 前7位决定其归属地
            String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
            String num7 = number.substring(0,7);
            Cursor cursor = database.rawQuery(sql, new String[]{num7});
            if(cursor.moveToNext()) {
                address = cursor.getString(0);
            }

            cursor.close();
            database.close();

            return address;
        }else {
//        	匪警号码 : 3位
//        	模拟器 : 4位
//        	客服电话 : 5位
//        	本地号码 : 6,7,8,9位
//        	座机号: 大于或等于10位, 以0开头, 其第2,3位或2,3,4位决定其归属地
            switch (number.length()){
                case 3:
                    address = "匪警号码";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    address = "本地号码";
                    break;
                default:
                    if(number.length() >= 10 && number.startsWith("0")) {
                        //其第2,3位或2,3,4位决定其归属地
                        String num23 = number.substring(1,3);
                        String sql = "select location from data2 where area=?";
                        Cursor cursor = database.rawQuery(sql, new String[]{num23});
                        if(cursor.moveToNext()) {//2 3位
                            address = cursor.getString(0);
                            address = address.substring(0,address.length()-2)+"座机";
                        }else {//或2,3,4位
                            String num234 = number.substring(1, 4);
                            cursor = database.rawQuery(sql, new String[]{num234});
                            if(cursor.moveToNext()){
                                address = cursor.getString(0);
                                address = address.substring(0,address.length()-2)+"座机";
                            }
                        }

                        // 关闭资源
                        cursor.close();
                        database.close();

                        return  address;
                    }
                    break;
            }
        }

        return address;
    }
}
