package com.yuyunchao.asus.mynews;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.View;

import com.yuyunchao.asus.mynews.biz.MyUserManager;

/**
 * Created by asus on 2016/11/2.
 */
public class MyApplication extends Application {
    public boolean isLogin;
    public String token;
    public String userName;
    public String userPwd;
    MyUserManager myUserManager;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences  = getSharedPreferences("mylogin",MODE_PRIVATE);
        isLogin = preferences.getBoolean("login",false);
        token = preferences.getString("user_token", "");
        userName = preferences.getString("user_name", "");
        userPwd = preferences.getString("user_pwd",userPwd);
        if (isLogin) {
            myUserManager = new MyUserManager(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myUserManager.loginService(userName, userPwd);
                }
            }).start();
        }
    }
}
