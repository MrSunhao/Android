package com.company.sun.intelligentfan;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sun on 2017/5/16.
 */

public class CountFragment extends Fragment {
    private LineChart linechart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private ArrayList<String> xVals;
    private ArrayList<Entry> yVals;

    private View view;

    public CountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_count, container, false);
        linechart = (LineChart) view.findViewById(R.id.linechart);

        xVals = new ArrayList<>();
        yVals = new ArrayList<>();

//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//            }
//        };
//        Timer timer = new Timer(true);
//        timer.schedule(timerTask, 1000, 15000);

        //        请求数据
        String httpurl = "http://123.207.160.127:8888/web/ControlFanServlet";
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPostExecute(String s) {
                if ("false".equals(s)) {
                    Toast.makeText(view.getContext(), "没有数据！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (s == null) {
                    Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (s.length() > 0) {

                    //解析json串
                    try {
                        System.out.println("----------------->" + s);
                        JSONObject jsonobj = new JSONObject(s);
                        JSONArray dataArr = jsonobj.getJSONArray("fandata");
                        yVals.add(new Entry(0, 0));
                        for (int i = 0; i < dataArr.length(); i++) {
                            System.out.println("x====" + i + ";" + "y====" + dataArr.get(i));
                            yVals.add(new Entry(i + 1, dataArr.getInt(i))); //坐标点
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    lineDataSet = new LineDataSet(yVals, "风速监测");
                    lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    lineData = new LineData(lineDataSet);

                    Description description = new Description();
                    description.setText("风速");
                    linechart.setDescription(description);

                    XAxis xAxis = linechart.getXAxis();
                    xAxis.setEnabled(true);
                    xAxis.setDrawGridLines(true);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    linechart.setData(lineData);
                }

            }

            @Override
            protected String doInBackground(String... params) {
                String httpurl = params[0];


                try {
                    String reqContent = "action=getws"; //请求的内容

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
