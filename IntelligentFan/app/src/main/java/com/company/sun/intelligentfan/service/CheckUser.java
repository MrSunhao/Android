package com.company.sun.intelligentfan.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sun on 2017/5/12.
 */

public class CheckUser implements Runnable {
    private static final String host = "100.95.235.231";
    private boolean islogin = false;
    private String username;
    private String password;


    public CheckUser() {

    }

    public CheckUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
//        Socket socket = null;
        try {
            //1.创建一个Socket,制定服务器地址和端口
            Socket socket = new Socket(host, 8888);
            //2.获取输出流，向服务器端发送信息
            OutputStream os = socket.getOutputStream(); //字节输出流
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os)); //将输出流包装成打印流

            Map<String, String> map = new HashMap<String, String>();
            map.put("username", username);
            map.put("password", password);

            JSONObject jsonObject = new JSONObject(map);
            String json = jsonObject.toString();

            pw.write(json);
            pw.flush();
            socket.shutdownOutput(); //关闭输出流


            //获取输入流 用于读取客服端相应的信息
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String message ;
            String status = null;

            while ((message = br.readLine()) != null) {
                status = message;
            }

            if (status != null) {
                islogin = Boolean.parseBoolean(status);
            }

            //4.关闭资源
            br.close();
            is.close();

            pw.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean islogin() {
        return islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }
}
