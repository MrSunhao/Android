package com.company.sun.intelligentfan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sun on 2017/5/18.
 */

public class ResetPwdActivity extends AppCompatActivity {
    private ImageButton go_back;
    private EditText oldpwdEdit;
    private EditText newpwdEdit;
    private EditText confirmpwdEdit;
    private MessageDigest messageDigest;
    private Button resetpwd_ok;

    private ViewPager vpager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        initConrols();

        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                返回上一个页面
                ResetPwdActivity.this.finish();
            }
        });

        resetpwd_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwd = MD5(oldpwdEdit.getText().toString());
                if (oldpwd == null || oldpwd.equals("")) {
                    Toast.makeText(ResetPwdActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String newpwd = MD5(newpwdEdit.getText().toString());
                if (newpwd == null || newpwd.equals("")) {
                    Toast.makeText(ResetPwdActivity.this, "新密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String confirmpwd = MD5(confirmpwdEdit.getText().toString());
                if (confirmpwd == null || confirmpwd.equals("")) {
                    Toast.makeText(ResetPwdActivity.this, "重复密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("rememberUser", Context.MODE_PRIVATE);
                final String pwd = sharedPreferences.getString("password", null);
                final String username = sharedPreferences.getString("username",null);
                if (!oldpwd.equals(pwd)) {
                    System.out.println("pwd--------------------->"+pwd);
                    System.out.println("oldpwd------------------>"+oldpwd);
                    Toast.makeText(ResetPwdActivity.this, "原始密码错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpwd.equals(confirmpwd)) {
                    Toast.makeText(ResetPwdActivity.this, "密码不一致！", Toast.LENGTH_SHORT).show();
                }

                String httpurl = "http://123.207.160.127:8888/web/ChangePwd";
                //发起修改密码请求  得到TRUE 或者 false
                new AsyncTask<String,Void,String>(){
                    @Override
                    protected void onPostExecute(String s) {
                        System.out.println("sss------------>"+s);
                        super.onPostExecute(s);
                        if(s == null){
                            Toast.makeText(ResetPwdActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
                        }
                        if("true".equals(s)){
                            Toast.makeText(ResetPwdActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ResetPwdActivity.this.finish();
                        }
                        if("false".equals(s)){
                            Toast.makeText(ResetPwdActivity.this, "更新失败！请重试！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {

                            String reqContent = "name="+username+"&pwd="+newpwd; //请求的内容
                            URL url = new URL(httpurl);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                            httpURLConnection.setRequestProperty("charset", "utf-8");

                            httpURLConnection.connect();

                            OutputStream os = httpURLConnection.getOutputStream();
                            PrintWriter printWriter = new PrintWriter(os);  //得到打印流
                            printWriter.print(reqContent);
                            printWriter.flush();
                            String str = "";

                            int statuscode = httpURLConnection.getResponseCode();
                            if (statuscode == 200) {
                                str = readStr(httpURLConnection.getInputStream());
                            }
                            if (statuscode == 404) {
                                return null;
                            }
                            httpURLConnection.disconnect();
                            return str;

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(httpurl);

            }
        });

    }

    private void initConrols() {
        go_back = (ImageButton) findViewById(R.id.go_back);
        oldpwdEdit = (EditText) findViewById(R.id.old_pwd);
        newpwdEdit = (EditText) findViewById(R.id.new_pwd);
        confirmpwdEdit = (EditText) findViewById(R.id.confirm_pwd);
        resetpwd_ok = (Button) findViewById(R.id.resetpwd_ok);
        vpager = (ViewPager) findViewById(R.id.vpager);
    }

    private String MD5(String inputstr) {
        String output = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputbyte = inputstr.getBytes();
            messageDigest.update(inputbyte);
            byte[] md = messageDigest.digest();
            BigInteger bigInteger = new BigInteger(md);
            output = bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String readStr(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        is.close();
        return sb.toString();
    }
}
