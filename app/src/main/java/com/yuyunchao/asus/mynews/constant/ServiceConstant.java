package com.yuyunchao.asus.mynews.constant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by asus on 2016/10/24.
 */
public class ServiceConstant {
    public static final String SERVICE_NAME = "http://118.244.212.82:9092/newsClient/";
    public static final int SERVICE_VERSION = 1;
    /**
     * 判断邮箱的正则表达式
     * @param s
     * @return
     */
    public static boolean verifyEmail(String s){
        if (s.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")){
            return true;
        }
        return false;
    }
    /**
     * 判断用户名的正则表达式
     * @param s
     * @return
     */
    public static boolean verifyUserName(String s){
        if (s.matches("[a-zA-Z0-9_]{6,24}")){
            return true;
        }
        return false;
    }
    /**
     * 判断密码的正则表达式
     * @param s
     * @return
     */
    public static boolean verifyPwd(String s){
        if (s.matches("[a-zA-Z0-9_]{6,24}")){
            return true;
        }
        return false;
    }

    /**
     * 判断网络连接状态
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        }
        return true;
    }
}
