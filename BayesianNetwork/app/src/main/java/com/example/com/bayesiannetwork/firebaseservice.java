package com.example.com.bayesiannetwork;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class firebaseservice extends FirebaseMessagingService {

    String TAG = "firebse service";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
        Log.d("NEW_TOKEN",s);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        getSharedPreferences("bayesiannetwork",MODE_PRIVATE).edit().putString("tokennotif",refreshedToken).commit();
        getSharedPreferences("bayesiannetwork",MODE_PRIVATE).edit().putString("notiftoken",refreshedToken).commit();
        getSharedPreferences("bayesiannetwork",MODE_PRIVATE).edit().putInt("statustoken",0).commit();
        SharedPreferences a =getSharedPreferences("bayesiannetwork", MODE_PRIVATE);
        Log.e(TAG, "tokenservernodejs" + a.getString("tokennotif",""));
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.e("Msgnodejsfrom", "From: " + remoteMessage.getFrom());
        Log.e("Msgnodejsdata", "From: " + remoteMessage.getData());
        Log.e("tokendata",getSharedPreferences("bayesiannetwork",MODE_PRIVATE).getString("tokennotif",""));
       //Log.e("Msgnodejsnotific", "From: " + remoteMessage.getNotification());
        //Log.e("Msgnodejsbody", "From: " + remoteMessage.getNotification().getBody());
        JSONObject notif = new JSONObject(remoteMessage.getData());
        try {
            showNotification(notif.getString("title"),notif.getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showNotification(String title, String content) {


                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                Intent intent=null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("default",
                            "Bayesian Network",
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Bayesian Network");
//                    mNotificationManager.createNotificationChannel(channel);
                    // Configure the notification channel.
                    channel.setDescription("New Notification");
                    channel.enableLights(true);
                    channel.enableVibration(true);

                    if (mNotificationManager != null) {
//                        mNotificationManager.deleteNotificationChannel("default");
                        mNotificationManager.createNotificationChannel(channel);
                    }
//                    else{
//                        mNotificationManager.createNotificationChannel(channel);
//                    }
                }

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                            .setSmallIcon(android.R.drawable.ic_menu_more) // notification icon
                            .setContentTitle(title) // title for notification
                            .setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                            .setContentText(content)
                            .setColor(getResources().getColor(R.color.blue_500))
                            .setGroup(getSharedPreferences("bayesiannetwork",MODE_PRIVATE).getString("level",""))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                            .setAutoCancel(true); // clear notification after click


                    intent = new Intent(getApplicationContext(), a_login.class);

                    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pi);
                    Random rand = new Random();
                    int value = rand.nextInt(2000000);
//                    MediaPlayer mp= MediaPlayer.create(getApplicationContext(),u);
//                    mp.start();
                    mNotificationManager.notify(value, mBuilder.build());
//                    notificationManager.notify(value,mBuilder.build());
    }
}
/**/