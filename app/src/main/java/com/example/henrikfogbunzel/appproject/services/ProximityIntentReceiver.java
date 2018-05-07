package com.example.henrikfogbunzel.appproject.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.henrikfogbunzel.appproject.ProfileActivity;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering =  intent.getBooleanExtra(key,false);
        if (entering) {
            Log.d(getClass().getSimpleName(), "entering");
        }
        else {
            Log.d(getClass().getSimpleName(), "exiting");
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);

        //Notification notification = createNotification();
        //notification.setLatestEventInfo(context,
               // "Proximity Alert!", "You are near your point of interest.", pendingIntent);

      //  notificationManager.notify(NOTIFICATION_ID, notification);

        //Intent intent = new Intent(context, ProfileActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // sendNotification("title", "message", intent, 0);


    }

    private void sendNotification(String title, String message, Intent intent, int i) {
        //Intent notificationIntent = new Intent(ge)
    //    NotificationCompat.Builder  notificationBuilder = new NotificationCompat.Builder()
                //.setSmallIcon();
    }


//    private Notification createNotification() { ;
    // }
}
