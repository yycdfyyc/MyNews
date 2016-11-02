package com.yuyunchao.asus.mynews.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Guouanthu on 16/10/18.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter{

    protected ArrayList<T> myList = new ArrayList<T>();
    protected LayoutInflater inflater;
    protected Context mContext;

    public MyBaseAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    /**
     * 设置所有数据
     * @param myList
     */
    public void setMyList(ArrayList<T> myList){
        this.myList = myList;
    }

    /**
     * 获取所有数据
     * @return
     */
    public ArrayList<T> getMyList() {
        return myList;
    }
    /**
     * 添加单条数据
     */
    public void insertNewsData(T t){
        myList.add(t);
    }

    /**s
     * 添加多条数据
     */
    public void insertNewsList(ArrayList<T> list){
        myList.addAll(list);
    }

    /**
     * 刷新适配器
     */
    public void update(){
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(myList == null)
            return 0;
        return myList.size();
    }
    public void setOnTop(T t){
        myList.add(0, t);
    }
    @Override
    public T getItem(int position) {
        if(myList == null && myList.size() == 0)
            return null;
        if(position > myList.size())
            return null;
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getMyView(position,convertView,parent);
    }

    public abstract View getMyView(int position, View convertView, ViewGroup parent);
}
