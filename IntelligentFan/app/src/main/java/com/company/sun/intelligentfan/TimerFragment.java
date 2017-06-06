package com.company.sun.intelligentfan;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Sun on 2017/5/16.
 */

public class TimerFragment extends Fragment {

    private TimePicker timePicker;
    private Button timer_ok;
    private Button timer_close;
    private EditText secdate;
    private TextView show_timer;
    private Switch switch_fan;
    private View view;
    private int sec;
    private String times;

    private CountDownTimer countDownTimer = null;

    public TimerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_timer, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        show_timer = (TextView) view.findViewById(R.id.show_timers);
        secdate = (EditText) view.findViewById(R.id.secdate);
        secdate.setText("0");
        timer_ok = (Button) view.findViewById(R.id.timer_ok);
        timer_close = (Button) view.findViewById(R.id.timer_close);
        switch_fan = (Switch) MainActivity.instance.findViewById(R.id.switch_fan);

        timer_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = 0;
                int minute = 0;
                int secs = 0;
                String secs_test = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                secs_test = secdate.getText().toString();
                if (secs_test == null || secs_test == "") {
                    secdate.setText("0");
                    secs = 0;
                } else {
                    secs = Integer.parseInt(secs_test);
                    if (secs > 60) {
                        secs = 60;
                        secdate.setText("60");
                    } else if (secs < 0) {
                        secs = 0;
                        secdate.setText("0");
                    } else {

                    }
                }
                sec = hour * 3600 + minute * 60 + secs;
                times = "" + hour + ":" + minute + ":" + secs;
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";

                if (sec > 0) {
                    new AsyncTask<String, Void, String>() {
                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            System.out.println("time------------------------------------>" + s);
                            if (s == null) {
                                Toast.makeText(view.getContext(), "操作失败！", Toast.LENGTH_SHORT).show();
                            }
                            if ("true".equals(s)) {

                                String message = sec + "秒后即将关闭！";
                                if (countDownTimer == null) {
                                    countDownTimer = new CountDownTimer(sec * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            show_timer.setText(String.valueOf(millisUntilFinished / 1000) + "秒后即将关闭！");
                                        }

                                        @Override
                                        public void onFinish() {
                                            countDownTimer.cancel();
                                            show_timer.setText("");
                                            switch_fan.setChecked(false);
                                        }
                                    };
                                    countDownTimer.start();
                                } else {
                                    countDownTimer.cancel();
                                    countDownTimer.start();
                                }
                                if (sec > 0) {
                                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                                    show_timer.setText(message);
                                }
                            }
                        }

                        @Override
                        protected String doInBackground(String... params) {
                            String httpurl = params[0];
                            try {
                                String reqContent = "action=timing#&times=" + times; //请求的内容

                                URL url = new URL(httpurl);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                                httpURLConnection.setRequestProperty("charset", "utf-8");

                                httpURLConnection.connect();

                                OutputStream os = httpURLConnection.getOutputStream();
                                PrintWriter printWriter = new PrintWriter(os);
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


        timer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                show_timer.setText("");
                secdate.setText("0");
                Toast.makeText(v.getContext(), "取消定时！", Toast.LENGTH_SHORT).show();
//                向服务器发起取消定时指令
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            Toast.makeText(view.getContext(), "操作失败！", Toast.LENGTH_SHORT).show();
                        }
                        if ("true".equals(s)) {
                            Toast.makeText(view.getContext(), "已取消定时！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {
                            String reqContent = "action=canceltime#"; //请求的内容

                            URL url = new URL(httpurl);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                            httpURLConnection.setRequestProperty("charset", "utf-8");

                            httpURLConnection.connect();

                            OutputStream os = httpURLConnection.getOutputStream();
                            PrintWriter printWriter = new PrintWriter(os);
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

        return view;
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
