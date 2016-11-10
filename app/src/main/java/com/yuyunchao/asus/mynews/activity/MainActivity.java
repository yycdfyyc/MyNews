package com.yuyunchao.asus.mynews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.BaseActivity;
import com.yuyunchao.asus.mynews.biz.MyUserManager;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.fragment.AllPicFragment;
import com.yuyunchao.asus.mynews.fragment.MainFragment;
import com.yuyunchao.asus.mynews.fragment.LoginFragment;
import com.yuyunchao.asus.mynews.fragment.MyCollectionFragment;
import com.yuyunchao.asus.mynews.lib.circle_image_view.CircleImageView;
import com.yuyunchao.asus.mynews.lib.slidingmenu.SlidingMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private boolean is_exit = false; //点击退出键时的布尔值
    private long l_firstTime;//第一次点击的时间
    private long l_secondTime;//第二次点击的时间
    int bgColor = 0x33c85555;
    public LinearLayout[] ll_lift = new LinearLayout[5];
    public LinearLayout ll_shard;
    public SlidingMenu slidingMenu;
    public TextView tv_login,tv_main_title;
    public ImageView iv_main_right,iv_main_lift;
    public CircleImageView iv_user_icon;
    public boolean isLogin;
    private  MyUserManager myUserManager;
    private String uid;
    @Override
    protected int setContent() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        //加载SlidingMenu
        replaceFragment(new MainFragment());
        initSlidingMenu();
        tv_login = (TextView) slidingMenu.findViewById(R.id.tv_login);
        iv_user_icon = (CircleImageView) slidingMenu.findViewById(R.id.iv_login_icon);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        iv_main_lift = (ImageView) findViewById(R.id.iv_title_home_default);
        iv_main_right = (ImageView) findViewById(R.id.iv_title_share_default);


        ll_shard = (LinearLayout) findViewById(R.id.ll_shard);
        ll_lift[0] = (LinearLayout) findViewById(R.id.ll_news);
        ll_lift[1] = (LinearLayout) findViewById(R.id.ll_local);
        ll_lift[2] = (LinearLayout) findViewById(R.id.ll_read);
        ll_lift[3] = (LinearLayout) findViewById(R.id.ll_ties);
        ll_lift[4] = (LinearLayout) findViewById(R.id.ll_pics);

    }

    @Override
    protected void initListener() {
        tv_login.setOnClickListener(this);
        iv_user_icon.setOnClickListener(this);
        iv_main_right.setOnClickListener(this);
        iv_main_lift.setOnClickListener(this);
        for (int i = 0;i<ll_lift.length;i++){
            ll_lift[i].setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        autoLogin();
    }
    /**
     * 加载slidingMenu
     */
    private void initSlidingMenu(){
        //实例化
        slidingMenu = new SlidingMenu(this);
        //设置滑动方向
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        //设置是否可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置滑动距离
        slidingMenu.setBehindOffsetRes(R.dimen.behind_offset);
        //在当前的activity上滑动
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //设置左面布局
        slidingMenu.setMenu(R.layout.layout_lift);
        //设置右面布局
        slidingMenu.setSecondaryMenu(R.layout.layout__login_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击 登录与头像时
            case R.id.tv_login:
            case R.id.iv_login_icon:
                if (slidingMenu != null && slidingMenu.isMenuShowing()){
                    if (isLogin){
                        Intent intent = new Intent(this, UserActivity.class);
                        startActivity(intent);
                        slidingMenu.showContent();
                    }else {
                        replaceFragment(new LoginFragment());
                        setTitle("登录");
                        slidingMenu.showContent();
                    }
                }
                break;
            //点击 activity左边图片
            case R.id.iv_title_home_default:
                if (slidingMenu != null && slidingMenu.isMenuShowing()){
                    slidingMenu.showContent();
                }else {
                    slidingMenu.showMenu();
                }
                break;
            //点击 activity右边图片
            case R.id.iv_title_share_default:
                if (slidingMenu != null && slidingMenu.isMenuShowing()){
                    slidingMenu.showContent();
                }else {
                    slidingMenu.showSecondaryMenu();
                }
                break;
            case R.id.ll_news:
                setbgColor(0);
                switch (tv_main_title.getText().toString()){
                    case "新闻":
                    case "财经":
                    case "科技":
                    case "体育":
                        break;
                    default:
                        setTitle("新闻");
                        replaceFragment(new MainFragment());
                        break;
                }

                setGoneSlidingMenu();
                break;
            case R.id.ll_local:
                setbgColor(1);
                setGoneSlidingMenu();
                break;
            case R.id.ll_read:
                setbgColor(2);
                if (!tv_main_title.getText().equals("收藏")) {
                    setTitle("收藏");
                    replaceFragment(new MyCollectionFragment());
                }
                setGoneSlidingMenu();
                break;
            case R.id.ll_ties:
                setbgColor(3);
                setGoneSlidingMenu();
                break;
            case R.id.ll_pics:
                setbgColor(4);
                if (!tv_main_title.getText().equals("图片")) {
                    setTitle("图片");
                    replaceFragment(new AllPicFragment());
                }
                setGoneSlidingMenu();
                break;


        }
    }

    /**
     * 设置标题
     * @param s
     */
    public void setTitle(String s){
        tv_main_title.setText(s);
    }

    /**
     * 加载Fragment
     * @param f
     */
    public void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_con,f);
        ft.commit();
    }
    /**
     * 按键点击的方法
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(slidingMenu != null && slidingMenu.isMenuShowing()){
                slidingMenu.showContent();
                return true;
            }
            switch (tv_main_title.getText().toString()){
                case "登录":
                    tv_main_title.setText("新闻");
                    replaceFragment(new MainFragment());
                    break;
                case "注册":
                    tv_main_title.setText("登录");
                    replaceFragment(new LoginFragment());
                    break;
                case "图片":
                case "财经":
                case "科技":
                case "体育":
                case "收藏":
                case "新闻":
                    doubleClickExit(keyCode,event);
                    break;
            }

        }
        return true;
    }

    /**
     * 双击退出按钮退出
     * @param keyCode
     * @param event
     */
    private void doubleClickExit(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && !is_exit){
            //第一次点击时的时间戳
            l_firstTime = System.currentTimeMillis();
            //重置退出为true
            is_exit=true;
            //弹窗提示
            Toast.makeText(this, "再按一次退出",Toast.LENGTH_SHORT).show();
        }else if(keyCode == KeyEvent.KEYCODE_BACK && is_exit){
            //第二次点击时的时间戳
            l_secondTime = System.currentTimeMillis();

            if(l_secondTime - l_firstTime <2000){
                //如果两次点击时间不超过2s则退出
                finish();
            }else{
                //如果两次点击时间超过2s则重置退出为false
                is_exit=false;
                //并回调自身
                doubleClickExit(keyCode,event);
            }
        }
    }

    /**
     * 自动登录
     */
    public void autoLogin(){
        myUserManager = new MyUserManager(this);
        SharedPreferences p = getSharedPreferences("mylogin",MODE_PRIVATE);
        isLogin = p.getBoolean("login",false);
        uid = p.getString("user_name", "");
        if (isLogin){
            String s = p.getString("user_name","");
            Bitmap bitmap = myUserManager.getHeadBitMap(s + ".jpg");
            if (bitmap != null){
                iv_user_icon.setImageBitmap(bitmap);
            }else {
                String token = p.getString("user_token", "");
                myUserManager.MyUserStringRequest(ServiceConstant.SERVICE_NAME +
                        "user_home?ver=1&imei=111111111111&token=" + token, listener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            }


            tv_login.setText(s);
            ll_shard.setVisibility(View.GONE);
        }else {
            iv_user_icon.setImageResource(R.drawable.biz_pc_main_info_profile_avatar_bg_dark);
            tv_login.setText("立即登录");
            ll_shard.setVisibility(View.VISIBLE);
        }
    }
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject object = jsonObject.getJSONObject("data");
                String url = object.getString("portrait");
                myUserManager.MyUserBitmapRequest(url, bitmapListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Response.Listener<Bitmap> bitmapListener = new Response.Listener<Bitmap>() {
        @Override
        public void onResponse(Bitmap bitmap) {
            if (bitmap!=null)
            iv_user_icon.setImageBitmap(bitmap);
            //存放到缓存文件中
            File file = new File(getCacheDir(),"userImage");
            if (!file.exists()){
                file.mkdirs();
            }
//            String userIconName = user_icon.substring(user_icon.lastIndexOf("/") + 1);
            String userIconName = uid + ".jpg";
            File icon = new File(file, userIconName);
            try {
                OutputStream outputStream = new FileOutputStream(icon);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 设置左边布局的背景颜色
     * @param index
     */
    private void setbgColor(int index){
        for (int i = 0;i<ll_lift.length;i++){
            ll_lift[i].setBackgroundColor(getResources().getColor(R.color.tm));
        }
        ll_lift[index].setBackgroundColor(bgColor);
    }

    /**
     * 使slidingMenu隐藏
     */
    public void setGoneSlidingMenu(){
        if (slidingMenu != null && slidingMenu.isMenuShowing()){
            slidingMenu.showContent();
        }
    }



}
