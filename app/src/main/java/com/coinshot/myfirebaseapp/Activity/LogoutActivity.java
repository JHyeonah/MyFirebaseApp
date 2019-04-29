package com.coinshot.myfirebaseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

public class LogoutActivity extends AppCompatActivity {
    TextView emailText;
    Button googleSignOutBtn;
    LoginButton facebookLoginButton;
    private static final int RC_SIGN_OUT = 300;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        emailText = findViewById(R.id.emailText);
        googleSignOutBtn = findViewById(R.id.signOutBtn);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String type = intent.getStringExtra("type");
        emailText.setText("Email : " + email);

        if(type.equals("google")){
            facebookLoginButton.setVisibility(View.INVISIBLE);
        }else if(type.equals("facebook")){
            googleSignOutBtn.setVisibility(View.INVISIBLE);
        }

        googleSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(RC_SIGN_OUT, mainIntent);
                finish();
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();

                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                Toast.makeText(getApplicationContext(), "페이스북 로그아웃 성공", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
