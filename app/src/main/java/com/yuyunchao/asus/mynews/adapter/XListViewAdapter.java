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
import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.MyBaseAdapter;
import com.yuyunchao.asus.mynews.biz.MyNewsManager;
import com.yuyunchao.asus.mynews.lib.xListView.XListView;

import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * Created by asus on 2016/10/25.
 */
public class XListViewAdapter extends MyBaseAdapter<NewsListEntity> {
    private XListView xl;
    private LruCache<String, Bitmap> lruCache;
    private MyNewsManager myNewsManager;
    public XListViewAdapter(Context mContext, XListView xl) {
        super(mContext);
        this.xl = xl;
        myNewsManager = new MyNewsManager(mContext);
        lruCache = new LruCache<>(1024 * 1024);
    }
    public XListViewAdapter(Context mContext){
        super(mContext);
        myNewsManager = new MyNewsManager(mContext);
        lruCache = new LruCache<>(1024 * 1024);
    }

    /**
     * 得到相应图片
     * @param url
     * @return
     */
    private Bitmap getBitmap(final String url){
        //图片名字
        final String iconName = url.substring(url.lastIndexOf("/") + 1);
        //根据图片名字得到缓存中的图片
        Bitmap bit = lruCache.get(iconName);
        //如果缓存中有该图片，直接return
        if (bit != null){
            Log.i("yyc", "缓存加载图片");
            return bit;
        }
        //如果本地中有该图片，return该图片
        bit = readCachaImg(iconName);
        if (bit != null){
            Log.i("yyc", "本地加载图片");
            return bit;
        }

        myNewsManager.MyBitmapRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                ImageView im = (ImageView) xl.findViewWithTag(url);
                if (im != null){
                    im.setImageBitmap(bitmap);
                    //将图片写入缓存
                    lruCache.put(iconName, bitmap);
                    Log.i("yyc", "网络加载图片");
                    //将图片写入本地
                    deposit(bitmap, iconName);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        return bit;
    }
    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_lsit, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView)convertView.findViewById(R.id.tv_item_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_item_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_item_time);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String title = myList.get(position).getTitle();
        String content = myList.get(position).getSummary();
        String time = myList.get(position).getStamp();
        String icon = myList.get(position).getIcon();
        holder.tv_title.setText(title);
        holder.tv_content.setText(content);
        holder.tv_time.setText(time);
        holder.iv_icon.setTag(icon);
        Bitmap b = getBitmap(icon);
        if (b != null){
            holder.iv_icon.setImageBitmap(b);
        }
        return convertView;
    }

    /**
     * 图片存放到本地
     */
    private void deposit(Bitmap bit,String iconNmae){
        File f = mContext.getCacheDir();
        FileOutputStream out = null;
        if (!f.exists()) {
            f.mkdir();
        }
        File mFile = new File(f, iconNmae);
        try {
            out = new FileOutputStream(mFile);
            bit.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //写入缓存
        lruCache.put(iconNmae, bit);

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
    public static class ViewHolder{
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_time;
        public ImageView iv_icon;
    }
}
