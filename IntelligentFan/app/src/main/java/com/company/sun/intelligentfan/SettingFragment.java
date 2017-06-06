package com.company.sun.intelligentfan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sun on 2017/5/16.
 */

public class SettingFragment extends Fragment {
    private TextView reset_pwd;
    private TextView help;
    private TextView exits;
    private SharedPreferences sharedPreferences;
    private View view;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_setting,container,false);
        reset_pwd = (TextView) view.findViewById(R.id.resetpwd);
        help = (TextView) view.findViewById(R.id.help);
        exits = (TextView) view.findViewById(R.id.exit);

        reset_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ResetPwdActivity.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),HelpActivity.class);
                startActivity(intent);
                Toast.makeText(v.getContext(), "帮助文档", Toast.LENGTH_SHORT).show();
            }
        });

        exits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除本地用户信息
                sharedPreferences = v.getContext().getSharedPreferences("rememberme", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("atuologin");
                editor.remove("username");
                editor.remove("password");
                editor.remove("expirationdate");
                editor.commit();
                MainActivity.instance.finish();
                Intent intent = new Intent(v.getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
