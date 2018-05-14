package com.example.henrikfogbunzel.appproject.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.henrikfogbunzel.appproject.ProfileActivity;
import com.example.henrikfogbunzel.appproject.R;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;


    public ProximityIntentReceiver() {
    }

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;



        mContext = context;

        String notifycation_title = mContext.getResources().getString(R.string.notification_title);

        String notifycation_content_text = mContext.getResources().getString(R.string.notification_contextText);

        Boolean entering =  intent.getBooleanExtra(key,false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }


        Intent intentNotification = new Intent(context, ProfileActivity.class);
        intentNotification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        sendNotification("title", "message", intentNotification, 0);

    }

    private void sendNotification(String title, String message, Intent intent, int i) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.RED)
                .setContentTitle(mContext.getString(R.string.notification_title))
                .setContentText(mContext.getString(R.string.notification_contextText));
                //.setContentIntent();


        if(intent!=null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }

}