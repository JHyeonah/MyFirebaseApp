package com.coinshot.myfirebaseapp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.coinshot.myfirebaseapp.Activity.MainActivity;
import com.coinshot.myfirebaseapp.R;
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String channel = "채널";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channel_name = getString(R.string.default_notification_channel_id);

            NotificationManager notichannel = (android.app.NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMsg = new NotificationChannel(channel, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            channelMsg.setDescription("채널 설명");
            channelMsg.enableLights(true);
            channelMsg.enableVibration(true);
            channelMsg.setShowBadge(false);
            notichannel.createNotificationChannel(channelMsg);

        }
        notificationManager.notify(0,notificationBuilder.build());
    }

    private void sendRegistrationToServer(String s){
        // 토큰을 서버로 보냄
    }
}
