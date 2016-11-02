package com.yuyunchao.asus.mynews.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyunchao.asus.mynews.Entry.UserLoginLogEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.MyBaseAdapter;

/**
 * Created by asus on 2016/11/1.
 */
public class UserLoginLogAdapter extends MyBaseAdapter<UserLoginLogEntity> {
    public UserLoginLogAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user_loginlog, null);
            holder = new ViewHolder();
            holder.tv_address = (TextView)convertView.findViewById(R.id.tv_item_user_address);
            holder.tv_time = (TextView)convertView.findViewById(R.id.tv_item_user_time);
            holder.tv_device = (TextView) convertView.findViewById(R.id.tv_item_user_device);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_device.setText(myList.get(position).getDevice());
        holder.tv_address.setText(myList.get(position).getAddress());
        holder.tv_time.setText(myList.get(position).getTime());
        return convertView;
    }



    public static class ViewHolder{
        public TextView tv_device;
        public TextView tv_address;
        public TextView tv_time;
    }
}
