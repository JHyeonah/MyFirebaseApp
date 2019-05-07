package com.coinshot.myfirebaseapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinshot.myfirebaseapp.activity.MainActivity;
import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.activity.WebviewActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Timer;
import java.util.TimerTask;

public class FirebaseInstanceIDService extends FirebaseMessagingService {
    final String TAG = "FCM";

    WindowManager wm;
    View mView;
    boolean flag = true;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "FirebaseInstanceIDService : " + s);
        sendRegistrationToServer(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage != null && remoteMessage.getData().size() > 0){
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage){
        final String title = remoteMessage.getData().get("title");
        final String message = remoteMessage.getData().get("message");
        final String url = remoteMessage.getData().get("url");
        String channel = "채널";

        Intent intent;
        if(url != null){
            intent = new Intent(this, WebviewActivity.class);
            intent.putExtra("url", url);
        }else{
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,(int)(System.currentTimeMillis()/1000),intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // 오레오 이후부터 채널을 통해 알림을 여러가지 용도로 나누어 관리
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channel_name = getString(R.string.default_notification_channel_id);

            NotificationManager notichannel = (android.app.NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMsg = new NotificationChannel(channel, channel_name, NotificationManager.IMPORTANCE_HIGH);
            channelMsg.setDescription("채널 설명");
            channelMsg.enableLights(true);
            channelMsg.enableVibration(true);
            channelMsg.setShowBadge(false);
            notichannel.createNotificationChannel(channelMsg);

        }else{
            // 오레오 미만 버전에서는 뷰를 상단에 팝업으로 띄워줌
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                        try{
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            wm = (WindowManager)getSystemService(WINDOW_SERVICE);

                            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.TYPE_TOAST,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                                    PixelFormat.TRANSLUCENT
                            );
                            params.gravity = Gravity.TOP;

                            mView = inflater.inflate(R.layout.activity_popup, null);

                            final TextView titleTv = mView.findViewById(R.id.title);
                            final TextView contentTv = mView.findViewById(R.id.content);
                            RelativeLayout popupLayout = mView.findViewById(R.id.popupLayout);

                            titleTv.setText(title);
                            contentTv.setText(message);

                            popupLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent;
                                    if(url != null){
                                        intent = new Intent(getApplicationContext(), WebviewActivity.class);
                                        intent.putExtra("url", url);
                                    }else{
                                        intent = new Intent(getApplicationContext(), MainActivity.class);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            });
                            wm.addView(mView, params);


                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        wm.removeView(mView);
                                    }
                                }, 3000);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                }
            });

        }
        notificationManager.notify((int)(System.currentTimeMillis()/1000),notificationBuilder.build());
    }

    private void sendRegistrationToServer(String s){
        // 토큰을 서버로 보냄
    }
}
