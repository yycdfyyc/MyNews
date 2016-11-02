package com.yuyunchao.asus.mynews.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyunchao.asus.mynews.Entry.NewsTypeEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.MyBaseAdapter;


/**
 * Created by asus on 2016/10/18.
 */
public class HorizontalListViewAdapter extends MyBaseAdapter<NewsTypeEntity>{
    private int itemPosition;
    public HorizontalListViewAdapter(Context mContext) {
        super(mContext);
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }
    @Override
    public View getMyView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_horizontal_list, null);
            holder = new ViewHolder();
            holder.tv = (TextView)convertView.findViewById(R.id.tv_item_hor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String s = myList.get(position).getSubgroup();
        holder.tv.setText(s);
        if (itemPosition == position)
            holder.tv.setTextColor(Color.RED);
        return convertView;
    }

    public static class ViewHolder{
        public TextView tv;
    }
}
