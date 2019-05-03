package com.coinshot.myfirebaseapp.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.databinding.ActivityMainBinding;
import com.coinshot.myfirebaseapp.model.Push;
import com.coinshot.myfirebaseapp.model.Response;
import com.coinshot.myfirebaseapp.service.FCMService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private static final int RC_SIGN_OUT = 300;

    String title, to;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private CallbackManager callbackManager;

    FCMService service;
    ActivityMainBinding binding;

    public static final String TAG = "LOGIN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(FCMService.class);

        title = "로그인 알림";

        callbackManager = CallbackManager.Factory.create();
        binding.facebookLoginButton.setReadPermissions("email", "public_profile");

        firebaseAuth = FirebaseAuth.getInstance();

        getToken();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Login fail");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        googleSignInClient = GoogleSignIn.getClient(this,gso);

        // 구글 로그인 버튼
        binding.googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // 페이스북 로그인 버튼
        binding.facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess : " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 페이스북 CallbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // 구글 로그인 처리
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                e.printStackTrace();
            }
        }else if(requestCode == RC_SIGN_OUT){
            signOut();
        }
    }

    // 구글 로그인 처리
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG,"firebaseAuthWithGoogle : " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInCredential : success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String email = user.getEmail();
                            String message = "구글로 로그인 했습니다.";
                            NetworkTask networkTask = new NetworkTask(service, title, message);
                            networkTask.execute(to);

                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), LogoutActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("type","google");
                            startActivityForResult(intent,RC_SIGN_OUT);

                        }else{
                            Log.e(TAG, "signInCredential:failure", task.getException());
                        }
                    }
                });
    }

    // 페이스북 로그인 처리
    private void handleFacebookAccessToken(final AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken : " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithCredentialFacebook : success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String email;
                            String message = "페이스북으로 로그인 했습니다.";
                            NetworkTask networkTask = new NetworkTask(service, title, message);
                            networkTask.execute(to);

                            if(user.getEmail() != null){
                                email = user.getEmail();
                            }else{
                                email = "None";
                            }

                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), LogoutActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("type", "facebook");
                            startActivity(intent);

                        }else{
                            Log.e(TAG, "signInWithCredential : failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 디바이스 토큰값 받아옴
    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.w(TAG, "getInstanceID failed", task.getException());
                            return;
                        }
                        to = task.getResult().getToken();

                        Log.d(TAG, to);
                    }
                });
    }

    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                firebaseAuth.signOut();
                if(mGoogleApiClient.isConnected()){
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()){
                                Log.d(TAG, "User logged out");
                            }else{
                                Log.d(TAG,"User log out failed");
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
    }

    public static class NetworkTask extends AsyncTask<String, Void, String>{
        private String msg;
        private String title;
        private FCMService service;

        public NetworkTask(FCMService service, String title, String msg){
            this.msg = msg;
            this.title = title;
            this.service = service;
        }
        @Override
        protected String doInBackground(String... tokens) {
            Push push = new Push(tokens[0], title, msg);

            Log.d(TAG, push.toString());
            Call<Response> call = service.postFCMBody(push);
            call.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(@NonNull Call<Response> call, @NonNull retrofit2.Response<Response> response) {
                    Log.i(TAG, "onResponse: call: " + call);
                    Log.i(TAG, "onResponse: response: " + response);
                }

                @Override
                public void onFailure(@NonNull Call<Response> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: call failed: " + call, t);
                }
            });
            return null;
        }
    }
}
