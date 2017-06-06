package com.company.sun.intelligentfan;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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


/**
 * Created by Sun on 2017/5/16.
 */

public class WindsFragment extends Fragment {
    private ImageButton addwinds;
    private ImageButton decwinds;
    private TextView windsSpeed;
    private View view;

    public WindsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_winds, container, false);
        addwinds = (ImageButton) view.findViewById(R.id.addwinds);
        decwinds = (ImageButton) view.findViewById(R.id.decwinds);
        windsSpeed = (TextView) view.findViewById(R.id.wind_speed);
        String url = "http://123.207.160.127:8888/web/ControlFanServlet";
        new AsyncTask<String,Void,String>(){
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("pws---------->"+s);
                if(s == null || "".equals(s)){

                }else{
                    windsSpeed.setText(s+"00");
                }
            }

            @Override
            protected String doInBackground(String... params) {

                String httpurl = params[0];
                try {
                    String reqContent = "action=getpws"; //请求的内容
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
        }.execute(url);

        addwinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                向服务器发送指令  带速度值
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        if (s == null) {
                            Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
                        }
                        if ("true".equals(s)) {
                            int ws = Integer.parseInt(windsSpeed.getText().toString());
                            if (ws >= 2000) {
                                Toast.makeText(view.getContext(), "已经达到最大值！", Toast.LENGTH_SHORT).show();
                            } else {
                                ws += 200;
                            }
                            windsSpeed.setText(String.valueOf(ws));
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {

                            String reqContent = "action=iws#"; //请求的内容
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

        decwinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                向服务器发送指令
                String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
                        }
                        if ("true".equals(s)) {
                            int ws = Integer.parseInt(windsSpeed.getText().toString());
                            if (ws <= 0) {
                                Toast.makeText(view.getContext(), "已经达到最小值！", Toast.LENGTH_SHORT).show();
                            } else {
                                ws -= 200;
                            }
                            windsSpeed.setText(String.valueOf(ws));
                        }
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String httpurl = params[0];
                        try {
                            String reqContent = "action=rws#"; //请求的内容
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
