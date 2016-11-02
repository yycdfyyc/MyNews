package com.yuyunchao.asus.mynews.biz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yuyunchao.asus.mynews.Entry.UserEntity;
import com.yuyunchao.asus.mynews.MyApplication;
import com.yuyunchao.asus.mynews.activity.MainActivity;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.lib.volley.MultiPosttRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 用户管理类
 * Created by asus on 2016/11/1.
 */
public class MyUserManager {

    RequestQueue requestQueue;
    Context mContext;
    String iconUrl;
    public MyUserManager(Context context){
        requestQueue = Volley.newRequestQueue(context);
        mContext = context;
    }

    /**
     * 获取网络头像
     */
    public void MyUserBitmapRequest(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener){
        ImageRequest imageRequest = new ImageRequest(url,listener,0,0, Bitmap.Config.RGB_565,errorListener);
        requestQueue.add(imageRequest);
    }

    /**
     * 获取本地头像
     */
    public Bitmap getHeadBitMap(String url){
        if (url == null) {
            return null;
        }
        String headIconName = url;
        File file = new File(mContext.getCacheDir() + File.separator + "userImage");
        if (!file.exists()){
            file.mkdirs();
        }
        File f[] = file.listFiles();
        for (int i = 0; i < f.length; i++) {
            if (f[i].getName().equals(headIconName)) {
                return BitmapFactory.decodeFile(f[i].getAbsolutePath());
            }
        }
        return null;
    }

    /**
     * 获取头像
     */
    public Bitmap getHeadBitMap(UserEntity entity, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener){
        Bitmap b = getHeadBitMap(entity.getUid()+".jpg");
        if (b != null) {
            return b;
        }
        MyUserBitmapRequest(entity.getIconUrl(),listener,errorListener);
        return null;
    }

    public void MyUserStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        StringRequest stringRequest = new StringRequest(url, listener, errorListener);
        requestQueue.add(stringRequest);
    }

    /**
     * 上传头像
     */
    public void upHeadIcon(File f, String token, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        String url = ServiceConstant.SERVICE_NAME+"user_image?token="+token;
        MultiPosttRequest request = new MultiPosttRequest(url, listener, errorListener);
        request.buildMultipartEntity("portrait", f);
        requestQueue.add(request);
    }


    /**
     * 登录用建立连接
     * @param user
     * @param pwd
     * @return
     */
    public String loginService(String user,String pwd){
        //http://118.244.212.82:9092/newsClient/
        //user_login?ver=版本号&uid=用户名&pwd=密码&device=0
        try {
            StringBuffer s = new StringBuffer();
            //1.建立连接
            URL url = new URL(
                    ServiceConstant.SERVICE_NAME + "user_login");
            String content ="ver=1"+"&uid="+user+"&pwd="+pwd+"&device=0";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //2.设置请求头
            //请求模式
            connection.setRequestMethod("POST");
            //请求头格式
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",content.length()+"");
            //允许HttpURLConnection可以使用输出流
            connection.setDoOutput(true);
            //获取输出流
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.close();

            int code = connection.getResponseCode();
            if (code==200){
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream buffer = new BufferedInputStream(inputStream);
                byte[] bytes = new byte[1024];
                int count = 0;
                while ((count = buffer.read(bytes))!=-1){
                    s.append(new String(bytes,0,count));
                }
                inputStream.close();
                return s.toString();
            }else if (code == 404){
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
