package com.yuyunchao.asus.mynews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.base.BaseActivity;

public class ShowActivity extends BaseActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ViewPager vp_show;
    View[] view = new View[3];
    ImageView iv_pager1,iv_pager2,iv_pager3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        sharedPreferences = getSharedPreferences("phone",MODE_PRIVATE);
        boolean isNotFirst = sharedPreferences.getBoolean("isNotFirst",false);
        //判断是不是非第一次登录，
        if (isNotFirst) {
            //非第一次登陆
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //第一次登陆
            editor = sharedPreferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("isNotFirst", true);
            editor.commit();
            initUI();
        }


    }

    @Override
    protected int setContent() {
        return R.layout.activity_show;
    }

    @Override
    protected void initUI() {

        vp_show = (ViewPager) findViewById(R.id.vp_show);
        view[0] = getLayoutInflater().inflate(R.layout.pager_frist, null);
        view[1] = getLayoutInflater().inflate(R.layout.pager_second, null);
        view[2] = getLayoutInflater().inflate(R.layout.pager_third, null);
        iv_pager1 = (ImageView) findViewById(R.id.iv_yuan1);
        iv_pager2 = (ImageView) findViewById(R.id.iv_yuan2);
        iv_pager3 = (ImageView) findViewById(R.id.iv_yuan3);

        vp_show.setAdapter(new MyViewPagerAdapter());
        vp_show.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        iv_pager1.setImageResource(R.drawable.userbg2);
                        iv_pager2.setImageResource(R.drawable.userbg);
                        iv_pager3.setImageResource(R.drawable.userbg);
                        break;
                    case 1:
                        iv_pager1.setImageResource(R.drawable.userbg);
                        iv_pager2.setImageResource(R.drawable.userbg2);
                        iv_pager3.setImageResource(R.drawable.userbg);
                        break;
                    case 2:
                        iv_pager1.setImageResource(R.drawable.userbg);
                        iv_pager2.setImageResource(R.drawable.userbg);
                        iv_pager3.setImageResource(R.drawable.userbg2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void initListener() {

    }
    class MyViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return view.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = view[position];
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(view[position]);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
    public void clickBtn(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
