package com.coinshot.myfirebaseapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.model.Push;
import com.coinshot.myfirebaseapp.model.Response;
import com.coinshot.myfirebaseapp.service.FCMService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PushActivity extends AppCompatActivity {
    EditText title_et, content_et;
    Button sendBtn, cancelBtn;
    String title, content, token;

    FCMService service;

    public static final String TAG = "LOGIN";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        sendBtn = findViewById(R.id.sendBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        service = retrofit.create(FCMService.class);

        getToken();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_et.getText().toString();
                content = content_et.getText().toString();

                if(title.trim().length() == 0 || content.trim().length() == 0){
                    Toast.makeText(getApplicationContext(), "제목, 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    NetworkTask networkTask = new NetworkTask(service, title, content);
                    networkTask.execute(token);
                    Toast.makeText(getApplicationContext(), "푸시가 발송되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void getToken(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.w(TAG, "getInstanceID failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();

                        Log.d(TAG, token);

                    }
                });
    }

    public static class NetworkTask extends AsyncTask<String, Void, String> {
        private String msg;
        private String title;
        private FCMService service;

        public NetworkTask(FCMService service, String title, String msg){
            this.service = service;
            this.msg = msg;
            this.title = title;
        }
        @Override
        protected String doInBackground(String... tokens) {
            // todo token index
            Push push = new Push(tokens[0], title, msg);

            Log.d(TAG, push.toString());
            Call<Response> call = service.postFCMBody(push);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                    Log.i(TAG, "onResponse: call: "+call);
                    Log.i(TAG, "onResponse: response: "+response);
                }

                @Override
                public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: call failed: "+call, t);
                }
            });
            return null;
        }
    }
}
