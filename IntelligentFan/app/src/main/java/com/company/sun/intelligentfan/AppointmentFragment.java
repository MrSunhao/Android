package com.company.sun.intelligentfan;

import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.net.URL;
import java.util.Calendar;


/**
 * Created by Sun on 2017/5/16.
 */

public class AppointmentFragment extends Fragment {

    private Calendar c;
    private EditText am_time;
    private DatePicker datePicker;
    private Button am_ok;
    private Button am_close;
    private TextView show_am;
    private View view;

    private String dateTime;

    public AppointmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_appiontment, container, false);
        am_time = (EditText) view.findViewById(R.id.am_time);
        am_time.setText("00:00");
        am_time.setInputType(InputType.TYPE_NULL);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        am_ok = (Button) view.findViewById(R.id.am_ok);
        am_close = (Button) view.findViewById(R.id.am_close);
        show_am = (TextView) view.findViewById(R.id.show_am);

        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        setAmTime(); //设置焦点

        am_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String ht = "00";
                        String mt = "00";
                        if (hourOfDay < 10) {
                            ht = "0" + hourOfDay;
                        } else {
                            ht = "" + hourOfDay;
                        }

                        if (minute < 10) {
                            mt = "0" + minute;
                        } else {
                            mt = "" + minute;
                        }
                        String am_text = ht + ":" + mt;
                        am_time.setText(am_text);
                    }
                }, hour, minute, true).show();
            }
        });

        am_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //提交数据
                int year = datePicker.getYear();
                int mouth = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                String hourAndMinute = am_time.getText().toString();
                if (hourAndMinute == "" || hourAndMinute == null) {
                    am_time.setText("00:00");
                    hourAndMinute = "00:00";
                }
                dateTime = "" + year + "/" + mouth + "/" + day + " " + hourAndMinute;

//                向服务器发送指令 带预约时间
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            Toast.makeText(view.getContext(), "操作失败！请重试！", Toast.LENGTH_SHORT).show();
                        }
                        if (s.equals("true")) {
                            String show_am_text = dateTime + "后将启动！";
                            show_am.setText(show_am_text);
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];

                        try {
                            String reqContent = "action=toa#&dateTime=" + dateTime; //请求的内容

                            URL url = new URL(httpurl);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");

                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                            httpURLConnection.setRequestProperty("charset","utf-8");

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

        am_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                向服务器发送指令  取消预约
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            Toast.makeText(view.getContext(), "操作失败！", Toast.LENGTH_SHORT).show();
                        }
                        if (s.equals("true")) {
                            show_am.setText("");
                            am_time.setText("00:00");
                            Toast.makeText(view.getContext(), "已经取消预约！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {
                            String reqContent = "action=cancletoa#"; //请求的内容

                            URL url = new URL(httpurl);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");

                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(reqContent.length()));
                            httpURLConnection.setRequestProperty("charset","utf-8");

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

    private void setAmTime() {
        am_time.setFocusable(true);
        am_time.setFocusableInTouchMode(true);
        am_time.requestFocus();
        am_time.findFocus();
        am_time.setText("00:00");
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
