package com.yuyunchao.asus.mynews.biz;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by asus on 2016/10/26.
 */
public class MyNewsManager {
    RequestQueue requestQueue;
    public MyNewsManager(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }
    public void MyBitmapRequest(String url, Response.Listener<Bitmap> listener, Response.ErrorListener errorListener){
        ImageRequest imageRequest = new ImageRequest(url,listener,0,0, Bitmap.Config.RGB_565,errorListener);
        requestQueue.add(imageRequest);
    }
    public void MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        StringRequest stringRequest = new StringRequest(url, listener, errorListener);
        requestQueue.add(stringRequest);
    }
}
