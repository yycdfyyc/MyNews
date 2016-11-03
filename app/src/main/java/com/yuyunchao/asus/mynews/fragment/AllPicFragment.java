package com.yuyunchao.asus.mynews.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.adapter.MyRecycleAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllPicFragment extends Fragment {
    private View view;
    private RecyclerView rv_main;
    private MyRecycleAdapter adapter;
    private ArrayList<Bitmap> list = new ArrayList<>();
    public AllPicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_pic, container, false);
        initRecyclerView();
        return view;
    }

    public void initRecyclerView(){
        rv_main = (RecyclerView) view.findViewById(R.id.rv_all_picture);
        //设置布局管理器
//        //线性布局类似listView
//        rv_main.setLayoutManager(new LinearLayoutManager(this));
        //网格布局
//        rv_main.setLayoutManager(new GridLayoutManager(getActivity(),3));
//        //瀑布式布局
        rv_main.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        //设置分隔线
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        rv_main.addItemDecoration(decoration);
        //设置adapter
        initList(getActivity().getCacheDir());
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        adapter = new MyRecycleAdapter(getActivity(), list,mScreenWidth);
        rv_main.setAdapter(adapter);
    }
    private void initList(File file){
        File[] f = file.listFiles();
        for (File ff : f) {
            if (ff.isDirectory()) {
                if (ff.getName().equals("userImage"))
                initList(ff);
            }else if (ff.isFile()){
                String name = ff.getName();
                if (name.indexOf(".") == -1) {
                } else {
                    if (name.contains(".jpg")||name.contains(".png"))
                        list.add(BitmapFactory.decodeFile(ff.getAbsolutePath()));
                }
            }
        }
    }
    class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space=space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left=space;
            outRect.right=space;
            outRect.bottom=space;
            if(parent.getChildAdapterPosition(view)==0){
                outRect.top=space;
            }
        }
    }
}
