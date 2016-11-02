package com.yuyunchao.asus.mynews.fragment;


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

import com.yuyunchao.asus.mynews.R;
import com.yuyunchao.asus.mynews.constant.ServiceConstant;
import com.yuyunchao.asus.mynews.activity.MainActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements TextWatcher{
    private View view;
    private EditText et_email,et_user,et_pwd,et_qrpwd;
    private Button btn_register;
    private String email,user,pwd,qrpwd;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_regiest, container, false);
        initUI();
        initListener();
        return view;
    }
    private  void initUI(){
        et_email = (EditText) view.findViewById(R.id.et_register_user_email);
        et_user = (EditText) view.findViewById(R.id.et_register_user_name);
        et_pwd = (EditText) view.findViewById(R.id.et_register_pw);
        et_qrpwd = (EditText) view.findViewById(R.id.et_register_qr_pw);
        btn_register = (Button) view.findViewById(R.id.btn_register_fragment_register);
    }

    private void initListener(){
        et_qrpwd.addTextChangedListener(this);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = et_email.getText().toString();
                user = et_user.getText().toString();
                pwd = et_pwd.getText().toString();
                qrpwd = et_qrpwd.getText().toString();
                MainActivity mainActivity = (MainActivity)getActivity();
                if(ServiceConstant.verifyEmail(email)){
                    if(ServiceConstant.verifyUserName(user)){
                        if (ServiceConstant.verifyPwd(pwd)){
                            RegisterTask task = new RegisterTask();
                            task.execute(user, pwd, email);
                        }else {
                            mainActivity.showToast("密码不正确，密码为6至24位字符串，只能是数字、大小写英文字母或下划线");
                            et_pwd.setText("");
                            et_qrpwd.setText("");
                        }
                    }else {
                        mainActivity.showToast("用户名不正确，用户名为6至24位字符串，只能是数字、大小写英文字母或下划线");
                        et_user.setText("");
                        et_pwd.setText("");
                        et_qrpwd.setText("");
                    }
                }else {
                    mainActivity.showToast("邮箱格式不正确");
                    et_email.setText("");
                    et_pwd.setText("");
                    et_qrpwd.setText("");
                }
            }
        });


    }
    /**
     * 建立连接
     * @param user
     * @param pwd
     * @param email
     * @return
     */
    private String parsesJson(String user,String pwd,String email){
        //http://118.244.212.82:9092/newsClient/
        //user_register?ver=版本号&uid=用户名&email=邮箱&pwd=登陆密码
        try {
            StringBuffer s = new StringBuffer();
            //1.建立连接
            URL url = new URL(
                    ServiceConstant.SERVICE_NAME + "user_register");
            String content ="ver=1"+"&uid="+user+"&email="+email+"&pwd="+pwd;
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        email = et_email.getText().toString();
        user = et_user.getText().toString();
        pwd = et_pwd.getText().toString();
        qrpwd = et_qrpwd.getText().toString();
        if(email.length()>0&&user.length()>0&&pwd.length()>0&&qrpwd.length()>0){
            if (pwd.equals(qrpwd)){
                btn_register.setEnabled(true);
                btn_register.setBackground(getResources().getDrawable(R.drawable.my_button));
            }else {
                btn_register.setEnabled(false);
                btn_register.setBackground(getResources().getDrawable(R.drawable.normalbutton_pressed));
            }
        }else {
            btn_register.setEnabled(false);
            btn_register.setBackground(getResources().getDrawable(R.drawable.normalbutton_pressed));
        }

    }

    /**
     * 注册异步类
     */
    class RegisterTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            return parsesJson(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String message = jsonObject.getString("message");
                if (message.equals("OK")){
                    JSONObject object = jsonObject.getJSONObject("data");
                    int result = object.getInt("result");
                    String explain = object.getString("explain");
                    MainActivity mainActivity = ((MainActivity)getActivity());
                    if (result == 0){
                        mainActivity.replaceFragment(new HorizontalListViewFragment());
                        mainActivity.setTitle("资讯");
                    }else {
                        et_email.setText("");
                        et_user.setText("");
                        et_pwd.setText("");
                        et_qrpwd.setText("");
                    }
                    Toast.makeText(mainActivity,explain,Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
