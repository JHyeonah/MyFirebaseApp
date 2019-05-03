package com.coinshot.myfirebaseapp.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.databinding.ActivityWebviewBinding;

public class WebviewActivity extends AppCompatActivity {
    String url;

    WebView wView;
    ActivityWebviewBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);

        wView = binding.webView;
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        wView.getSettings().setJavaScriptEnabled(true);
        wView.loadUrl(url);
        wView.setWebViewClient(new MyWebViewClient());

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wView.canGoBack()){
                    wView.goBack();
                }
            }
        });
    }

    private static class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("check URL : " , url);
            if(url.matches("https://coinshot.org/.*")){
                view.loadUrl(url);
            }else{
                Toast.makeText(view.getContext(), "코인샷 외부로 이동할 수 없습니다", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && wView.canGoBack()){
            wView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
