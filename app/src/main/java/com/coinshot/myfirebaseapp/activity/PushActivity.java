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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushActivity extends AppCompatActivity {
    EditText title_et, content_et;
    Button sendBtn, cancelBtn;
    String title, content, token;

    final String TAG = "LOGIN";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        sendBtn = findViewById(R.id.sendBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        getToken();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_et.getText().toString();
                content = content_et.getText().toString();

                if(title.trim().length() == 0 || content.trim().length() == 0){
                    Toast.makeText(getApplicationContext(), "제목, 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    NetworkTask networkTask = new NetworkTask(title, content);
                    networkTask.execute();
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

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String msg;
        private String title;

        public NetworkTask(String title, String msg){
            this.msg = msg;
            this.title = title;
        }
        @Override
        protected String doInBackground(Void... voids) {
            try{
                // FMC 메시지 생성
                JSONObject root = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("title", title);
                data.put("message", msg);
                root.put("to", token);
                root.put("data", data);

                Log.d(TAG,root.toString());
                URL url = new URL(getString(R.string.fcm_url));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.addRequestProperty("Authorization", "key=" + getString(R.string.server_key));
                con.addRequestProperty("Content-Type","application/json");
                OutputStream os = con.getOutputStream();
                os.write(root.toString().getBytes("utf-8"));
                os.flush();
                con.getResponseCode();
                Log.d(TAG,con.toString());
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
