package com.coinshot.myfirebaseapp.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;

public class PushActivity extends AppCompatActivity {
    EditText title_et, content_et;
    Button sendBtn, cancelBtn;
    String title, content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        sendBtn = findViewById(R.id.sendBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_et.getText().toString();
                content = content_et.getText().toString();

                if(title.trim().length() == 0 || content.trim().length() == 0){
                    Toast.makeText(getApplicationContext(), "제목, 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
