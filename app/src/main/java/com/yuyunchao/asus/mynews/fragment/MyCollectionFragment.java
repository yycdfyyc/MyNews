package com.yuyunchao.asus.mynews.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.SQLite.MyDBHelper;
import com.yuyunchao.asus.mynews.activity.NewsWebViewActivity;
import com.yuyunchao.asus.mynews.adapter.XListViewAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCollectionFragment extends Fragment implements NewsWebViewActivity.UpData{
    View view;
    XListViewAdapter adapter;
    ListView lv_collection_data;
    ArrayList<NewsListEntity> entities = new ArrayList<>();
    MyDBHelper helper;
    public MyCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_collection, container, false);
        initUI();
        return view;
    }
    private void initUI(){
        lv_collection_data = (ListView) view.findViewById(R.id.lv_collection_data);
        adapter = new XListViewAdapter(getActivity());
        helper = new MyDBHelper(getActivity());
        entities = helper.selectFromNewsListByCollection();
        adapter.setMyList(entities);
        lv_collection_data.setAdapter(adapter);
        lv_collection_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NewsWebViewActivity.class);
                NewsListEntity entity = adapter.getMyList().get(position);
                intent.putExtra("newsEntity",entity);
                startActivity(intent);
            }
        });
        NewsWebViewActivity.up(this);
    }

    @Override
    public void updataAdapter(Boolean b) {
        if (b){
            entities.clear();
            entities = helper.selectFromNewsListByCollection();
            adapter.setMyList(entities);
            adapter.notifyDataSetChanged();
            Log.i("yyc", "sx");
        }
    }
}
