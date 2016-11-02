package com.yuyunchao.asus.mynews.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.Entry.NewsTypeEntity;
import com.yuyunchao.asus.mynews.HorizontalListView.HorizontalListView;
import com.yuyunchao.asus.mynews.JSONManger.MyJsonManger;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.SQLite.MyDBHelper;
import com.yuyunchao.asus.mynews.activity.MainActivity;
import com.yuyunchao.asus.mynews.activity.NewsWebViewActivity;
import com.yuyunchao.asus.mynews.adapter.XListViewAdapter;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.adapter.HorizontalListViewAdapter;
import com.yuyunchao.asus.mynews.lib.xListView.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorizontalListViewFragment extends Fragment implements AdapterView.OnItemClickListener{
    MainActivity mainActivity = (MainActivity) getActivity();
    private int subid = 1;
    //下拉刷新
    private int Refresh = 1;
    //上拉加载
    private int Load = 2;

    private int mode;

    XListView xlv_data;

    HorizontalListView hlv_data;
    View view;
    //接受新闻类型数据的list，传给adapter的list
    ArrayList<NewsTypeEntity> list;
    //接受新闻list数据的list
    ArrayList<NewsListEntity> newsListList;
    //横向listView的adapter
    HorizontalListViewAdapter horizontalAdapter;
    //XListView的adapter
    XListViewAdapter xListViewAdapter;
    MyDBHelper myDBHelper;
    //xListView的下拉与上拉监听事件
    private XListView.IXListViewListener xlistener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            loadNewsListData();
            xlv_data.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            xlv_data.stopRefresh();
            xlv_data.stopLoadMore();
        }

        @Override
        public void onLoadMore() {
            loadMoreNewsListData();
            xlv_data.stopLoadMore();
            xlv_data.stopRefresh();
        }
    };

    public HorizontalListViewFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_horizontal_list_view, container, false);
        hlv_data = (HorizontalListView) view.findViewById(R.id.hlv_someData);
        xlv_data = (XListView) view.findViewById(R.id.xlv_data);
        horizontalAdapter = new HorizontalListViewAdapter(getActivity());
        xListViewAdapter = new XListViewAdapter(getActivity(),xlv_data);
        mainActivity = (MainActivity) getActivity();
        xlv_data.setPullLoadEnable(true);
        xlv_data.setPullRefreshEnable(true);
        xlv_data.setXListViewListener(xlistener);
        xlv_data.setOnItemClickListener(this);
        newsListList = new ArrayList<>();
        hlv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                horizontalAdapter.setItemPosition(position);
                if (subid != horizontalAdapter.getMyList().get(position).getSubid()){
                    subid = horizontalAdapter.getMyList().get(position).getSubid();
                    xListViewAdapter.getMyList().clear();
                    loadNewsListData();
                }
                horizontalAdapter.notifyDataSetChanged();
            }
        });
        loadNewsTypeData();
        loadNewsListData();
        return view;
    }

    /**
     * 加载新闻类型数据
     */
    private void loadNewsTypeData(){
        if (!ServiceConstant.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
        }
        myDBHelper = new MyDBHelper(getActivity());
        list = new ArrayList<>();
        list = myDBHelper.select();
        if (list != null&& list.size()>0){
            horizontalAdapter.setMyList(list);
            hlv_data.setAdapter(horizontalAdapter);
            Log.i("yyc", "type数据库");
        }else {
            initTypeData();
            Log.i("yyc", "type网络");
        }
    }
    /**
     * 加载新闻list数据
     */
    private void loadNewsListData(){

        mainActivity.showLoadingDialog("正在加载...",true);
        myDBHelper = new MyDBHelper(getActivity());
        newsListList = myDBHelper.selectFromNewsList(subid);
        if (newsListList != null&& newsListList.size()>0){
            xListViewAdapter.setMyList(newsListList);
            xlv_data.setAdapter(xListViewAdapter);
            Log.i("yyc", "数据库");
            mainActivity.cancelDialog();
        }else {
            if (!ServiceConstant.isNetworkAvailable(getActivity())) {
                Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
            }else {
                initListData();
                Log.i("yyc", "网络");
            }

        }

    }

    /**
     * 加载更多新闻list数据
     */
    private void loadMoreNewsListData(){
        myDBHelper = new MyDBHelper(getActivity());
        mainActivity.showLoadingDialog("正在加载...",true);
        newsListList = xListViewAdapter.getMyList();
        if (!ServiceConstant.isNetworkAvailable(getActivity())){
            xListViewAdapter.setMyList(newsListList);
            xListViewAdapter.update();
            Log.i("yyc", "加载更多数据库");
        }else {
            initListMoreData();
            Log.i("yyc", "加载更多网络");
        }
        mainActivity.cancelDialog();
    }

    /**
     * 网络添加新闻类型数据
     */
    private void initTypeData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        MyListener listener = new MyListener();
        StringRequest stringRequest = new StringRequest(ServiceConstant.SERVICE_NAME + "news_sort?ver=1&imei=111111111111111",
                listener, listener);
        requestQueue.add(stringRequest);
    }
    /**
     * 网络添加最新新闻list数据
     */
    private void initListData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        mode = Refresh;
        StringRequest stringRequest = new StringRequest(
                ServiceConstant.SERVICE_NAME + "news_list?ver="+ServiceConstant.SERVICE_VERSION+"&subid="+subid
                        +"&dir="+mode+"&nid=1&stamp=20160911&cnt=20",
                listListener, listErrorListener);
        requestQueue.add(stringRequest);
    }
    /**
     * 网络添加更多新闻list数据
     */
    private void initListMoreData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        mode = Load;
        StringRequest stringRequest = new StringRequest(
                ServiceConstant.SERVICE_NAME + "news_list?ver="+ServiceConstant.SERVICE_VERSION+"&subid="+subid
                        +"&dir="+mode+"&nid="+newsListList.get(newsListList.size()-1).getNid()+"&stamp=20160911&cnt=20",
                listListener, listErrorListener);
        requestQueue.add(stringRequest);
    }


    //加载新闻list成功的接口
    private Response.Listener<String> listListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            MyJsonManger manger = new MyJsonManger();
            if (mode == Refresh){
                newsListList = manger.getNewsListJson(s,subid);
                xListViewAdapter.setMyList(newsListList);
                //插入数据库
                for (int i = 0;i<newsListList.size();i++){
                    myDBHelper.insertIntoNewsList(newsListList.get(i));
                }
                xlv_data.setAdapter(xListViewAdapter);
            }
            else if(mode == Load){
                ArrayList<NewsListEntity> l = manger.getNewsListJson(s,subid);
                if (l !=null && l.size()>0){
                    xListViewAdapter.insertNewsList(l);
                    // 写入数据库
                    for (int i = 0; i < l.size(); i++) {
                        myDBHelper.insertIntoNewsList(l.get(i));
                    }
                }
            }
            xListViewAdapter.update();
            mainActivity.cancelDialog();

        }
    };
    //加载新闻list失败的接口
    private Response.ErrorListener listErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };

    /**
     * xlv的item监听
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), NewsWebViewActivity.class);
        NewsListEntity entity = xListViewAdapter.getMyList().get(position-1);
        intent.putExtra("newsEntity",entity);
        startActivity(intent);
    }


    class MyListener implements Response.Listener<String>,Response.ErrorListener{
        @Override
        public void onResponse(String s) {
            MyJsonManger myJsonManger = new MyJsonManger();
            list = myJsonManger.getNewsSortJson(s);
            horizontalAdapter.setMyList(list);
            hlv_data.setAdapter(horizontalAdapter);
            //插入数据库
            for (int i = 0; i < list.size(); i++) {
                myDBHelper.insert(list.get(i));
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }



}
