package com.yuyunchao.asus.mynews.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.database.DatabaseUtilsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyunchao.asus.mynews.R;


/**
 * activity的基类
 * Created by asus on 2016/10/18.
 */
public abstract class BaseActivity extends FragmentActivity {
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setContent());
        initUI();
        initListener();
    }

    /**
     * activity的布局
     * @return
     */
    protected abstract int setContent();

    /**
     * activity的控件
     */
    protected abstract void initUI();

    /**
     * 控件的监听
     */
    protected abstract void initListener();
    public void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载数据时候显示的dialog
     * @param msg 显示的文字
     * @param cancelable 可以不可以用"返回键"取消
     */
    public void showLoadingDialog(String msg,boolean cancelable) {
        View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
        ImageView im = (ImageView) v.findViewById(R.id.iv_dialogloading_img);
        TextView tv = (TextView) v.findViewById(R.id.tv_dialogloading_msg);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.iv_rotate);
        im.setAnimation(animation);
        if(msg!=null){
            tv.setText(msg);
        }
        dialog = new Dialog(this,R.style.loading_dialog);
        dialog.setCancelable(cancelable);
        dialog.setContentView(layout);
        dialog.show();
    }

    public void cancelDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

}
