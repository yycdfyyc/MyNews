package com.yuyunchao.asus.mynews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyunchao.asus.mynews.Entry.CommentListEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.MyBaseAdapter;
import com.yuyunchao.asus.mynews.biz.MyNewsManager;
import com.yuyunchao.asus.mynews.lib.xListView.XListView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by asus on 2016/10/28.
 */
public class CommentAdapter extends MyBaseAdapter<CommentListEntity> {
    private LruCache<String,Bitmap> lruCache;
    private XListView xl;
    private MyNewsManager myNewsManager;

    public CommentAdapter(Context mContext, XListView xl) {
        super(mContext);
        this.xl = xl;
         myNewsManager = new MyNewsManager(mContext);
        lruCache = new LruCache<>(1024 * 1024);
    }
    /**
     * 得到相应图片
     * @param url
     * @return
     */
    private Bitmap getBitmap(final String url, final int cid){
        //图片名字
        final String iconName = url.substring(url.lastIndexOf("/") + 1);
        //根据图片名字得到缓存中的图片
        Bitmap bit = lruCache.get(iconName);
        //如果缓存中有该图片，直接return
        if (bit != null){
            Log.i("yyc", "缓存加载icon图片");
            return bit;
        }
        //如果本地中有该图片，return该图片
        bit = readCachaImg(iconName);
        if (bit != null){
            Log.i("yyc", "本地加载icon图片");
            return bit;
        }

        myNewsManager.MyBitmapRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                ImageView im = (ImageView) xl.findViewWithTag(cid);
                if (im != null){
                    im.setImageBitmap(bitmap);
                    //将图片写入缓存
                    lruCache.put(iconName, bitmap);
                    Log.i("yyc", "网络加载icon图片");
                    //将图片写入本地
                    deposit(bitmap, iconName);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("yyc", "网络加载失败");
            }
        });
        return bit;
    }
    /**
     * 图片存放到本地
     */
    private void deposit(Bitmap bit,String iconName){
        File f = mContext.getCacheDir();
        FileOutputStream out = null;
        if (!f.exists()) {
            f.mkdir();
        }
        File mFile = new File(f, iconName);
        try {
            out = new FileOutputStream(mFile);
            bit.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //写入缓存
        lruCache.put(iconName, bit);

    }

    /**
     * 读取本地图片
     */
    private Bitmap readCachaImg(String icon){
        Bitmap bitmap = null;
        File[] files = mContext.getCacheDir().listFiles();
        File mFile = null;
        for (File f: files) {
            if (icon.equals(f.getName())){
                mFile = f;
                break;
            }
        }
        if (mFile == null){
            return null;
        }
        bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
        lruCache.put(icon, bitmap);
        return bitmap;
    }

    /**
     * 计算时间
     */
    private String jishuangsj(String time){
        //yyyy-MM-dd HH:mm:ss
        String tx = null;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            Date nowData = new Date();
            long xctime = nowData.getTime() - date.getTime();
            int ss = (int) (xctime / 1000);
            int ms = (int) (xctime / (60 * 1000));
            int hs = (int) (xctime / (60 * 60 * 1000));
            int days = (int) (xctime / (24 * 60 * 60 * 1000));
            int months = (int) (xctime / (30 * 24 * 60 * 60 * 1000));
            int years = (int) (xctime / (365 * 24 * 60 * 60 * 1000));
            if (ss < 60) {
                tx = "刚刚";
            }else {
                if (ms < 60) {
                    tx = ms + "分前";
                } else {
                    if (hs < 24) {
                        tx = hs + "小时前";
                    } else {
                        if (days < 30) {
                            tx = days + "天前";
                        } else {
                            if (months < 12) {
                                tx = months + "月前";
                            } else {
                                tx = years + "年前";
                            }
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tx;
    }
    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comment, null);
            holder = new ViewHolder();
            holder.tv_content = (TextView)convertView.findViewById(R.id.tv_comment_item_content);
            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_comment_item_time);
            holder.iv_user = (ImageView) convertView.findViewById(R.id.iv_comment_item_userIcon);
            holder.tv_username = (TextView) convertView.findViewById(R.id.tv_comment_item_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        int cid = myList.get(position).getCid();
        String times = myList.get(position).getStamp();
        holder.tv_username.setText(myList.get(position).getUid());
        holder.tv_content.setText(myList.get(position).getComContent());
        holder.tv_time.setText(jishuangsj(times));
        holder.iv_user.setTag(cid);
        String icon = myList.get(position).getPortrait();
        if (icon == null){
            icon = "http:\\/\\/118.244.212.82:9092\\/Images\\/image.png";
        }
        Bitmap b = getBitmap(icon,cid);
        if (b != null) {
            holder.iv_user.setImageBitmap(b);
        }
        return convertView;
    }



    public static class ViewHolder{
        public TextView tv_username;
        public TextView tv_content;
        public TextView tv_time;
        public ImageView iv_user;
    }
}
