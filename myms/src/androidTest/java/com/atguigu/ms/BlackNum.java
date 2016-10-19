package com.atguigu.ms;

import android.test.AndroidTestCase;
import android.util.Log;

import com.atguigu.ms.bean.BlackNumInfo;
import com.atguigu.ms.dao.BlackNumDao;

import java.util.List;

/**
 * Created by lenovo on 2016/3/30.
 */
public class BlackNum extends AndroidTestCase {

    public  void testGet(){
        BlackNumDao blackNumDao = new BlackNumDao(getContext());

        List<BlackNumInfo> list = blackNumDao.get();

        Log.e("tag","get:"+list.toString());
    }

    public void testAdd(){
        BlackNumDao blackNumDao = new BlackNumDao(getContext());

        int id = blackNumDao.add(new BlackNumInfo(-1,"122334"));

        Log.e("tag","id:"+id);
    }


    public void testUpdate(){
        BlackNumDao blackNumDao = new BlackNumDao(getContext());
        blackNumDao.update(new BlackNumInfo(1,"110"));
    }

    public void testDelete(){
        BlackNumDao blackNumDao = new BlackNumDao(getContext());
        blackNumDao.deleteById(2);
    }
}
