package com.yuyunchao.asus.mynews.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.BoolRes;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyunchao.asus.mynews.Entry.NewsListEntity;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.SQLite.MyDBHelper;
import com.yuyunchao.asus.mynews.base.BaseActivity;
import com.yuyunchao.asus.mynews.biz.MyNewsManager;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsWebViewActivity extends BaseActivity implements View.OnClickListener{
    private ProgressBar progressBar;
    private WebView wv_web;
    private ImageView iv_back, iv_news_menu;
    private PopupWindow pp;
    private LinearLayout ll_sorry;
    private int isCollection;
    private Button btn_collect;
    private int nid;
    private TextView tv_cmt;
    @Override
    protected int setContent() {
        return R.layout.activity_news_web_view;
    }

    @Override
    protected void initUI() {
        wv_web = (WebView) findViewById(R.id.webView);
        iv_back = (ImageView) findViewById(R.id.iv_web_back);
        tv_cmt = (TextView) findViewById(R.id.tv_cmt);
        iv_news_menu = (ImageView) findViewById(R.id.iv_web_news_menu);
        iv_back.setOnClickListener(this);
        iv_news_menu.setOnClickListener(this);
        tv_cmt.setOnClickListener(this);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        ll_sorry = (LinearLayout) findViewById(R.id.ll_sorry);
        initPopupWindow();
        isNetwork();
    }

    @Override
    protected void onStart() {
        super.onStart();
        parseCmt(nid);
    }

    @Override
    protected void initListener() {

    }
    private void initPopupWindow(){
        pp = new PopupWindow();
        View view = getLayoutInflater().inflate(R.layout.layout_collect, null);
        btn_collect = (Button) view.findViewById(R.id.btn_collect);
        btn_collect.setOnClickListener(this);
        pp.setContentView(view);
        pp.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //PopupWindow有时需要设置背景，其他的点击等等才会有效果
        pp.setBackgroundDrawable(new BitmapDrawable());
        pp.setFocusable(true);
//        view.findViewById(R.id.collect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pp.dismiss();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_web_back:
                finish();
                break;
            case R.id.iv_web_news_menu:
                Log.i("yyc", "menu");
                if (pp != null && pp.isShowing()) {
                    pp.dismiss();
                }else {
                    if (isCollection == 2){
                        btn_collect.setText("取消收藏");
                    }else if (isCollection == 1){
                        btn_collect.setText("添加收藏");
                    }
//                    pp.showAsDropDown(iv_back);
                    pp.showAtLocation(findViewById(R.id.ll_collection), Gravity.BOTTOM,0,0 );
                    pp.setOutsideTouchable(true);
                    Log.i("yyc", "menujieshu");
                }
                break;
            case R.id.btn_collect:
                if (isCollection == 2){
                    isCollection = 1;
                    showToast("取消收藏成功");
                }else if (isCollection == 1){
                    isCollection =2;
                    showToast("添加收藏成功");
                }
                MyDBHelper helper = new MyDBHelper(this);
                helper.updataNewsCollection(nid);
                pp.dismiss();
                break;
            case R.id.tv_cmt:
                Log.i("yyc", "cmt");
                Intent intent = new Intent(this, CommentActivity.class);
                intent.putExtra("nid", nid);
                startActivity(intent);
                break;
        }
    }

    /**
     * 判断网络状态
     */
    private void isNetwork(){
        //有网
        if (ServiceConstant.isNetworkAvailable(this)) {
            ll_sorry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            wv_web.setVisibility(View.VISIBLE);
            tv_cmt.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            NewsListEntity entity = (NewsListEntity) intent.getSerializableExtra("newsEntity");
            String url = entity.getLink();
            isCollection = entity.getIscollection();
            nid = entity.getNid();
            //
            wv_web.loadUrl(url);
            wv_web.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            wv_web.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        if (View.GONE == progressBar.getVisibility()) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        progressBar.setProgress(newProgress);
                    }
                }

            });
        } else {
            tv_cmt.setVisibility(View.GONE);
            ll_sorry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            wv_web.setVisibility(View.GONE);
        }
    }

    private void parseCmt(int nid){
        MyNewsManager manager = new MyNewsManager(this);
        manager.MyStringRequest(
                ServiceConstant.SERVICE_NAME + "cmt_num?ver=1&nid=" + nid, listener, errorListener);
    }
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject object = new JSONObject(s);
                int count = object.getInt("data");
                if (count > 0) {
                    tv_cmt.setText("跟帖:" + count);
                } else {
                    tv_cmt.setText("未有人评论");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (pp != null && pp.isShowing()) {
                pp.dismiss();
                Log.i("yyc", "back pp");
            } else {
                if (updata != null){
                    Log.i("yyc", "updata");
                    updata.updataAdapter(true);
                }
                Log.i("yyc", "back activity");
                finish();
            }

        }
        return true;
    }
    /**
     * 刷新收藏的listViewAdapter的回调接口
     */
    private static UpData updata;
    public static void up(UpData updata){
        NewsWebViewActivity.updata = updata;
    }
    public interface UpData {
        void updataAdapter(Boolean b);
    }
}
