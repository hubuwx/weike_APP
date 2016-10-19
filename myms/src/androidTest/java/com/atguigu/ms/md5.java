package com.atguigu.ms;

import android.test.AndroidTestCase;
import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by lenovo on 2016/3/26.
 */
public class md5 extends AndroidTestCase {

    public void TestMd5(){
        String num = md5("12345");
        Log.e("TAG1111",num);
    }

    public static String md5(String pwd) {
        StringBuffer sb = new StringBuffer();
        try {
            //创建用于加密的加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");
            //将字符串转换为一个16位的byte[]
            byte[] bytes = digest.digest(pwd.getBytes("utf-8"));
            for(byte b : bytes) {//遍历
                //与255(0xff)做与运算(&)后得到一个255以内的数值
                int number = b & 255;//也可以& 0xff
                //转化为16进制形式的字符串, 不足2位前面补0
                String numberString = Integer.toHexString(number);
                if(numberString.length()==1) {
                    numberString = 0+numberString;
                }
                //连接成密文
                sb.append(numberString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
