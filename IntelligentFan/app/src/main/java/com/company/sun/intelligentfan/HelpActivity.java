package com.company.sun.intelligentfan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class HelpActivity extends AppCompatActivity {

    private ImageButton hgo_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        hgo_back = (ImageButton) findViewById(R.id.hgo_back);

        hgo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpActivity.this.finish();
            }
        });
    }
}
