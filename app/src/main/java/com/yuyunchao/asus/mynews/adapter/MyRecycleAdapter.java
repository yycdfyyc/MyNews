package com.yuyunchao.asus.mynews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuyunchao.asus.mynews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/11/3.
 */
public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Bitmap> list;
    private ArrayList<Integer> mHeights;
    private MyViewHolder holder;
    private int mScreenWidth;
    public MyRecycleAdapter(Context context,ArrayList<Bitmap> list,int mScreenWidth){
        this.context = context;
        this.list = list;
        mHeights = new ArrayList<>();
        this.mScreenWidth = mScreenWidth;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         holder = new MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_recycle_view_all_pic,
                        parent,
                        false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (mHeights.size() <= position) {
            mHeights.add((int) (100 + Math.random() * 300));
        }
        ViewGroup.LayoutParams lp = holder.iv_img.getLayoutParams();
        lp.height = mHeights.get(position);
        lp.width = mScreenWidth / 3;
        holder.iv_img.setLayoutParams(lp);
        holder.iv_img.setImageBitmap(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_img;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_item_recycle);
        }
        public ImageView getIv_img() {
            return iv_img;
        }
    }
}
