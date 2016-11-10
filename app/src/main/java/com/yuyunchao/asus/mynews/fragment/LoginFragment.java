package com.yuyunchao.asus.mynews.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yuyunchao.asus.mynews.MyApplication;
import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener,TextWatcher{
    private EditText et_user,et_pwd;
    private Button btn_login,btn_register,btn_wjmima;
    private String user,pwd;
    private MainActivity mainActivity;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        et_user = (EditText) view.findViewById(R.id.et_login_user_name);
        et_pwd = (EditText) view.findViewById(R.id.et_login_pw);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        initListener();

        return view;
    }
    private void initListener(){
        et_pwd.addTextChangedListener(this);
        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String user = s.toString();
                String pwd = et_pwd.getText().toString();
                if (user.length() > 0 && pwd.length() > 0) {
                    btn_login.setBackground(getResources().getDrawable(R.drawable.normalbutton_normal));
                    btn_login.setEnabled(true);
                }
            }
        });
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (ServiceConstant.isNetworkAvailable(getActivity())) {
                    user = et_user.getText().toString();
                    pwd = et_pwd.getText().toString();
                    MyAsncyTask at = new MyAsncyTask();
                    at.execute(user, pwd);
                } else {
                    Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_register:
                if (ServiceConstant.isNetworkAvailable(getActivity())) {
                    ((MainActivity)getActivity()).replaceFragment(new RegisterFragment());
                    ((MainActivity)getActivity()).setTitle("注册");
                } else {
                    Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.length()>0){
            String user = et_user.getText().toString();
            if (user.length()>0){
                btn_login.setBackground(getResources().getDrawable(R.drawable.normalbutton_normal));
                btn_login.setEnabled(true);
            }
        }else {
            btn_login.setBackground(getResources().getDrawable(R.drawable.normalbutton_pressed));
            btn_login.setEnabled(false);
        }
    }

    /**
     * 建立连接
     * @param user
     * @param pwd
     * @return
     */
    private String parsesJson(String user,String pwd){
        //http://118.244.212.82:9092/newsClient/
        //user_login?ver=版本号&uid=用户名&pwd=密码&device=0
        try {
            StringBuffer s = new StringBuffer();
            //1.建立连接
            URL url = new URL(
                    ServiceConstant.SERVICE_NAME + "user_login");
            String content ="ver=1"+"&uid="+user+"&pwd="+pwd+"&device=0";
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //2.设置请求头
            //请求模式
            connection.setRequestMethod("POST");
            //请求头格式
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",content.length()+"");
            //允许HttpURLConnection可以使用输出流
            connection.setDoOutput(true);
            //获取输出流
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.close();

            int code = connection.getResponseCode();
            if (code==200){
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream buffer = new BufferedInputStream(inputStream);
                byte[] bytes = new byte[1024];
                int count = 0;
                while ((count = buffer.read(bytes))!=-1){
                    s.append(new String(bytes,0,count));
                }
                inputStream.close();
                return s.toString();
            }else if (code == 404){
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 登录异步处理类
     */
    class MyAsncyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return parsesJson(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject object = new JSONObject(s);
                    String message = object.getString("message");
                    if (message.equals("OK")) {
                        JSONObject object1 = object.getJSONObject("data");
                        String token = object1.getString("token");
                        mainActivity = ((MainActivity) getActivity());
                        mainActivity.showToast("登录成功");
                        mainActivity.tv_login.setText(user);
                        mainActivity.tv_main_title.setText("资讯");
                        mainActivity.slidingMenu.showSecondaryMenu();
                        mainActivity.replaceFragment(new MainFragment());
                        mainActivity.ll_shard.setVisibility(View.GONE);
                        SharedPreferences preferences = getActivity().getSharedPreferences("mylogin", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("login", true);
                        mainActivity.isLogin = true;
                        editor.putString("user_name", user);
                        editor.putString("user_token", token);
                        editor.putString("user_pwd", pwd);
                        editor.commit();
                        MyApplication application = (MyApplication) getActivity().getApplication();
                        application.isLogin = true;
                        application.userName = user;
                        application.token = token;
                        application.userPwd = pwd;
                        mainActivity.autoLogin();
                    } else {
                        ((MainActivity) getActivity()).showToast(message);
                        et_user.setText("");
                        et_pwd.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
