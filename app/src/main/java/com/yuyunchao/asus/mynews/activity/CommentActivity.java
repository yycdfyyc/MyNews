package com.yuyunchao.asus.mynews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyunchao.asus.mynews.Entry.CommentListEntity;
import com.yuyunchao.asus.mynews.JSONManger.MyJsonManger;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.adapter.CommentAdapter;
import com.yuyunchao.asus.mynews.base.BaseActivity;
import com.yuyunchao.asus.mynews.biz.MyNewsManager;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.lib.xListView.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener{
    private ImageView iv_back,iv_send;
    private MyJsonManger myJsonManger;
    private EditText et_comment;
    private XListView xlv_comment_data;
    private CommentAdapter adapter;
    private ArrayList<CommentListEntity> list = new ArrayList<>();
    private int nid;
    private int cid;
    private String user;
    private String ctx;
    @Override
    protected int setContent() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initUI() {
        Intent intent = getIntent();
        nid = intent.getIntExtra("nid", 0);
        xlv_comment_data = (XListView) findViewById(R.id.xlv_comment_data);
        iv_back = (ImageView) findViewById(R.id.iv_comment_back);
        iv_send = (ImageView) findViewById(R.id.iv_send_comment);
        et_comment = (EditText) findViewById(R.id.et_comment);
        xlv_comment_data.setPullRefreshEnable(true);
        xlv_comment_data.setPullLoadEnable(true);
        xlv_comment_data.setXListViewListener(this);
        showLoadingDialog("正在加载评论",true);
        adapter = new CommentAdapter(this,xlv_comment_data);
        initComment(nid,1,1);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        iv_send.setOnClickListener(this);

    }

    /**
     * 加载评论
     */
    private void initComment(int nid,int mode,int cid) {
        MyNewsManager myNewsManager = new MyNewsManager(this);
        myNewsManager.MyStringRequest(
                ServiceConstant.SERVICE_NAME+"cmt_list?ver=1&nid="+nid+"&type=1&stamp=11111111&cid="+cid+"&dir="+mode+"&cnt=20",
                listener,errorListener);
    }
    /**
     * 加载刷新评论
     */
//    private void initCommentF(int nid,int mode,int cid) {
//        //cmt_list?ver=1&nid=22&type=1&stamp=11111111&cid=1&dir=1&cnt=20
//        MyNewsManager myNewsManager = new MyNewsManager(this);
//        myNewsManager.MyStringRequest(
//                ServiceConstant.SERVICE_NAME+"cmt_list?ver=1&nid="+nid+"&type=1&stamp=11111111&cid="+cid+"&dir="+mode+"&cnt=20",
//                listenerF,errorListener);
//    }
    /**
     * 加载更多评论
     */
    private void initMoreComment(int nid,int mode,int cid){
        MyNewsManager myNewsManager = new MyNewsManager(this);
        myNewsManager.MyStringRequest(
                ServiceConstant.SERVICE_NAME+"cmt_list?ver=1&nid="+nid+"&type=1&stamp=11111111&cid="+cid+"&dir="+mode+"&cnt=20",
                listenerLodeMore,errorListener);
    }

    /**
     * 加载评论的接口
     */
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            myJsonManger = new MyJsonManger();
            list = myJsonManger.getCommentJson(s);
            if (list != null && list.size() > 0) {
                adapter.setMyList(list);
                xlv_comment_data.setAdapter(adapter);
            }
            cancelDialog();
        }
    };
    /**
     * 下拉刷新成功接口
     */
//    private Response.Listener<String> listenerF = new Response.Listener<String>() {
//        @Override
//        public void onResponse(String s) {
//            myJsonManger = new MyJsonManger();
//            ArrayList<CommentListEntity> mlist = myJsonManger.getCommentJson(s);
//            if (mlist != null && mlist.size() > 0) {
//                list.addAll(0,mlist);
//                adapter.setMyList(list);
//                adapter.update();
//            }
//            cancelDialog();
//        }
//    };
    /**
     * 上拉加载更多成功接口
     */
    private Response.Listener<String> listenerLodeMore = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            myJsonManger = new MyJsonManger();
            ArrayList<CommentListEntity> listmore = myJsonManger.getCommentJson(s);
            if (listmore != null && listmore.size() > 0) {
                list.addAll(listmore);
                adapter.setMyList(list);
                adapter.update();
            }
            cancelDialog();
        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            cancelDialog();
            showToast("加载评论失败");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comment_back:
                finish();
                break;
            case R.id.iv_send_comment:
                showLoadingDialog("发表评论中。。。",false);
                SharedPreferences shard = getSharedPreferences("mylogin", MODE_PRIVATE);
                boolean isLogin = shard.getBoolean("login", false);
                if (isLogin) {
                    ctx = et_comment.getText().toString();
                    String token = shard.getString("user_token", "");
                    user = shard.getString("user_name", "");
                    if (ctx != null && ctx.length() > 0 && (!token.equals(""))) {
                        MyNewsManager myNewsManager = new MyNewsManager(this);
                            String url =ServiceConstant.SERVICE_NAME + "cmt_commit?ver=1&nid=" + nid +
                                    "&token="+token+"&imei=111111111111111&ctx="+ctx;
                            Log.i("yyc",nid+"nid:"+token);
                            myNewsManager.MyStringRequest(url,comListener,comErrorListener);
                    } else {
                        showToast("评论不能为空");
                        cancelDialog();
                    }
                } else {
                    showToast("请先登录");
                    et_comment.setText("");
                    cancelDialog();
                }
                break;
        }
    }
    private Response.Listener<String> comListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                if (message !=null && message.equals("OK")) {
                    CommentListEntity en = new CommentListEntity();
                    en.setCid(0);
                    en.setStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    en.setPortrait(null);
                    en.setComContent(ctx);
                    en.setUid(user);
                    adapter.setOnTop(en);
                    adapter.notifyDataSetChanged();
                    xlv_comment_data.setSelection(0);
                    showToast("发表成功");
                    et_comment.setText("");
                    cancelDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private Response.ErrorListener comErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            showToast("发表失败");
            cancelDialog();
        }
    };

    @Override
    public void onRefresh() {
        xlv_comment_data.setRefreshTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        initComment(nid,1,1);
        xlv_comment_data.stopRefresh();
        xlv_comment_data.stopLoadMore();
    }

    @Override
    public void onLoadMore() {
        ArrayList<CommentListEntity> nowList = adapter.getMyList();
        cid = nowList.get(nowList.size()-1).getCid();
        initMoreComment(nid, 2, cid);
        xlv_comment_data.stopRefresh();
        xlv_comment_data.stopLoadMore();
    }
    private void putcom(int nid,String token,String ctx){
        try {
            URL url = new URL(ServiceConstant.SERVICE_NAME + "cmt_commit?ver=1&nid=" + nid +
                                    "&token="+token+"&imei=111111111111111&ctx="+ctx);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
