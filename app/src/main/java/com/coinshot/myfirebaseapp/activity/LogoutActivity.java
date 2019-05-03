package com.coinshot.myfirebaseapp.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.databinding.ActivityLogoutBinding;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {
    private static final int RC_SIGN_OUT = 300;

    ActivityLogoutBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logout);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String type = intent.getStringExtra("type");
        binding.emailText.setText("Email : " + email);

        if(type.equals("google")){
            binding.facebookLoginButton.setVisibility(View.INVISIBLE);
        }else if(type.equals("facebook")){
            binding.signOutBtn.setVisibility(View.INVISIBLE);
        }

        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(RC_SIGN_OUT, intent);
                Toast.makeText(getApplicationContext(), "구글 로그아웃 성공", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "페이스북 로그아웃 성공", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.pushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PushActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
