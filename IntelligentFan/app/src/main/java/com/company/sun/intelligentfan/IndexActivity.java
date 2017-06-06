package com.company.sun.intelligentfan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

public class IndexActivity extends AppCompatActivity {

    private SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        shared = getSharedPreferences("rememberme", Context.MODE_PRIVATE);
        checkUser();
        this.finish();
    }

    private void checkUser() {
        boolean au = shared.getBoolean("atuologin", false);
        if (au == true) {
            long currentExpirationdate = (new Date().getTime());
            Long expirationdate = shared.getLong("expirationdate", 0);
            if (expirationdate < currentExpirationdate) {
                clearUserInfo();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private void clearUserInfo() {
        SharedPreferences.Editor editor = shared.edit();
        editor.remove("atuologin");
        editor.remove("username");
        editor.remove("password");
        editor.remove("expirationdate");
        editor.commit();
    }
}
