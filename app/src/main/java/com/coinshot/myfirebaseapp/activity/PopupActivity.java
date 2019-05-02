package com.coinshot.myfirebaseapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.coinshot.myfirebaseapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class PopupActivity extends Activity {
    TextView titleTv, contentTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.slide_down, R.anim.fui_slide_in_right);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // 팝업 화면 출력 시 배경이 검게 되지 않음
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_popup);

        titleTv = findViewById(R.id.title);
        contentTv = findViewById(R.id.content);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        titleTv.setText(title);
        contentTv.setText(content);

        // 팝업 사라짐
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 4000);

    }
}
