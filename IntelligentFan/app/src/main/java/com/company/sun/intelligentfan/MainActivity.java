package com.company.sun.intelligentfan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {

    //UI Object

    private ImageButton home_menu;
    private Switch switch_fan;

    private RadioGroup footer_bar;
    private RadioButton winds_menu;
    private RadioButton appointment_menu;
    private RadioButton timer_menu;
    private RadioButton count_menu;
    private RadioButton setting_menu;
    private ViewPager vpager;
    private String status;
    public static MainActivity instance = null;
    private MyFragmentPagerAdapter mAdapter;
    private SharedPreferences shared;
    private SharedPreferences shared1;


    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;
    public static final int PAGE_FIVE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        winds_menu.setChecked(true);

        home_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpager.setCurrentItem(0);
            }
        });

        String url = "http://123.207.160.127:8888/web/ControlFanServlet";
        new AsyncTask<String,Void,String>(){
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null || "".equals(s)){

                }
                if("1".equals(s)){
                    switch_fan.setChecked(true);
                }
                if("0".equals(s)){
                    switch_fan.setChecked(false);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String httpurl = params[0];
                try {
                    String reqContent = "action=getstatus"; //请求的内容

                    URL url = new URL(httpurl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                    httpURLConnection.setRequestProperty("charset","utf-8");

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
        }.execute(url);

        switch_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "action=turnon#";
                    Toast.makeText(MainActivity.this, "这是开", Toast.LENGTH_SHORT).show();
                } else {
                    status = "action=turnoff#";
                    Toast.makeText(MainActivity.this, "这是关", Toast.LENGTH_SHORT).show();
                }
//                向服务器发送指令
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            Toast.makeText(MainActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
                        }
                        if ("true".equals(s)) {
                            Toast.makeText(MainActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {
                            String reqContent = status; //请求的内容

                            URL url = new URL(httpurl);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                            httpURLConnection.setRequestProperty("charset","utf-8");

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

    private void bindViews() {
        home_menu = (ImageButton) findViewById(R.id.home_menu);
        switch_fan = (Switch) findViewById(R.id.switch_fan);
        footer_bar = (RadioGroup) findViewById(R.id.footer_bar);
        winds_menu = (RadioButton) findViewById(R.id.winds_menu);
        appointment_menu = (RadioButton) findViewById(R.id.appointment_menu);
        timer_menu = (RadioButton) findViewById(R.id.timer_menu);
        count_menu = (RadioButton) findViewById(R.id.count_menu);
        setting_menu = (RadioButton) findViewById(R.id.setting_menu);

        shared = getSharedPreferences("rememberme", Context.MODE_PRIVATE);
        shared1 = getSharedPreferences("rememberUser",Context.MODE_PRIVATE);

        vpager = (ViewPager) findViewById(R.id.vpager);
        instance = this;

        footer_bar.setOnCheckedChangeListener(this);

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.winds_menu:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.appointment_menu:
                vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.timer_menu:
                vpager.setCurrentItem(PAGE_THREE);
                break;
            case R.id.count_menu:
                vpager.setCurrentItem(PAGE_FOUR);
                break;
            case R.id.setting_menu:
                vpager.setCurrentItem(PAGE_FIVE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    winds_menu.setChecked(true);
                    break;
                case PAGE_TWO:
                    appointment_menu.setChecked(true);
                    break;
                case PAGE_THREE:
                    timer_menu.setChecked(true);
                    break;
                case PAGE_FOUR:
                    count_menu.setChecked(true);
                    break;
                case PAGE_FIVE:
                    setting_menu.setChecked(true);
                    break;
            }
        }
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
