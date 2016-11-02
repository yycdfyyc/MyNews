package com.yuyunchao.asus.mynews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuyunchao.asus.mynews.Entry.UserEntity;
import com.yuyunchao.asus.mynews.Entry.UserLoginLogEntity;
import com.yuyunchao.asus.mynews.MyApplication;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.adapter.UserLoginLogAdapter;
import com.yuyunchao.asus.mynews.base.BaseActivity;
import com.yuyunchao.asus.mynews.biz.MyUserManager;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class UserActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_user_name,tv_user_exit,tv_user_comment_count,tv_user_jifen;
    private ImageView iv_user_icon,iv_user_back;
    private ListView lv_user_loginLog_data;
    private Button btn_pp_pic,btn_pp_camare,btn_pp_cancel;
    private MyUserManager manager;
    private SharedPreferences preferences;
    private ArrayList<UserLoginLogEntity> entities = new ArrayList<>();
    private UserLoginLogAdapter adapter;
    private PopupWindow popupWindow;
    private String user_icon;
    private String uid;
    private UserEntity userEntity;

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    //头像的文件
    private File mUserIcon;

    @Override
    protected int setContent() {
        return R.layout.activity_user;
    }

    @Override
    protected void initUI() {
        userEntity = new UserEntity();
        tv_user_comment_count = (TextView) findViewById(R.id.tv_user_comment_count);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_exit = (TextView) findViewById(R.id.tv_user_exit);
        tv_user_jifen = (TextView) findViewById(R.id.tv_user_jifen);
        iv_user_icon = (ImageView) findViewById(R.id.iv_user_icon);
        iv_user_back = (ImageView) findViewById(R.id.iv_user_back);
        lv_user_loginLog_data = (ListView) findViewById(R.id.lv_user_loginLog_data);
        adapter = new UserLoginLogAdapter(this);
        preferences = getSharedPreferences("mylogin", MODE_PRIVATE);
        initpopuWindow();
        initUserInfo();
    }

    @Override
    protected void initListener() {
        iv_user_icon.setOnClickListener(this);
        tv_user_exit.setOnClickListener(this);
        iv_user_back.setOnClickListener(this);
    }

    private void initUserInfo() {
        //http://118.244.212.82:9092/newsClient/user_home?ver=1&imei=101010101010101&token=6f94adf1fc617dd1d76195618658d256
        manager = new MyUserManager(this);
        String token = preferences.getString("user_token", "");
        if (!token.equals("")) {
            manager.MyUserStringRequest(
                    ServiceConstant.SERVICE_NAME + "user_home?ver=1&imei=101010101010101&token=" + token,
                    listener,errorListener);
        }else {
            showToast("出现错误");
        }
    }

    /**
     * 获取用户信息成功接口
     */
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                if (message.equals("OK")) {
                    JSONObject object = jsonObject.getJSONObject("data");
                    uid = object.getString("uid");
                    userEntity.setUid(uid);
                    user_icon = object.getString("portrait");
                    userEntity.setIconUrl(user_icon);
                    int comnum = object.getInt("comnum");
                    userEntity.setComNum(comnum);
                    int integration = object.getInt("integration");
                    userEntity.setIntegration(integration);
                    JSONArray array = object.getJSONArray("loginlog");
                    for (int i = 0; i < array.length(); i++) {
                        UserLoginLogEntity entity = new UserLoginLogEntity();
                        JSONObject object1 = array.getJSONObject(i);
                        String time = object1.getString("time");
                        String adress = object1.getString("address");
                        int device = object1.getInt("device");
                        String devicestr = "";
                        switch (device) {
                            case 0:
                                devicestr = "手机";
                                break;
                            case 1:
                                devicestr = "PC";
                                break;
                        }
                        entity.setTime(time);
                        entity.setAddress(adress);
                        entity.setDevice(devicestr);
                        entities.add(entity);
                    }
                    userEntity.setEntities(entities);
                    adapter.setMyList(entities);
                    lv_user_loginLog_data.setAdapter(adapter);
                    tv_user_name.setText(uid);
                    tv_user_comment_count.setText("用户评论数:"+comnum);
                    tv_user_jifen.setText("用户积分票数:"+integration);
                    Bitmap b = manager.getHeadBitMap(userEntity, bitmapListener, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
                    if (b != null) {
                        Log.i("yyc", "缓存head");
                        iv_user_icon.setImageBitmap(b);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    /**
     * 获取用户头像成功接口
     */
    private Response.Listener<Bitmap> bitmapListener = new Response.Listener<Bitmap>() {
        @Override
        public void onResponse(Bitmap bitmap) {
            Log.i("yyc", "网络head");
            iv_user_icon.setImageBitmap(bitmap);
            //存放到缓存文件中
            File file = new File(getCacheDir(),"userImage");
            if (!file.exists()){
                file.mkdirs();
            }
//            String userIconName = user_icon.substring(user_icon.lastIndexOf("/") + 1);
            String userIconName = uid + ".jpg";
            File icon = new File(file, userIconName);
            try {
                OutputStream outputStream = new FileOutputStream(icon);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    /**
     * 获取用户信息失败接口
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            showToast("获取用户信息失败");
        }
    };


    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_back:
                finish();
                break;
            case R.id.tv_user_exit:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("login", false);
                MyApplication myApplication = (MyApplication) getApplication();
                myApplication.isLogin = false;
                myApplication.userName = "";
                myApplication.token = "";
                finish();
                editor.commit();
                break;
            case R.id.iv_user_icon:
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else {
                    popupWindow.showAtLocation(findViewById(R.id.rl_popup), Gravity.BOTTOM, 0, 0);
//                    popupWindow.showAsDropDown(v);
                }
                break;
            case R.id.btn_cancel:
                popupWindow.dismiss();
                break;
            case R.id.btn_from_pic:
                //相册上传
                choseHeadImageFromGallery();
                break;
            case R.id.btn_from_cam:
                //拍照上传
                choseHeadImageFromCameraCapture();
                break;
        }
    }

    /**
     * 加载popupWindow
     */
    private void initpopuWindow(){
        popupWindow = new PopupWindow();
        View view = getLayoutInflater().inflate(R.layout.layout_popupwin_user_icon, null);
        btn_pp_camare = (Button) view.findViewById(R.id.btn_from_cam);
        btn_pp_pic = (Button) view.findViewById(R.id.btn_from_pic);
        btn_pp_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_pp_camare.setOnClickListener(this);
        btn_pp_cancel.setOnClickListener(this);
        btn_pp_pic.setOnClickListener(this);
        popupWindow.setContentView(view);
        popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //PopupWindow有时需要设置背景，其他的点击等等才会有效果
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        createFile();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        createFile();
        //存储照片文件
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(mUserIcon));
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(data.getData());
                break;

            case CODE_CAMERA_REQUEST:
                    cropRawPhoto(Uri.fromFile(mUserIcon));
                break;

            case CODE_RESULT_REQUEST:
                if (data != null) {
                    setImageToHeadView(data);
                    String token = ((MyApplication) getApplication()).token;
                    File mIcon = new File(getCacheDir() + File.separator + "userImage" + File.separator + uid + ".jpg");
                    manager.upHeadIcon(mIcon,token,headListener,headErrorListener);
                }
                break;
        }

    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }
    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //将裁剪后的图片存放到文件中
            File mIcon = new File(getCacheDir() + File.separator + "userImage" + File.separator + uid + ".jpg");
            try {
                OutputStream out = new FileOutputStream(mIcon);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            iv_user_icon.setImageBitmap(photo);
        }
    }

    private Response.Listener<String> headListener =new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                if (message.equals("OK")) {
                    showToast("上传头像成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    private Response.ErrorListener headErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    };
    private void createFile(){
        File mFileCache = getCacheDir();
        File mUserHead = new File(mFileCache, "user_head");
        mUserIcon = new File(mUserHead, IMAGE_FILE_NAME);
        if (!mUserHead.exists()){
            mUserHead.mkdirs();
        }
        if (!mUserIcon.exists()) {
            try {
                mUserIcon.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
