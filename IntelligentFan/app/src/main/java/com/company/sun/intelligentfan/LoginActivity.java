package com.company.sun.intelligentfan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.company.sun.intelligentfan.service.CheckUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {


    private TextView login_title;
    private EditText usernameText;
    private EditText passwordText;
    private Button submit;
    private Button reset;
    private CheckBox rememberMe;

    private SharedPreferences shared;
    private SharedPreferences shared1;
    private MessageDigest messageDigest;
    private Boolean atuologin;
    private String password;
    private String username;

    ProgressDialog dialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initCtrols();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atuologin = rememberMe.isChecked();
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();
                if (username.equals("") || username == null) {
//                    提示用户为空的提示框
                    String usernameHint = "账号不能为空！";
                    getAlertDialog(usernameHint).show();
                } else if (password.equals("") || password == null) {
//                    提示密码为空的提示框
                    String passwordHint1 = "密码不能为空！";
                    getAlertDialog(passwordHint1).show();
                } else if (password.length() < 4) {
                    String passwordHint2 = "密码长度不能低于4位！";
                    getAlertDialog(passwordHint2).show();
                } else {
                    getProgressDialog(dialog);
                    password = MD5(password);

                    shared1 = getSharedPreferences("rememberUser",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = shared1.edit();
                    editor1.putString("username",username);
                    editor1.putString("password",password);
                    editor1.commit();
//                  请求路径
                    String httpurl = "http://123.207.160.127:8888/web/CheckLoginServlet";

//                    发起请求 到数据库检查用户信息
                    final String finalPassword = password;  //得到用户密码
                    new AsyncTask<String, Void, String>() {
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            System.out.println("-------------->"+s);

                            if (s == null) {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
                            }

                            if ("true".equals(s)) {
                                dialog.cancel();
                                saveUserInfo(atuologin, username, password);
                                //                 保存用户信息
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
                            if ("false".equals(s)) {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        protected String doInBackground(String... params) {
                            String httpurl = params[0];
                            try {

                                String reqContent = "name=" + username + "&" + "password=" + finalPassword; //请求的内容

                                System.out.println("------------------>"+reqContent);
//                                创建url对象
                                URL url = new URL(httpurl);
//                                通过url 对象得到 HttpURLConnection 连接
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST"); //设置请求方式

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
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameText.setText("");
                passwordText.setText("");
            }
        });

    }

    protected void initCtrols() {
        login_title = (TextView) findViewById(R.id.login_title);
//        设置登录标题为粗体
        TextPaint textPaint = login_title.getPaint();
        textPaint.setFakeBoldText(true);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);

        submit = (Button) findViewById(R.id.submit);
        reset = (Button) findViewById(R.id.reset);

        rememberMe = (CheckBox) findViewById(R.id.rememberMe);
        dialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
    }

    protected AlertDialog getAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("输入错误！");
        builder.setIcon(R.mipmap.erroricon);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    protected void saveUserInfo(Boolean atuologin, String username, String password) {
        if (rememberMe.isChecked()) {
            long expirationdate = (new Date().getTime()) + (7 * 3600 * 1000);
            shared = getSharedPreferences("rememberme", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean("atuologin", atuologin);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putLong("expirationdate", expirationdate);
            editor.commit();
        }
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

    public void getProgressDialog(ProgressDialog dialog) {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);
        dialog.setTitle("正在登录...");
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
