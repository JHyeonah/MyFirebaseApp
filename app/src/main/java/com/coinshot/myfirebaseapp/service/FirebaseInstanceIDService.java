package com.coinshot.myfirebaseapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.coinshot.myfirebaseapp.activity.MainActivity;
import com.coinshot.myfirebaseapp.R;
import com.coinshot.myfirebaseapp.activity.PopupActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseInstanceIDService extends FirebaseMessagingService {
    final String TAG = "FCM";

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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,(int)(System.currentTimeMillis()/1000),intent,PendingIntent.FLAG_ONE_SHOT);

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String channel = "채널";

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
            // 오레오 미만 버전에서는 activity로 상단에 팝업 띄워줌
            Intent popupIntent = new Intent(getApplicationContext(), PopupActivity.class);
            popupIntent.putExtra("title", title);
            popupIntent.putExtra("content", message);
            popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(popupIntent);
        }
        notificationManager.notify((int)(System.currentTimeMillis()/1000),notificationBuilder.build());
    }

    private void sendRegistrationToServer(String s){
        // 토큰을 서버로 보냄
    }
}
